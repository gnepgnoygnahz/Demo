package parse.insure;

import bean.common.Key;
import bean.dataBase.insure.Bnf;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import utils.HiveUtil;

/**
 * @ClassName BnfParse
 * @Description TODO 解析Bnf表
 * @Author zhangyp
 * @Date 2020/3/19 23:26
 * @Version 1.0
 */
public class BnfParse extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) {
        return HiveUtil.getSingleStructObjectInspector(Bnf.class);
    }

    @Override
    public void process(Object[] args) throws HiveException {
        String data = args[0].toString();
        if (!StringUtils.isBlank(data)) {
            String[] lines = data.split(Key.SEPARATORTAB);
            for (String line : lines) {
                forward(line.split(Key.SEPARATORBREAKLINE));
            }
        }
    }

    @Override
    public void close() {

    }
}
