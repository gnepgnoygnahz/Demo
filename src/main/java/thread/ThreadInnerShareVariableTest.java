package thread;

import java.util.Random;

public class ThreadInnerShareVariableTest {
    // private static ThreadLocal<MyThreadInstance> tl = new
    // ThreadLocal<MyThreadInstance>();
    // private static MyThreadInstance mti = MyThreadInstance.getThreadInstance();
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyThreadInstance mti = MyThreadInstance.getThreadInstance();
                    mti.setName("kb" + new Random().nextInt());
                    mti.setAge(new Random().nextInt());
                    // System.out.println(mti.toString());
                    System.out.println(Thread.currentThread().getName() + "产生" + mti.getName() + " " + mti.getAge());
                    // tl.set(mti);
                    new A().get();
                    new B().get();
                }
            }).start();
        }
    }

    static class A {

        public void get() {
            MyThreadInstance mti = MyThreadInstance.getThreadInstance();
            // System.out.println(mti.toString());
            System.out.println("A从" + Thread.currentThread().getName() + "得到" + mti.getName() + " " + mti.getAge());
        }
    }

    static class B {
        public void get() {
            MyThreadInstance mti = MyThreadInstance.getThreadInstance();
            // System.out.println(mti.toString());
            System.out.println("B从" + Thread.currentThread().getName() + "得到" + mti.getName() + " " + mti.getAge());
        }
    }
}

class MyThreadInstance {
    private String name;
    private int age;
    // private static MyThreadInstance mti = null;
    private static ThreadLocal<MyThreadInstance> tl = new ThreadLocal<MyThreadInstance>();

    public static MyThreadInstance getThreadInstance() {
        // MyThreadInstance mti = null;
        MyThreadInstance mti = tl.get();
        if (mti == null) {
            mti = new MyThreadInstance();
            tl.set(mti);
        }
        return mti;
    }

    private MyThreadInstance() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "MyThreadInstance [name=" + name + ", age=" + age + "]";
    }

}