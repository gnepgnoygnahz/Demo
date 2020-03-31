package parse.underwrite;

import bean.common.Key;
import bean.common.PolicyState;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import utils.HiveUtil;

/**
 * @ClassName PolicyStateParse
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/24 22:57
 * @Version 1.0
 */
public class PolicyStateParse extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) {
        return HiveUtil.getSingleStructObjectInspector(PolicyState.class);
    }

    @Override
    public void process(Object[] args) throws HiveException {
        String data = args[0].toString();
        if (!StringUtils.isBlank(data)) {
            forward(data.split(Key.SEPARATORBREAKLINE));
        }
    }

    @Override
    public void close() {

    }
}
