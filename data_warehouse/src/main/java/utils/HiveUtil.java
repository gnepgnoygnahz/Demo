package utils;

import bean.common.Key;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HiveUtil
 * @Description TODO hive函数工具类
 * @Author zhangyp
 * @Date 2020/3/18 12:41
 * @Version 1.0
 */
public class HiveUtil {

    /**
     * UDTF函数initialize时返回的数据结构，将对象里的属性一一返回
     * @param c 解析的对象
     * @param <T> 自定义类型
     * @return 返回的数据结构
     */
    public static <T> StructObjectInspector getSingleStructObjectInspector(Class<T> c) {
        List<String> allFieldNames = BeanUtil.getAllFieldName(c);
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<>();
        for (String field : allFieldNames) {
            fieldNames.add(field);
            fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        }
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    /**
     * UDTF函数process时返回的数据
     * @param data
     * @return
     */
    public static String [] getSingleProcess(String data){
           return  data.split(Key.SEPARATORTAB);
    }
}
