package jvm;

/**
 * @ClassName StackAllocation
 * @Description TODO 栈上分配,Hotsopt虚拟机并没有实现该功能，当我们开启逃逸分析后有效果是因为标量替换的原因
 * @Author zhangyp
 * @Date 2020/6/7 14:21
 * @Version 1.0
 * -Xmx10 -Xms1Gm -XX:-DoEscapeAnalysis -XX:+PrintGCDetails
 */
public class StackAllocation {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 2000000000; i++) {
            alloc();
        }
        // 查看执行时间
        long end = System.currentTimeMillis();
        System.out.println("花费的时间为： " + (end - start) + " ms");
        // 为了方便查看堆内存中对象个数，线程sleep
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    private static void alloc() {
        User user = new User();//未发生逃逸
    }

    static class User {
    }
}
