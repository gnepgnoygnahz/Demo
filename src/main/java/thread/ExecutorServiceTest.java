package thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;

/*
 * 一、线程池：提供了一个线程队列，队列中保存着所有等待状态的线程。避免了创建与销毁额外开销，提高了响应的速度。
 *
 * 二、线程池的体系结构：
 * 	java.util.concurrent.Executor : 负责线程的使用与调度的根接口
 * 		|--**ExecutorService 子接口: 线程池的主要接口
 * 			|--ThreadPoolExecutor 线程池的实现类
 * 			|--ScheduledExecutorService 子接口：负责线程的调度
 * 				|--ScheduledThreadPoolExecutor ：继承 ThreadPoolExecutor， 实现 ScheduledExecutorService
 *
 * 三、工具类 : Executors
 * ExecutorService Executors.newFixedThreadPool() : 创建固定大小的线程池
 * ExecutorService Executors.newCachedThreadPool() : 缓存线程池，线程池的数量不固定，可以根据需求自动的更改数量。
 * ExecutorService Executors.newSingleThreadExecutor() : 创建单个线程池。线程池中只有一个线程
 *
 * ScheduledExecutorService Executors.newScheduledThreadPool() : 创建固定大小的线程，可以延迟或定时的执行任务。
 * 一个ExecutorService es一共维护这两个队列，一个是任务队列如各种BlockingQueue，另一个是Complete task已完成的任务的队列，任务一结束会被放到这个队列中
 *
 */
public class ExecutorServiceTest {
    public static void main(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
		/* public ThreadPoolExecutor(
		         int corePoolSize,核心线程池大小
                 int maximumPoolSize,线程池内最大线程个数
                 long keepAliveTime,线程保持存活的时间
                 TimeUnit unit,时间单位
                 BlockingQueue<Runnable> workQueue,阻塞队列) */
        //固定线程个数，当任务结束时不死亡，等待任务执行,线程池内始终是3个线程
		/*public static ExecutorService newFixedThreadPool(int nThreads) {
		    return new ThreadPoolExecutor(nThreads, nThreads,
		                                  0L, TimeUnit.MILLISECONDS,
		                                  new LinkedBlockingQueue<Runnable>());
		}*/
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        //不固定线程个数，当任务来临时，创建线程去执行任务，执行完后，若空闲一定时间(等待超时，默认60秒)，将被收回
	/*	public static ExecutorService newCachedThreadPool() {
			  return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
			                                  60L, TimeUnit.SECONDS,
			                                  new SynchronousQueue<Runnable>());
			}*/
        ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
        //只有一个线程，如果该线程异常结束，会产生新的线程去执行任务，可以保证任务的执行顺序与任务的提交顺序一致
		/*public static ExecutorService newSingleThreadExecutor() {
			  return new FinalizableDelegatedExecutorService
			        (new ThreadPoolExecutor(1, 1,
			                                0L, TimeUnit.MILLISECONDS,
			                                new LinkedBlockingQueue<Runnable>()));
			}*/
        ExecutorService v = Executors.newWorkStealingPool();
        //工作窃取,默认启动的线程个数为cpu核数，这些线程都是后台线程.ForkJoinPool看马士兵源码
		/*public static ExecutorService newWorkStealingPool() {
	        return new ForkJoinPool
	            (Runtime.getRuntime().availableProcessors(),//cup核数
	             ForkJoinPool.defaultForkJoinWorkerThreadFactory,
	             null, true);
	    }*/
		/*public ForkJoinPool(int parallelism,
                ForkJoinWorkerThreadFactory factory,
                UncaughtExceptionHandler handler,
                boolean asyncMode)
                {
					this(checkParallelism(parallelism),
						checkFactory(factory),
						handler,
						asyncMode ? FIFO_QUEUE : LIFO_QUEUE,
						"ForkJoinPool-" + nextPoolId() + "-worker-");
					checkPermission();
				}*/

        for (int i = 1; i <= 10; i++) {
            final int task = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i <= 10; i++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + "\tloop" + i + "\ttask" + task);
                    }
                }
            });

        }
        System.out.println("task over");
        fixedThreadPool.shutdown();

        //4秒后执行一次
        Executors.newScheduledThreadPool(3).schedule(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("booming");
            }
        }, 4, TimeUnit.SECONDS);

        //4秒后执行一次，前一次执行开始到下一次执行开始的时间间隔为2秒
        //当时间间隔即执行方法所用时间大于定时器设定的时间间隔时，设定的时间间隔将不起着用，以执行方法所用的时间为准
        Executors.newScheduledThreadPool(3).scheduleAtFixedRate((new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("AtFixedRate booming" + new Date().getSeconds());
            }
        }), 4, 2, TimeUnit.SECONDS);

        //4秒后执行一次，以后前一次执行结束到下一次执行开始的时间间隔为2秒
        Executors.newScheduledThreadPool(3).scheduleWithFixedDelay((new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("WidthFixedDelay booming");
            }
        }), 4, 2, TimeUnit.SECONDS);
    }
}
