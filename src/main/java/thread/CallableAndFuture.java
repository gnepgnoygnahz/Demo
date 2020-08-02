package thread;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableAndFuture {
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(5);
        Future<Integer> submit = es.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                System.out.println("正在运行callable");
                Thread.sleep(2000);
                return 24;
            }

        });

        try {
            System.out.println("得到结果" + submit.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            /* es.shutdown(); */
        }

        CompletionService<Integer> cs = new ExecutorCompletionService<Integer>(es);//提交一组callable
        for (int i = 1; i <= 10; i++) {
            final int x = i;
            cs.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Thread.sleep(new Random().nextInt(5000));
                    return x;
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            try {
                System.out.println(cs.take().get());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                es.shutdown();
            }
        }

    }
}
