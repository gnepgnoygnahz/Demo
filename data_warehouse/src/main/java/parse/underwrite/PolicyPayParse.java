package parse.underwrite;

import bean.common.Key;
import bean.dataBase.underwrite.PolicyPay;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import utils.HiveUtil;

/**
 * @ClassName PolicyPayParse
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/24 22:55
 * @Version 1.0
 */
public class PolicyPayParse extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) {
        return HiveUtil.getSingleStructObjectInspector(PolicyPay.class);
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
