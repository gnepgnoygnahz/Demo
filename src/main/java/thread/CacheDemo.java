package thread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheDemo {
    private static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    private static ReadWriteLock rwl = new ReentrantReadWriteLock();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 20; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    getDate("hehe");
                }
            }).start();
        }
        Thread.sleep(1000);
    }

    public static void getDate(String key) {
        rwl.readLock().lock();
        Object data1 = null;
        try {
            data1 = cache.get(key);
            if (data1 != null) {
                System.out.println(Thread.currentThread().getName() + "从缓存中读到" + data1);
            }
            if (data1 == null) {
                rwl.readLock().unlock();
                rwl.writeLock().lock();
                if (cache.get(key) == null) {
                    try {
                        Object data2 = null;
                        System.out.println(Thread.currentThread().getName() + "查数据库");
                        data2 = "kobe";// 去数据库查值，效率没有直接从map查询高
                        cache.put(key, data2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        rwl.writeLock().unlock();
                    }
                } else {
                    try {
                        System.out.println(Thread.currentThread().getName() + "从缓存中读到" + cache.get(key));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        rwl.writeLock().unlock();
                    }
                }
                rwl.readLock().lock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwl.readLock().unlock();
        }
    }
}
