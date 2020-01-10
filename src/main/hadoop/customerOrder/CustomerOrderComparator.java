package hadoop.customerOrder;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CustomerOrderComparator extends WritableComparator {
    public CustomerOrderComparator() {
        super(CustomerOrderKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        return super.compare((CustomerOrderKey) a, (CustomerOrderKey) b);
    }
}
