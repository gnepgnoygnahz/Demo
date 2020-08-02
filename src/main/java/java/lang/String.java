package java.lang;

/**
 * @ClassName String
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/5/31 21:11
 * @Version 1.0
 */
public class String {
    static {
        System.out.println("LoadClass My String");
    }

    //执行报错找不到main方法，原因就是双亲委派机制导致加载的String类时java的String
    //而java的String类中没有main方法
    public static void main(String[] args) {
        System.out.println("My String");
    }
}
