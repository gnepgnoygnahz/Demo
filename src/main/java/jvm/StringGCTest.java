package jvm;

/**
 * @ClassName StringGCTest
 * @Description TODO String的垃圾回收
 * -Xms15m -Xmx15m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails
 * @Author zhangyp
 * @Date 2020/6/14 16:17
 * @Version 1.0
 * 生成10000个String对象，但内存中却不足100000个，可以从Number of entries=62256看出
 * 循环过程发生过一次GC
 * [GC (Allocation Failure)
 * [PSYoungGen: 4096K->504K(4608K)] 4096K->732K(15872K), 0.0015024 secs]
 * [Times: user=0.00 sys=0.00, real=0.00 secs]
 * 因为intern()方法直接返回字符串常量池中的String了，new出来String对象没被使用，被垃圾回收了
 */
public class StringGCTest {
    public static void main(String[] args) {
        for (int j = 0; j < 100000; j++) {
            String.valueOf(j).intern();
        }
    }
}
