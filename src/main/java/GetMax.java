package java;

import org.junit.Test;

/**
 * Create By Zhangyp
 * Date:  2019/10/26
 * Desc:
 * 1 4 2 7 3 6 9
 * L 1 4 2 7                  													R 3 6 9
 * L 1 4							R 2 7
 * L 1    		R 4			L 2    		R 7
 * GETMAX(0,6)
 * 3ML(0,3)				=>7
 * 1ML(0,1)		=>4
 * 0ML(0,0)=>1
 * 0MR(1,1)=>4
 * MAX     =>4
 * 1MR(2,3)		=>7
 * 2ML(2,2)=>2
 * 2MR(3,3)=>7
 * MAX     =>7
 * MAX				=>7
 * 3MR(4,6)				=>9
 * 5ML(4,5)		=>6
 * 4ML(4,4)=>3
 * 4MR(5,5)=>6
 * MAX     =>6
 * 5MR(6,6)		=>9
 * MAX				=>9
 * MAX						=>9
 */
public class GetMax {

    public static int getMax(int[] arr, int l, int r, String side) {
        if (l == r) {
            System.out.println(side + "======" + arr[l]);
            return arr[l];
        }
        int mid = (l + r) / 2;
        int maxLeft = getMax(arr, l, mid, "left");
        int maxRight = getMax(arr, mid + 1, r, "right");
        System.out.println(side + "====max====" + Math.max(maxLeft, maxRight) + ", l=" + l + ", r=" + r + ", mid=" + mid);
        return Math.max(maxLeft, maxRight);
    }

    public static void main(String[] args) {
        int[] arr = {1, 4, 2, 7, 3, 6, 9};
        System.out.println(getMax(arr, 0, arr.length - 1, "main"));
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void calMoney() {
        int money = 0;
        for (int i = 1; i <= 44; i++) {
            if (money >= 150) {
                money += 2.5;
            } else if (money >= 100) {
                money += 4;
            } else {
                money += 5;
            }
        }
        System.out.println("===============" + money);
    }
}
