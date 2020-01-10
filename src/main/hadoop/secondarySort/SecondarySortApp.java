package hadoop.secondarySort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

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
public class SecondarySortApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        Job job = Job.getInstance(conf);
        job.setJobName("SecondarySort");
        job.setJarByClass(SecondarySortApp.class);

        FileInputFormat.addInputPath(job, new Path("D:\\Money\\data\\a.txt"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\Money\\data\\out"));

        job.setInputFormatClass(TextInputFormat.class);
        job.setMapperClass(SecondarySortMapper.class);
        job.setMapOutputKeyClass(ComboKey.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setReducerClass(SecondarySortReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);

        //设置排序对比器
        job.setSortComparatorClass(ComboKeyComparator.class);
        //设置mapper端聚合器
        //job.setCombinerClass(SecondaryCombiner.class);
        //设置mapper端分区器
        job.setPartitionerClass(SecondarySortPartitioner.class);
        //设置reducer端的分组器
        job.setGroupingComparatorClass(SecondarySortGroupingComparator.class);
        //设置reduce个数
        job.setNumReduceTasks(1);

        job.waitForCompletion(true);

    }
}
