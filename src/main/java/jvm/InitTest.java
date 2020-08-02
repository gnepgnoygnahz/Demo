package jvm;

/**
 * @ClassName Test
 * @Description TODO 类加载
 * @Author zhangyp
 * @Date 2020/5/31 16:56
 * @Version 1.0S
 */

/**
 * 变量的分类：
 * 按照数据类型分：①基本数据类型、②引用数据类型。
 * 按照在类中声明的位置分：
 * ①成员变量：在使用前都经历过初始化赋值
 * 类变量：linking的prepare阶段：给默认变量赋值
 * initial阶段：给类变量显式赋值即静态代码块赋值
 * 实例变量：随着对象的创建，会在堆空间中分配实例变量空间，并进行默认赋值
 * ②局部变量：在使用前，必须要进行显式的赋值，否则编译不通过
 */
public class InitTest {
    public static int a = 1;
    public int b = 2;
    InitTest n = new InitTest();

    //只有类中存在类变量或者静态代码块时才会出现clinit方法
    //一个类只能被加载一次，即即使创建多个对象，clinit方法也只能被调用一次
    static {
        a = 2;
    }

    //init方法是类的构造方法，创建多个对象可以被调用多次
    public InitTest() {
        a = 3;
    }

    public void a() {
        // System.out.println(this.getClass().getResource("").getPath());
        int c = 3;
        String d = "e";
        InitTest t = new InitTest();
    }

    public static void main(String[] args) {
        new InitTest().a();
    }
}
