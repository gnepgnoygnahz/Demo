package leetcode;

/**
 * @ClassName ReverseInt
 * @Description TODO 假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−2<<31, 2<<31 − 1]。请根据这个假设，如果反转后整数溢出那么就返回 0。
 * @Author zhangyp
 * @Date 2020/6/9 22:32
 * @Version 1.0
 */
public class ReverseInt {

    public static int reverse(int x) {
        long res = 0;
        while (x != 0) {
            res = res * 10 + x % 10;
            x /= 10;
        }
        return (int) res == res ? (int) res : 0;
    }

    public static void main(String[] args) {
        System.out.println(reverse(1234567));
    }
}
