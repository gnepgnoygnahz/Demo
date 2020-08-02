package thread;

public class WaitAndNotifyTest {
    public static void main(String[] args) {

        final MyThread mt = new MyThread();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 11; i++) {
                    try {
                        mt.product(i);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
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
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}

class MyThread {
    boolean flag;

    public synchronized void product(int i) throws InterruptedException {
        while (!flag) {//while循环会多次判断，if会产生虚假唤醒
            this.wait();
        }

        System.out.println("生产者" + i);
        flag = true;
        notifyAll();
    }

    public synchronized void customer(int i) throws InterruptedException {
        while (flag) {
            this.wait();
        }

        System.out.println("消费者" + i);
        flag = false;
        notifyAll();

    }
}
