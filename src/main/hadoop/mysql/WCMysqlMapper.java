package hadoop.mysql;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class WCMysqlMapper extends Mapper<LongWritable, WCMysqlDBWritable, Text, IntWritable> {

    private static final Logger logger = LogManager.getLogger(WCMysqlMapper.class);

    @Override
    protected void map(LongWritable key, WCMysqlDBWritable value, Context context) throws IOException, InterruptedException {
        //key就是记录的条数，从0开始
        logger.info(key.get());
        String[] arr = value.getContent().split("/");
        for (String data : arr) {
            context.write(new Text(data), new IntWritable(1));
        }
    }
}
