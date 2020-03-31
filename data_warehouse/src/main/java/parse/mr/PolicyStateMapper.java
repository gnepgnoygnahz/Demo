package parse.mr;

import bean.common.PolicyState;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @ClassName PolicyStateMapper
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/26 18:38
 * @Version 1.0
 */
public class PolicyStateMapper extends Mapper<LongWritable, Text, Text, PolicyState> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        JSONObject jsonObject = JSON.parseObject(line);
        PolicyState policyState = jsonObject.toJavaObject(PolicyState.class);
        context.write(new Text(policyState.getContNo()), policyState);
    }
}
