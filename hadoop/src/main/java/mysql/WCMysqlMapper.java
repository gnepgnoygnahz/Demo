package mysql;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WCMysqlMapper extends Mapper<LongWritable, WCMysqlDBWritable, Text, IntWritable> {

    @Override
    protected void map(LongWritable key, WCMysqlDBWritable value, Context context) throws IOException, InterruptedException {
        //key就是记录的条数，从0开始
        System.out.println(key.get());
        String[] arr = value.getContent().split("/");
        for (String data : arr) {
            context.write(new Text(data), new IntWritable(1));
        }
    }
}
