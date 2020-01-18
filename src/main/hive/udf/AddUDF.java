package hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * 自定义hive函数
 * 描述里面中文会乱码
 */
@Description(name = "add",
        value = "add(int a, int b) ===> return a+b \n"
                + "add(int a, int b, int c) ===> return a+b+c",
        extended = "Example:\n"
                + " add(1,1) ==> 2 \n"
                + " add(1,2,3) ==> 6;")
public class AddUDF extends UDF {

    //方法名字只能是这个，手敲，可以实现的父类方法没有这个
    public int evaluate(int a, int b) {
        return a + b;
    }

    public int evaluate(int a, int b, int c) {
        return a + b + c;
    }
}
