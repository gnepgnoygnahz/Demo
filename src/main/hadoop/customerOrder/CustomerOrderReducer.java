package hadoop.customerOrder;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;

public class CustomerOrderReducer extends Reducer<CustomerOrderKey, NullWritable, Text, NullWritable> {

    private static final Logger logger = LogManager.getLogger(CustomerOrderReducer.class);

    @Override
    protected void reduce(CustomerOrderKey key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        Iterator<NullWritable> iterator = values.iterator();
        iterator.next();
        logger.info("========" + key.toString());
        int cid = key.getCid();
        String customerInfo = key.getCustomerInfo();
        while (iterator.hasNext()) {
            iterator.next();
            logger.info("========" + key.toString());
            String orderInfo = key.getOrderInfo();
            context.write(new Text(cid + "===" + customerInfo + "===" + orderInfo), NullWritable.get());
        }
    }
}
