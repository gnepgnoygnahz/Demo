import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestZSet
 * @Description TODO 有序集合按照map的value排序
 * @Author zhangyp
 * @Date 2020/8/2 11:50
 * @Version 1.0
 */
public class TestZSet {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("zyp-1", 6379);
        jedis.flushAll();
        //必须是String, Double，按照double排序
        Map<String, Double> map = new HashMap<>();
        map.put("key1", 1.00);
        map.put("key3", 3.00);
        map.put("key2", 2.00);
        map.put("key4", 4.99);
        System.out.println("以map的形式添加元素：" + jedis.zadd("zset", map));
        System.out.println("添加元素：" + jedis.zadd("zset", 0, "key0"));
        System.out.println("所有元素为：" + jedis.zrange("zset", 0, -1));
        System.out.println("取出2到3位置的值带score：" + jedis.zrangeWithScores("zset", 2, 3));
        System.out.println("取出score在2到3之间的值" + jedis.zrangeByScore("zset", 2, 3));
        System.out.println("取出score在2到3之间的值带score：" + jedis.zrangeByScoreWithScores("zset", 2, 3));
        System.out.println("倒序取出2到3位置的值" + jedis.zrevrange("zset", 2, 3));
        System.out.println("倒序取出2到3位置的值带score：" + jedis.zrevrangeWithScores("zset", 2, 3));
        System.out.println("倒序取出score在2到3之间的值：" + jedis.zrevrangeByScore("zset", 3, 2));
        System.out.println("倒序取出score在2到3之间的值带score：" + jedis.zrevrangeByScoreWithScores("zset", 3, 2));
        System.out.println("元素个数：" + jedis.zcard("zset"));
        System.out.println("移除指定元素：" + jedis.zrem("zset", "key1"));
        System.out.println("按score范围移除元素：" + jedis.zremrangeByScore("zset", 2, 3));
        System.out.println("所有元素：" + jedis.zrange("zset", 0, -1));
        System.out.println("指定score内元素的个数：" + jedis.zcount("zset", 0, 5));
    }
}
