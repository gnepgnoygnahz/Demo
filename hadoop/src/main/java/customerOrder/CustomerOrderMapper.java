package customerOrder;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class CustomerOrderMapper extends Mapper<LongWritable, Text, CustomerOrderKey, NullWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        String path = fileSplit.getPath().toString();
        System.out.println("========" + path);
        CustomerOrderKey customerOrderKey = new CustomerOrderKey();
        String content = value.toString();
        String[] data = content.split(",");
        if (path.contains("customers")) {
            //1,tom,12
            customerOrderKey.setType(0);
            customerOrderKey.setCid(Integer.parseInt(data[0]));
            customerOrderKey.setCustomerInfo(content);
        } else {
            //1,no001,12.23,1
            customerOrderKey.setType(1);
            customerOrderKey.setCid(Integer.parseInt(data[3]));
            customerOrderKey.setOid(Integer.parseInt(data[0]));
            customerOrderKey.setOrderInfo(content);
        }
        context.write(customerOrderKey, NullWritable.get());
    }
}
