package parse.mr;

import bean.common.PolicyState;
import lombok.extern.log4j.Log4j2;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @ClassName PolicyStateApp
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/26 22:48
 * @Version 1.0
 */
@Log4j2
public class PolicyStateApp {

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        Job job = Job.getInstance(conf);
        job.setJobName("PolicyState");
        job.setJarByClass(PolicyStateApp.class);
        job.setInputFormatClass(TextInputFormat.class);
        //添加输入路径
        FileInputFormat.addInputPath(job, new Path("D:\\home\\zyp\\log\\data_warehouse"));
        //设置输出路径
        FileOutputFormat.setOutputPath(job, new Path("D:\\home\\zyp\\log\\data_warehouse\\out"));


        job.setInputFormatClass(TextInputFormat.class);
        job.setMapperClass(PolicyStateMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(PolicyState.class);

        job.setReducerClass(PolicyStateReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        job.setNumReduceTasks(1);
        job.waitForCompletion(true);
    }
}
