package parse.mr;

import bean.common.Key;
import bean.common.PolicyState;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import utils.BeanUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName PolicyStateReducer
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/26 21:47
 * @Version 1.0
 */
public class PolicyStateReducer extends Reducer<Text, PolicyState, Text, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<PolicyState> values, Context context) throws IOException, InterruptedException {
        List<PolicyState> arrayList = new ArrayList<>();
        for (PolicyState policyState : values) {
            arrayList.add(BeanUtil.copyProperties(policyState,new PolicyState()));
        }
        Collections.sort(arrayList);
        arrayList.get(arrayList.size()-1).setEndDate(Key.MAX_DATE);
        for (PolicyState policyState : arrayList) {
            context.write(new Text(BeanUtil.objToStringWithBreakLine(policyState)), NullWritable.get());
        }
    }
}
