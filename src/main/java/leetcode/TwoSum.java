package leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TwoSum
 * @Description TODO 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 * @Author zhangyp
 * @Date 2020/6/8 23:33
 * @Version 1.0
 */
public class TwoSum {

    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> m = new HashMap();
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (m.containsKey(need)) {
                return new int[]{m.get(need), i};
            }
            m.put(nums[i], i);
        }
        return null;
    }

    public static void main(String[] args) {
        int[] result = twoSum(new int[]{2, 7, 11, 15}, 17);
    }
}
