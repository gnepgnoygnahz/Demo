package dealData.process;

import dealData.bean.WebLogBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * drop table if exists ods_webLog_origin;
 * create table ods_webLog_origin(
 * valid string,
 * remote_addr string,
 * remote_user string,
 * time_local string,
 * request string,
 * status string,
 * body_bytes_sent string,
 * http_referer string,
 * http_user_agent string)
 * partitioned by (dateStr string)
 * row format delimited
 * fields terminated by ',';
 */
public class WebLogPreProcess {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        Job job = Job.getInstance(conf);
        job.setJarByClass(WebLogPreProcess.class);
        job.setMapperClass(WebLogPreProcessMapper.class);
        job.setMapOutputValueClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        FileInputFormat.addInputPath(job, new Path("D:\\Money\\data\\access.log"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\Money\\data\\pre"));
        job.setInputFormatClass(TextInputFormat.class);
        job.setNumReduceTasks(0);
        job.waitForCompletion(true);
    }

    static class WebLogPreProcessMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

        //用来存储网站url分类数据
        Set<String> pages = new HashSet<String>();
        Text k = new Text();
        NullWritable v = NullWritable.get();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            pages.add("/wp-includes");
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            WebLogBean webLogBean = WebLogBean.parser(line);
            if (null != webLogBean) {
                WebLogBean.filterStaticResource(webLogBean, pages);
                if (true == webLogBean.isValid()) {
                    k.set(webLogBean.toString());
                    // true,
                    // 194.237.142.21,
                    // -,
                    // 2013-09-18 06:49:18,
                    // /wp-content/uploads/2013/07/rstudio-git3.png,
                    // 304,
                    // 0,
                    // "-",
                    // "Mozilla/4.0(compatible;)"
                    context.write(k, v);
                }
            }
        }
    }
}
