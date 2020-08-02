package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread t1 = new Thread(.......) ;t1.start(t1线程一直拿着锁不会释放)
 * Thread t2 = new Thread(.......) ;t1.start(t2线程申请同一把锁，一直申请不到，会一直等待。
 * 如果t2线程里面有lock.lockInterruptibly()方法时，使用t2.interrupt可以打断线程，让t2不用继续等待，并报异常InterruptException)
 * synchronized默认为不公平锁，ReentrantLock可以指定为公平锁Lock lock = new ReentrantLock(true);效率较非公平锁低
 *
 * @author zhang
 */
public class LockAndCondition {
    final static ExecutorService threadLocal = Executors.newFixedThreadPool(5);
    // 重入锁，可以代替synchronized，jdk1.7之后效率差不多
    final static Lock lock = new ReentrantLock();
    final static Condition conditionput = lock.newCondition();
    final static Condition conditionget = lock.newCondition();
    final static Object[] arr = new Object[10];
    static int putIndex;
    static int getIndex;
    static int count;

    public static void main(String[] args) {
        for (int i = 1; i < 5; i++) {
            threadLocal.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i < 101; i++) {
                        try {
                            Object obj = (int) (Math.random() * 1000);
                            put(obj);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        for (int i = 1; i < 5; i++) {
            threadLocal.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i < 101; i++) {
                        try {
                            get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        threadLocal.shutdown();

        /*
         * new Thread(new Runnable() {
         *
         * @Override
         * public void run() {
         * 		for(int i=1;i<101;i++){
         * 			try {
         * 				Object obj = (int)(Math.random()*1000);
         * 				put(obj);
         * 			} catch (InterruptedException e) {
         * 					e.printStackTrace(); }
         * 			}
         * 		}
         *  }).start();
         */

        /*
         * new Thread(new Runnable() {
         *
         * @Override
         * public void run() {
         * 		for(int i=1;i<101;i++){
         * 			try { get(); }
         * 			catch(InterruptedException e) {
         * 					e.printStackTrace();
         * 			}
         * 		}
         * 	}
         * }).start();
         */
    }

    /**
     * 数组阻塞队列
     *
     * @param obj
     * @throws InterruptedException
     */
    public static void put(Object obj) throws InterruptedException {
        lock.lock();// 相当于synchronized(this),需要是手动释放锁
        try {
            while (count == arr.length) {
                conditionput.await();
            }
            if (++putIndex == arr.length) {
                putIndex = 0;
            }
            arr[putIndex] = obj;
            System.out.println(Thread.currentThread().getName() + "\t放入数据" + obj);
            count++;
            conditionget.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static Object get() throws InterruptedException {
        lock.lock();
        // boolean tryLock = lock.tryLock(3, TimeUnit.SECONDS);尝试获取锁3秒

        Object x = null;
        try {
            while (count == 0) {
                conditionget.await();
            }
            if (++getIndex == arr.length) {
                getIndex = 0;
            }
            x = arr[getIndex];
            System.out.println(Thread.currentThread().getName() + "\t读出数据" + x);
            count--;
            conditionput.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return x;

    }
}
