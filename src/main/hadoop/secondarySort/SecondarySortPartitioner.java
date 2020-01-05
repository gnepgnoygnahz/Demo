package hadoop.secondarySort;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class SecondarySortPartitioner extends Partitioner<ComboKey, NullWritable> {
    @Override
    public int getPartition(ComboKey comboKey, NullWritable nullWritable, int i) {

        //return comboKey.getYear() % i;
        return 0;
    }
}
