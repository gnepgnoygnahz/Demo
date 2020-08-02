import redis.clients.jedis.Jedis;

/**
 * @ClassName TestBitmap
 * @Description TODO 位存储，只有0和1两个状态,默认0
 * @Author zhangyp
 * @Date 2020/8/2 15:29
 * @Version 1.0
 */
public class TestBitmap {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("zyp-1", 6379);
        jedis.flushAll();
        //只能设置字符串0、1或者true、false，否则报错redis.clients.jedis.exceptions.JedisDataException: ERR bit is not an integer or out of range
        jedis.setbit("sign", 0, true);
        jedis.setbit("sign", 2, "0");
        System.out.println(jedis.getbit("sign", 0));
        System.out.println(jedis.getbit("sign", 1));
        System.out.println(jedis.getbit("sign", 2));
        System.out.println(jedis.bitcount("sign"));
    }
}
