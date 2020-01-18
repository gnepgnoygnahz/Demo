package hive.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

/**
 * 自定义null值处理函数
 */
public class NvlGenericUDF extends GenericUDF {

    private ObjectInspector[] argumentOIs;
    private GenericUDFUtils.ReturnObjectInspectorResolver returnOIResolver;

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        argumentOIs = objectInspectors;
        //检查参数个数
        if (argumentOIs.length != 2) {
            throw new UDFArgumentException("The operator 'nvl' accepts 2 arguments.");
        }
        returnOIResolver = new GenericUDFUtils.ReturnObjectInspectorResolver(true);
        //检查参数类型
        if (!(returnOIResolver.update(argumentOIs[0]) && returnOIResolver.update(argumentOIs[1]))) {
            throw new UDFArgumentTypeException(2, "The 1st and 2nd args of function NLV should have the same type, "
                    + "but they are different: \"" + argumentOIs[0].getTypeName()
                    + "\" and \"" + argumentOIs[1].getTypeName() + "\"");
        }
        return returnOIResolver.get();
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {
        Object o = returnOIResolver.convertIfNecessary(deferredObjects[0].get(), argumentOIs[0]);
        if (o == null) {
            o = returnOIResolver.convertIfNecessary(deferredObjects[1].get(), argumentOIs[1]);
        }
        return o;
    }

    @Override
    public String getDisplayString(String[] strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("if ");
        sb.append(strings[0]);
        sb.append(" is null ");
        sb.append("returns");
        sb.append(strings[1]);
        return sb.toString();
    }
}
