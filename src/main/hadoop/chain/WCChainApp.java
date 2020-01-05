package hadoop.chain;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WCChainApp {
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        Job job = Job.getInstance(conf);
        job.setJobName("WCChain");
        job.setJarByClass(WCChainApp.class);
        job.setInputFormatClass(TextInputFormat.class);
        /* job.addCacheFile(uri) */// 缓存普通文件到task运行节点的工作目录
        // 将产品表文件缓存到task工作节点的工作目录中去
        job.addCacheFile(new URI("file:/D:/Money/data/Chain/cache.txt"));
        //添加输入路径
        FileInputFormat.addInputPath(job, new Path("D:/Money/data/Chain/wc.txt"));
        //设置输出路径
        FileOutputFormat.setOutputPath(job, new Path("D:/Money/data/Chain/out"));

        ChainMapper.addMapper(job, WCChainMapMapper1.class, LongWritable.class, Text.class, Text.class, Text.class, conf);
        ChainMapper.addMapper(job, WCChainMapMapper2.class, Text.class, Text.class, Text.class, Text.class, conf);
        ChainReducer.setReducer(job, WCChainReducer.class, Text.class, Text.class, Text.class, IntWritable.class, conf);
        ChainReducer.addMapper(job, WCChainReduceMapper.class, Text.class, IntWritable.class, Text.class, IntWritable.class, conf);
        job.setNumReduceTasks(3);
        job.waitForCompletion(true);


    }
}
