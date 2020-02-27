package customerOrder;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class CustomerOrderReducer extends Reducer<CustomerOrderKey, NullWritable, Text, NullWritable> {

    @Override
    protected void reduce(CustomerOrderKey key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        Iterator<NullWritable> iterator = values.iterator();
        iterator.next();
        System.out.println("========" + key.toString());
        int cid = key.getCid();
        String customerInfo = key.getCustomerInfo();
        while (iterator.hasNext()) {
            iterator.next();
            System.out.println("========" + key.toString());
            String orderInfo = key.getOrderInfo();
            context.write(new Text(cid + "===" + customerInfo + "===" + orderInfo), NullWritable.get());
        }
    }
}
