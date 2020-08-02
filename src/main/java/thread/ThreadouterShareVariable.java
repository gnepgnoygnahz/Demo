package thread;

public class ThreadouterShareVariable {


    public static void main(String[] args) {

        final MyThreadTest mt = new MyThreadTest();
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    mt.A();
                }
            }
        }).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    mt.B();
                }
            }
        }).start();
    }


}

class MyThreadTest {
    private static int x = 1;

    public synchronized void A() {
        x++;
        System.out.println(x);
    }

    public synchronized void B() {
        x--;
        System.out.println(x);
    }
}
