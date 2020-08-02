package java.lang;

/**
 * @ClassName MyString
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/5/31 21:32
 * @Version 1.0
 */
public class MyString {
    //报错java.lang.SecurityException: Prohibited package name: java.lang
    //禁用的包名，此类在java.lang包下，因此由BootstrapClassLoader
    //报错以防止对BootstrapClassLoader造成伤害
    public static void main(String[] args) {
        System.out.println(1);
    }
}
