package jvm;

/**
 * @ClassName ParentDelegationMechanismTest
 * @Description TODO 双亲委派机制
 * @Author zhangyp
 * @Date 2020/5/31 21:13
 * @Version 1.0
 */

/**
 * 自己在项目中创建一个java.lang包，在包下创建String类
 * public class String {
 * static {
 * System.out.println("LoadClass My String");
 * }
 * //执行报错找不到main方法，原因就是双亲委派机制导致加载的String类时java的String
 * //而java的String类中没有main方法
 * public static void main(String[] args) {
 * System.out.println("My String");
 * }
 * }
 * 当我们new String()时如果加载的是自定义的String类，
 * 则静态代码块会执行输出LoadClass My String
 * 实际上什么都不会输出，因为此处加载的依然是java的String类
 * 程序运行时想要加载java.lang.String,
 * 会按照AppClassLoader->ExtClassLoader->BootstrapClassLoader
 * 一级一级向上委托最终到达BootstrapClassLoader，
 * BootstrapClassLoader发现java.lang.String是他需要加载的类就直接加载了。
 * 当我们要加载ParentDelegationMechanismTest时，向上传递到BootstrapClassLoader，
 * BootstrapClassLoader发现此类不归他加载，于是又向下传递到ExtClassLoader，
 * ExtClassLoader发现此类也不归他加载，于是又向下传递到AppClassLoader，
 * AppClassLoader发现此类也归他加载，于是就加载此类
 * <p>
 * 同样我们在java.lang包下创建MyString类，运行也报错
 * public class MyString {
 * //报错java.lang.SecurityException: Prohibited package name: java.lang
 * //禁用的包名，此类在java.lang包下，因此由BootstrapClassLoader
 * //报错以防止对BootstrapClassLoader造成伤害
 * public static void main(String[] args) {
 * System.out.println(1);
 * }
 * }
 */
public class ParentDelegationMechanismTest {
    public static void main(String[] args) {
        String s = new String();
        System.out.println(s);
    }
}