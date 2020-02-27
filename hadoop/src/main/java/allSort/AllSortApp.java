package allSort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.TotalOrderPartitioner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;

import java.io.IOException;

/**
 * 作业先经过FileInputFormat切片后由Reader去读数据发送给Mapper
 * Mapper处理数据经过分区器Partitioner分区后发送给Combiner(Combiner跟Reducer一样，都是继承Reducer，只不过是在Mapper阶段实现聚合)
 * Combiner处理完后发送给Reducer端的分组器GroupingComparator(在此之前数据已经排好序了)，该类继承WritableComparator实现compare方法
 * 以此方法将数据分组，分组的意义就是将compare返回值相同的key归为一组，以此来调用一次reduce方法，示例数据如下
 * 1932	49
 * 1979	78
 * 1989	37
 * 2010	27
 * 这4条数据是这个Reducer获取到的数据，GroupingComparator将年份为奇数的归为一组，年份为偶数的归为一组
 * 因此数据读取时
 * 1932	49
 * 会调用一次reduce方法，
 * 然后
 * 1979	78
 * 1989	37
 * 会调用一次reduce方法，注意虽然这两个值只调用一次reduce方法，但是并不代表key值不变，key值是会变的根实际数据是一样的
 * 最后
 * 2010	27
 * 又会调用一次reduce方法，共3次。
 */
public class AllSortApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        Job job = Job.getInstance(conf);
        job.setJobName("AllSort");
        job.setJarByClass(AllSortApp.class);

        FileInputFormat.addInputPath(job, new Path("D:\\Money\\data\\a.seq"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\Money\\data\\out"));


        //设置全排序分区类
        job.setPartitionerClass(TotalOrderPartitioner.class);
        //此时所指定的configuration，必须是你当前job实例的configuration。可直接使用job.getConfiguration()获得当前配置。
        TotalOrderPartitioner.setPartitionFile(job.getConfiguration(), new Path("file:///D:/Money/data/part.lst"));


        // 必须在setInputFormatClass和setMapOutputKeyClass之后将sample数据写入分区文件.不然报错
        // Caused by: java.io.IOException:
        // wrong key class: org.apache.hadoop.io.IntWritable is not class org.apache.hadoop.io.LongWritable
        //hadoop提供的方法来实现全局排序，要求Mapper的输入、输出的key必须保持类型一致，
        //此时，我们需要自定义InputSampler类，添加经过处理的key。

        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setMapOutputKeyClass(IntWritable.class);
        //这两个前后无所谓
        job.setMapperClass(AllSortMapper.class);
        job.setMapOutputValueClass(Text.class);

        // 必须在setNumReduceTasks之后将sample数据写入分区文件（要么不设置）.不然报错Wrong number of partitions in keyset
        // 他会读取Reduce个数来创建采样数据分区策略的文件。如3个分区就会创建两个分区的点，数据如下
        //2004    (null)
        //2034    (null)
        job.setNumReduceTasks(3);

        //设置采样器
        //freq:每个key被选中的概率
        //numSapmple:抽取样本的总数
        //maxSplitSampled:最大采样切片数
        InputSampler.Sampler<IntWritable, IntWritable> sampler = new InputSampler.RandomSampler<>(1, 700, 3);
        InputSampler.writePartitionFile(job, sampler);

        job.setReducerClass(AllSortReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        job.setGroupingComparatorClass(AllSortGroupingComparator.class);

        job.waitForCompletion(true);

    }
}
