package allSort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;


/**
 * Reducer端的分组器GroupingComparator(在此之前数据已经排好序了)，该类继承WritableComparator实现compare方法
 * 以此方法将数据分组，分组的意义就是将compare返回值相同的key归为一组，以此来调用一次reduce方法，示例数据如下
 * 1932	49
 * 1979	78
 * 1989	37
 * 2010	27
 * 这4条数据是这个Reducer获取到的数据，GroupingComparator将年份为奇数的归为一组，年份为偶数的归为一组
 * 因此数据读取时
 * 1932	49
 * 会调用一次reduce方法，
 * 然后
 * 1979	78
 * 1989	37
 * 会调用一次reduce方法，注意虽然这两个值只调用一次reduce方法，但是并不代表key值不变，key值是会变的根实际数据是一样的
 * 最后
 * 2010	27
 * 又会调用一次reduce方法，共3次。
 */
public class AllSortReducer extends Reducer<IntWritable, Text, Text, Text> {

    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values
        ) {
            System.out.println(key.get() + ":" + value.toString());
            context.write(new Text(key.get() + ""), value);
        }

    }

}
