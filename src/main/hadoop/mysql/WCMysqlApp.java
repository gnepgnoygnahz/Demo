package hadoop.mysql;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;

import java.io.IOException;

public class WCMysqlApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        Job job = Job.getInstance(conf);
        job.setJobName("WCMysql");
        job.setJarByClass(WCMysqlApp.class);

        //注意，要使用job的conf
        DBConfiguration.configureDB(job.getConfiguration(),
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://192.168.3.9:3306/print?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai",
                "root",
                "root");
        DBInputFormat.setInput(job,
                WCMysqlDBWritable.class,
                "select content from print_code",
                "select count(1) from print_code");
        DBOutputFormat.setOutput(job, "print_word_count", "word", "count");
        job.setMapperClass(WCMysqlMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(WCMysqlReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(3);

        job.waitForCompletion(true);

    }
}
