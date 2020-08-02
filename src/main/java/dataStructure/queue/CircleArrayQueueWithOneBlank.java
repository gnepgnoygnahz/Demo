package dataStructure.queue;

import java.util.Scanner;

/**
 * @ClassName CircleArrayQueueWithOneBlank
 * @Description TODO 循环数组，空出一个位置不存数据即实际数据量为数组大小减一
 * @Author zhangyp
 * @Date 2020/5/17 20:07
 * @Version 1.0
 */
public class CircleArrayQueueWithOneBlank {

    /**
     * 队列大小
     */
    private final int size;
    /**
     * 存放数据, 模拟队列
     */
    private final int[] arr;
    /**
     * 队列头部
     */
    private int head;
    /**
     * 队列尾部，end位置不会存储数据
     */
    private int end;

    public CircleArrayQueueWithOneBlank(int size) {
        this.size = size;
        this.head = 0;
        this.end = 0;
        this.arr = new int[size];
    }

    public static void main(String[] args) {

        CircleArrayQueueWithOneBlank queue = new CircleArrayQueueWithOneBlank(3);
        char key;
        Scanner scanner = new Scanner(System.in);//
        boolean loop = true;
        //输出一个菜单
        while (loop) {
            System.out.println("s(show): 显示队列");
            System.out.println("e(exit): 退出程序");
            System.out.println("a(add): 添加数据到队列");
            System.out.println("g(get): 从队列取出数据");
            System.out.println("h(head): 查看队列头的数据");
            key = scanner.next().charAt(0);//接收一个字符
            switch (key) {
                case 's':
                    queue.showQueue();
                    break;
                case 'a':
                    System.out.println("输出一个数");
                    int value = scanner.nextInt();
                    queue.add(value);
                    break;
                case 'g': //取出数据
                    try {
                        int res = queue.get();
                        System.out.printf("取出的数据是%d\n", res);
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'h': //查看队列头的数据
                    try {
                        int res = queue.head();
                        System.out.printf("队列头的数据是%d\n", res);
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'e': //退出
                    scanner.close();
                    loop = false;
                    break;
                default:
                    break;
            }
        }

        System.out.println("程序退出~~");
    }

    /**
     * 判断队列是否满
     */
    private boolean isFull() {
        return (end + 1) % size == head;
    }

    /**
     * 判断队列是否空
     */
    private boolean isEmpty() {
        return head == end;
    }

    /**
     * 添加数据
     */
    private void add(int n) {
        if (isFull()) {
            System.out.println("队列满，不能加入数据~");
            return;
        }
        arr[end] = n;
        end = (end + 1) % size;
    }

    /**
     * 取出数据
     */
    private int get() {
        if (isEmpty()) {
            throw new RuntimeException("队列空，不能取数据");
        }
        int value = arr[head];
        head = (head + 1) % size;
        return value;
    }

    /**
     * 队列中数据的个数
     */
    private int size() {
        return (end + size - head) % size;
    }

    /**
     * 显示队列所有数据
     */
    private void showQueue() {
        if (isEmpty()) {
            System.out.println("队列空的，没有数据~~");
            return;
        }
        for (int i = head; i < head + size(); i++) {
            System.out.printf("arr[%d]=%d\n", i % size, arr[i % size]);
        }
    }

    /**
     * 显示队列的头数据， 注意不是取出数据
     */
    public int head() {
        if (isEmpty()) {
            throw new RuntimeException("队列空的，没有数据~~");
        }
        return arr[head];
    }
}
