package chain;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.net.URI;

public class WCChainReducer extends Reducer<Text, Text, Text, IntWritable> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        URI[] cacheFiles = context.getCacheFiles();
        for (URI cacheFile : cacheFiles) {
            System.out.println(cacheFile.getPath());
        }

    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for (Text value : values) {
            count += 1;
        }
        context.write(key, new IntWritable(count));
    }
}
