import redis.clients.jedis.Jedis;

/**
 * @ClassName TestHyperloglog
 * @Description TODO 基数，即不重复的元素，占用内存固定，2^64不同元素只需12kb内存，但是有0.81%误差。只能计算不重复元素个数，不能取出来
 * @Author zhangyp
 * @Date 2020/8/2 14:51
 * @Version 1.0
 */
public class TestHyperloglog {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("zyp-1", 6379);
        jedis.flushAll();
        jedis.pfadd("key1", "a", "b", "c", "d", "e", "b");
        System.out.println(jedis.pfcount("key1"));
        jedis.pfadd("key2", "d", "e", "b", "f");
        System.out.println(jedis.pfmerge("key3", "key1", "key2"));
        System.out.println(jedis.pfcount("key3"));
    }
}
