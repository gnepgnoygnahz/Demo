package jvm;

/**
 * @ClassName HeapSpaceTest
 * @Description TODO jstat -gc PID 或者 -XX:+PrintGCDetails查看堆内存分配和使用情况
 * @Author zhangyp
 * @Date 2020/6/7 10:22
 * @Version 1.0
 * jstat -gc PID 或者 -XX:+PrintGCDetails查看堆内存分配和使用情况
 * -Xms600m -Xmx600m -XX:+PrintGCDetails 注意：设置的堆空间大小并不包括PERM GEN或者METASPACE区域
 * 输出
 * -Xms:575M
 * -Xmx:575M
 * Heap
 * PSYoungGen      total 179200K, used 12288K [0x00000000f3800000, 0x0000000100000000, 0x0000000100000000)
 * eden space 153600K, 8% used [0x00000000f3800000,0x00000000f44001b8,0x00000000fce00000)
 * from space 25600K, 0% used [0x00000000fe700000,0x00000000fe700000,0x0000000100000000)
 * to   space 25600K, 0% used [0x00000000fce00000,0x00000000fce00000,0x00000000fe700000)
 * ParOldGen       total 409600K, used 0K [0x00000000da800000, 0x00000000f3800000, 0x00000000f3800000)
 * object space 409600K, 0% used [0x00000000da800000,0x00000000da800000,0x00000000f3800000)
 * Metaspace       used 3340K, capacity 4496K, committed 4864K, reserved 1056768K
 * class space    used 364K, capacity 388K, committed 512K, reserved 1048576K
 * 设置的600输出只有575，因为PSYoungGen的from区和to区在同一时刻只有一块区域能存数据，因此计算时会少加一块
 */
public class HeapSpaceTest {
    public static void main(String[] args) throws InterruptedException {
        //虚拟机中堆内存总量
        long initialMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        //虚拟机试图使用的最大最大堆内存量
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        System.out.println("-Xms:" + initialMemory + "M");
        System.out.println("-Xmx:" + maxMemory + "M");
        Thread.sleep(10000000);
    }
}
