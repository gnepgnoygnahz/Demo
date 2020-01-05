package hadoop.secondarySort;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class SecondarySortCombiner extends Reducer<ComboKey, NullWritable, ComboKey, NullWritable> {
    @Override
    protected void reduce(ComboKey key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        if (key.getYear() % 2 == 0) {
            context.write(key, NullWritable.get());
        }
    }
}
