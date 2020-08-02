import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName a
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/4/21 17:25
 * @Version 1.0
 */
public class a {
    public int a() {
        return 1;
    }

    ;

    static {
        a = 3;
    }

    public static int a = 0;

    public static void main(String[] args) throws InterruptedException {
        a = 1;
        System.out.println(++a);
        System.out.println(Math.round(-11.5));
//        int [] array = {21,25,49,25,16,8,25,16,25,25,78,25,31};
//        sort(array, 0, array.length - 1);
//        for (int i : array) {
//            System.out.println(i);
//        }
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        stringStringHashMap.put("1","2");
//        String put = stringStringHashMap.put("1", "3");
//        System.out.println(put);
//        System.out.println((int)(1.2f));
//        System.out.println((int)(1.5f));
//        System.out.println((int)(1.7f));
//        //Thread.sleep(Integer.MAX_VALUE);
//        System.out.println(new String("1").intern() == new String("1").intern());
//        String a = new String("1");
//        String a1 = a.intern();
//        String b = new String("1");
//        String b1 = b.intern();
//        System.out.println(a ==b);
//        System.out.println(a1==b1);
    }

    public static void sort(int[] array, int left, int right) {
        if (left > right) {
            return;
        }
        int base = array[left];
        int i = left, j = right;
        while (i != j) {
            while (array[j] >= base && i < j) {
                j--;
            }

            while (array[i] <= base && i < j) {
                i++;
            }
            if (i < j) {
                int tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
        }
        array[left] = array[i];
        array[i] = base;
        sort(array, left, i - 1);
        sort(array, i + 1, right);
    }

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> m = new HashMap();
        int[] index = new int[2];
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (m.containsKey(need)) {
                index[0] = m.get(nums[i]);
                index[1] = i;
            }
        }
        return index;
    }
}