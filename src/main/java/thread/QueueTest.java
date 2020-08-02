package thread;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * 阻塞列队间线程通信，实现生产者消费者模式
 *
 * @author zhang
 */
public class QueueTest {
    public static void main(String[] args) {

        final MyThread3 mt = new MyThread3();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 11; i++) {
                    try {
                        mt.product(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 11; i++) {
                    try {
                        mt.customer(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}


/**
 * @author zhang
 * Queue<String> str = new ConcurrentLinkedQueue<>();单向队列，尾部插入，头部取出，无界队列，不阻塞
 * str.offer("hehe");相当于添加
 * str.poll();取出第一个，并从链表内删除
 * str.size();查看大小
 * str.peek();取出第一个，但不删除
 * Queue<String> str = new ConcurrentLinkedDeque<>();双端链表队列，无界队列，直到内存耗完，两头都可以插入和取出
 * 所有方法基本同上，就是offerfirst/last之类的
 * <p>
 * BlockingQueue del = new DelayQueue<>();见马士兵源码
 */
class MyThread3 {
    // 创建两个数组型阻塞列队，都只能放一个值，头部取出元素，尾部插入元素
    BlockingQueue<Integer> bq1 = new ArrayBlockingQueue<Integer>(1);
    BlockingQueue<Integer> bq2 = new ArrayBlockingQueue<Integer>(1);
    //当生产者生产出商品时，不放入队列而是直接找消费者，若找不到会阻塞（特指tq.transfer(1)方法）
    //put add等不会阻塞，会放进队列里
    TransferQueue<Integer> tq = new LinkedTransferQueue<>();
    // 一种特殊的TransferQueue，其中每个 put 必须等待一个 take，反之亦然。同步队列没有任何内部容量，甚至连一个队列的容量都没有。
    //不能用add方法，因为队列没有容量，会报异常 queue full
    //可以用put方法，没有消费者时会阻塞，put方法底层调用的还是transfer方法
    BlockingQueue<String> bq3 = new SynchronousQueue<String>();
    // 链表形式的阻塞列队，头部取出元素，尾部插入元素，无界
    BlockingQueue<Integer> bq4 = new LinkedBlockingQueue<Integer>();

    // 构造代码块，类加载时给bq2放一个值，使得bq2再放值时阻塞
    // 阻塞列队的存取都用三种方式，返回方式不同，详情请见 API中BlockingQueue接口
	/*{
		
		try {
			bq2.put(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/

    public void product(int i) throws InterruptedException {
        // 初始时bq1为空，可以放值
        //bq1.add()当列队满了之后会报异常queue full，
        //bq1.offer()返回值是boolean类型，可用于判断，不阻塞
        //bq1.offer(1, 3, TimeUnit.SECONDS);3秒内一直尝试添加1进入列队，3秒内会阻塞。
		/*bq1.put(1);
		System.out.println("生产者" + i);
		bq2.take();// 初始时bq2满了，通知bq2取值
*/
        Thread.sleep(5000);
        bq3.put("生产者" + i);
        System.out.println("生产者" + i);
    }

    public void customer(int i) throws InterruptedException {
		/*bq2.put(2);// 若bq2已满会阻塞
		System.out.println("消费者" + i);
		bq1.take();*/

        bq3.take();
        System.out.println("消费者" + i);
    }
}
