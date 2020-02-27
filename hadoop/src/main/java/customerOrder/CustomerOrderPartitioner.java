package customerOrder;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class CustomerOrderPartitioner extends Partitioner<CustomerOrderKey, NullWritable> {
    @Override
    public int getPartition(CustomerOrderKey customerOrderKey, NullWritable nullWritable, int numPartitions) {
        return customerOrderKey.getCid() % numPartitions;
    }
}
