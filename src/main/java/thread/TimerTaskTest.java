package thread;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器任务
 * 2秒一次4秒一次，交替执行
 *
 * @author zhang
 */
public class TimerTaskTest {
    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        class MyTimerTask extends TimerTask {
            @Override
            public void run() {
                count = (count + 1) % 2;
                System.out.println("bombing");
                new Timer().schedule(new MyTimerTask(), 2000 * (count + 1));
            }
        }

        new Timer().schedule(new MyTimerTask(), 1000);//先执行这一句

        while (true) {
            System.out.println(new Date().getSeconds());
            Thread.sleep(1000);
        }

    }
}
