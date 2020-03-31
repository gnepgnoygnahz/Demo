package parse.insure;

import bean.common.Key;
import bean.dataBase.insure.Appnt;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import utils.BeanUtil;
import utils.HiveUtil;

/**
 * @ClassName AppntParse
 * @Description TODO 解析Appnt表
 * @Author zhangyp
 * @Date 2020/3/18 1:45
 * @Version 1.0
 */
public class AppntParse extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) {
        return HiveUtil.getSingleStructObjectInspector(Appnt.class);
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

    public static void main(String[] args) throws HiveException {
        String[] split = BeanUtil.objToStringWithBreakLine(new Appnt())
                .split(Key.SEPARATORBREAKLINE);
    }
}
