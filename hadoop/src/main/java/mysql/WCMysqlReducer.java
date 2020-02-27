package mysql;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WCMysqlReducer extends Reducer<Text, IntWritable, WCMysqlDBWritable, NullWritable> {
    int count = 0;

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        for (IntWritable value : values) {
            count += value.get();
        }
        WCMysqlDBWritable keyOut = new WCMysqlDBWritable();
        keyOut.setWord(key.toString());
        keyOut.setCount(count);
        context.write(keyOut, NullWritable.get());
    }
}
