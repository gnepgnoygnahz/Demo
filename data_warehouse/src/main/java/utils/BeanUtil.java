package utils;

import java.util.*;

import bean.common.Key;
import bean.dataBase.insure.Appnt;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName BeanUtil
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/16 13:19
 * @Version 1.0
 */
public class BeanUtil {
    /**
     * 将一个集合中对象的值拷贝到另一个对象，属性相同即赋值
     *
     * @param source 源数据，将此对象数据取出
     * @param target 目标对象，将取出的数据赋值到该对象中
     * @param <T>    源数据类型
     * @param <E>    目标数据类型
     * @return 被赋值后的目标对象
     */
    public static <T, E> E copyProperties(T source, E target) {

        // 判断传入源数据是否为空，如果空，则抛自定义异常
        if (null == source) {
            return null;
        }

        // 获取源对象的类的详情信息
        Class<?> sClass = source.getClass();
        // 获取源对象的所有属性
        Field[] sFields = sClass.getDeclaredFields();
        // 获取目标对象的类的详情信息
        Class<?> tClass = target.getClass();
        // 获取目标对象的所有属性
        Field[] tFields = tClass.getDeclaredFields();

        // 循环取到源对象的单个属性
        for (Field sField : sFields) {
            // 循环取到目标对象的单个属性
            for (Field tField : tFields) {
                // 判断源对象的属性名、属性类型是否和目标对象的属性名、属性类型一致
                if (sField.getName().equals(tField.getName()) && sField.getGenericType().equals(tField.getGenericType())) {
                    try {
                        // 获取源对象的属性名，将属性名首字母大写，拼接如：getUsername、getId的字符串
                        String sName = sField.getName();
                        char[] sChars = sName.toCharArray();
                        sChars[0] -= 32;
                        String sMethodName = "get" + String.valueOf(sChars);
                        // 获得属性的get方法
                        Method sMethod = sClass.getMethod(sMethodName);
                        // 调用get方法
                        Object sFieldValue = sMethod.invoke(source);

                        // 获取目标对象的属性名，将属性名首字母大写，拼接如：setUsername、setId的字符串
                        String tName = tField.getName();
                        char[] tChars = tName.toCharArray();
                        tChars[0] -= 32;
                        String tMethodName = "set" + String.valueOf(tChars);
                        // 获得属性的set方法
                        Method tMethod = tClass.getMethod(tMethodName, tField.getType());
                        // 调用方法，并将源对象get方法返回值作为参数传入
                        tMethod.invoke(target, sFieldValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 返回目标对象
        return target;
    }

    /**
     * 将对象值输出位字符串，\t 分割
     *
     * @param object 传入的对象
     * @return 对象字符串格式
     */
    public static String objToStringWithBreakLine(Object object) {
        // 获取源对象的类的详情信息
        Class<?> sClass = object.getClass();
        // 获取源对象的所有属性
        Field[] fields = sClass.getDeclaredFields();
        StringBuilder obj = new StringBuilder();
        try {
            // 循环取到源对象的单个属性
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String sName = field.getName();
                char[] sChars = sName.toCharArray();
                sChars[0] -= 32;
                String sMethodName = "get" + String.valueOf(sChars);
                // 获得属性的get方法
                Method method = sClass.getMethod(sMethodName);
                // 调用get方法
                Object fieldValue = method.invoke(object);
                if (i == fields.length - 1) {
                    obj.append(fieldValue);
                } else {
                    obj.append(fieldValue).append(Key.SEPARATORBREAKLINE);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return obj.length() > 0 ? obj.toString() : "";
    }


    public static <T> List<String> getAllFieldName(Class<T> c) {
        List<String> fieldName = new ArrayList<>();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            fieldName.add(field.getName());
        }
        return fieldName;
    }

    public static void main(String[] args) {
        String fieldName="";
        Field[] fields = Appnt.class.getDeclaredFields();
        for (Field field : fields) {
            fieldName+=(field.getName()+",");
        }
        System.out.println(fieldName);
    }
}