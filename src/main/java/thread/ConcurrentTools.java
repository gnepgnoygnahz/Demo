package thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class ConcurrentTools {

    static ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        exchangrTest();
    }

    /**
     * 信号灯，每次有3个信号灯原，只能有3个线程拿到灯执行任务，但是没同步 单个信号灯可实现同步
     */
    public static void SemaphoreTest() {
        final Semaphore sp = new Semaphore(1, true);// true代表当有信号灯释放后，先等待的线程先获得信号灯，而不是随机获取
        for (int i = 1; i < 11; i++) {
            es.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        sp.acquire();// 获得信号灯
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(
                            Thread.currentThread().getName() + "进入,当前已有" + (1 - sp.availablePermits()) + "个并发");
                    // System.out.println(Thread.currentThread().getName()+"即将离开");
                    sp.release();// 释放信号灯
                    // 下面代码有时候执行不准确，因为其没有和上面的代码合成原子单元
                    System.out.println(
                            Thread.currentThread().getName() + "已离开，当前已有" + (1 - sp.availablePermits()) + "个并发");
                }
            });
        }
        es.shutdown();
    }

    /**
     * 只要有线程没有到达，所有线程都在等待，只有当所有线程都到达后，才能继续进行
     */
    public static void cyclicBarrierTest() {

        final CyclicBarrier cb = new CyclicBarrier(3);

        for (int i = 1; i < 4; i++) {
            es.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep((int) (Math.random() * 10000));
                        System.out.println(Thread.currentThread().getName() + "到达集合地点1，当前已有"
                                + (cb.getNumberWaiting() + 1) + "个已经到达" + (cb.getNumberWaiting() == 2 ? ",到齐了出发" : ""));
                        cb.await();// 线程在此等待
                        Thread.sleep((int) (Math.random() * 10000));
                        System.out.println(Thread.currentThread().getName() + "到达集合地点2，当前已有"
                                + (cb.getNumberWaiting() + 1) + "个已经到达" + (cb.getNumberWaiting() == 2 ? ",到齐了出发" : ""));
                        cb.await();
                        Thread.sleep((int) (Math.random() * 10000));
                        System.out.println(Thread.currentThread().getName() + "到达集合地点3，当前已有"
                                + (cb.getNumberWaiting() + 1) + "已经到达");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        es.shutdown();
    }

    /**
     * 计数器，当计数器被减为0时，线程可继续运行
     */
    public static void countDowdLatchTest() {
        final CountDownLatch cdl1 = new CountDownLatch(1);
        final CountDownLatch cdl2 = new CountDownLatch(3);

        for (int i = 1; i < 4; i++) {
            es.execute(new Runnable() {

                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "正在等待命令");
                    try {
                        cdl1.await();// 线程等待，另一个线程运行时调用countDown()方法，使计数器减一，当计数器为0时继续运行
                        System.out.println(Thread.currentThread().getName() + "接收到命令");
                        Thread.sleep((int) (Math.random() * 10000));
                        System.out.println(Thread.currentThread().getName() + "回应结果");
                        cdl2.countDown();// 计数器cdl2减一
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        es.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep((int) (Math.random() * 10000));
                    System.out.println(Thread.currentThread().getName() + "即将发布命令");
                    cdl1.countDown();
                    System.out.println(Thread.currentThread().getName() + "已经发送命令，等待结果");
                    cdl2.await();
                    System.out.println(Thread.currentThread().getName() + "得到结果");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        es.shutdown();
    }

    /**
     * 两个线程交换数据 当两个线程都到达时，交换数据才开始进行
     */
    public static void exchangrTest() {

        final Exchanger<String> e = new Exchanger<String>();

        es.execute(new Runnable() {

            @Override
            public void run() {
                String data1 = "kobe";
                System.out.println(Thread.currentThread().getName() + "正在把数据" + data1 + "换出去");
                String data2 = null;
                try {
                    Thread.sleep((int) (Math.random() * 10000));// 使每个线程到达的时间不同
                    data2 = e.exchange(data1);// 将data1交换出去，得到的值赋值给data2
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "换回的数据为" + data2);
            }
        });

        es.execute(new Runnable() {

            @Override
            public void run() {
                String data1 = "bryant";
                System.out.println(Thread.currentThread().getName() + "正在把数据" + data1 + "换出去");
                String data2 = null;
                try {
                    Thread.sleep((int) (Math.random() * 10000));
                    data2 = e.exchange(data1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "换回的数据为" + data2);
            }
        });
        es.shutdown();
    }
}
