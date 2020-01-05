package hadoop.secondarySort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SecondarySortMapper extends Mapper<LongWritable, Text, ComboKey, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] data = value.toString().split(" ");
        ComboKey comboKey = new ComboKey(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
        context.write(comboKey, NullWritable.get());
    }
}
