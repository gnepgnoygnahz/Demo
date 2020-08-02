package thread;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 多个读锁不互斥，读锁与写锁、写锁与写锁互斥
 *
 * @author zhang
 */
public class ReadAndWriteLock {
    public static void main(String[] args) {
        final MyThreadTest1 mt1 = new MyThreadTest1();
        for (int i = 1; i <= 3; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        mt1.read();
                    }
                }
            }).start();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    mt1.write(new Random().nextInt(1000));
                }
            }
        }).start();
    }
}

class MyThreadTest1 {
    private int i = 0;
    ReadWriteLock rwl = new ReentrantReadWriteLock();

    public void read() {
        rwl.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\tread" + i);
            Thread.sleep(100);
            System.out.println(Thread.currentThread().getName() + "\tread" + i + "over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwl.readLock().unlock();
        }
    }

    public void write(int i) {
        rwl.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\twrite" + i);
            Thread.sleep(100);
            this.i = i;
            System.out.println(Thread.currentThread().getName() + "\twrite" + i + "over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwl.writeLock().unlock();
        }
    }
}