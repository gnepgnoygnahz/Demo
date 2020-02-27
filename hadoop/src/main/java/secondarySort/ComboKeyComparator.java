package secondarySort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class ComboKeyComparator extends WritableComparator {
    protected ComboKeyComparator() {
        super(ComboKey.class);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        return super.compare((ComboKey) a, (ComboKey) b);
    }
}
