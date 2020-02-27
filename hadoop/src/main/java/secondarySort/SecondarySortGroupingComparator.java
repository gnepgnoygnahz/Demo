package secondarySort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * WritableComparatorM没有泛型，需初始化父类指定泛型，不然报错。
 * 实现compare方法，以此方法将数据分组，分组的意义就是将compare返回值相同的key归为一组，以此来调用一次reduce方法
 */
public class SecondarySortGroupingComparator extends WritableComparator {
    protected SecondarySortGroupingComparator() {
        super(ComboKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        return (((ComboKey) a).getYear() - ((ComboKey) b).getYear()) % 2;
    }
}
