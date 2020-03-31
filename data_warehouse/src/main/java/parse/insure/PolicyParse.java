package parse.insure;

import bean.common.Key;
import bean.dataBase.insure.Policy;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import utils.HiveUtil;

/**
 * @ClassName PolicyParse
 * @Description TODO 解析Policy表
 * @Author zhangyp
 * @Date 2020/3/19 23:06
 * @Version 1.0
 */
public class PolicyParse extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) {
        return HiveUtil.getSingleStructObjectInspector(Policy.class);
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
