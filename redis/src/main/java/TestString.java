import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName TestString
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/7/25 17:54
 * @Version 1.0
 */
public class TestString {
    public static void main(String[] args) throws InterruptedException {

        Jedis jedis = new Jedis("zyp-1", 6379);
        jedis.flushAll();
        System.out.println("===========增加数据===========");
        //System.out.println(jedis.set("key1", "value1","NX","PX",10000));//2.8.1
        System.out.println(jedis.set("key1", "value1", SetParams.setParams().nx().ex(5)));

        System.out.println(jedis.set("key2", "value2"));
        System.out.println(jedis.set("key3", "value3"));
        System.out.println("删除键key2:" + jedis.del("key2"));
        System.out.println("获取键key2:" + jedis.get("key2"));
        System.out.println("修改key1:" + jedis.set("key1", "value1Changed"));
        System.out.println("获取key1的值：" + jedis.get("key1"));
        System.out.println("在key3后面加入值：" + jedis.append("key3", "End"));
        System.out.println("key3的值：" + jedis.get("key3"));
        System.out.println("获取key3的长度：" + jedis.strlen("key3"));
        System.out.println("增加多个键值对：" + jedis.mset("key01", "value01", "key02", "value02", "key03", "value03"));
        System.out.println("获取多个键值对：" + jedis.mget("8key01", "key02", "key03"));
        System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03", "key04"));
        System.out.println("删除多个键值对：" + jedis.del("key01", "key02"));
        System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03"));

        jedis.flushAll();
        System.out.println("===========新增键值对防止覆盖原先值==============");
        System.out.println(jedis.setnx("key1", "value1"));
        System.out.println(jedis.setnx("key2", "value2"));
        System.out.println(jedis.setnx("key2", "value2-new"));
        System.out.println(jedis.msetnx("key3", "value3", "key2", "value2-new"));//原子操作，要么一起成功要么一起失败
        System.out.println(jedis.get("key1"));
        System.out.println(jedis.get("key2"));
        System.out.println(jedis.get("key3"));

        System.out.println("===========新增键值对并设置有效时间=============");
        System.out.println(jedis.setex("key3", 2, "value3"));
        System.out.println(jedis.get("key3"));
        TimeUnit.SECONDS.sleep(3);

        System.out.println(jedis.get("key3"));

        System.out.println("===========获取原值，更新为新值==========");
        System.out.println(jedis.getSet("key2", "key2GetSet"));
        System.out.println(jedis.get("key2"));

        System.out.println("获得key2的值的字串：" + jedis.getrange("key2", 2, 4));// 0 -1表示全部
        System.out.println("把2G换成3S：" + jedis.setrange("key2", 3, "3S"));
        System.out.println(jedis.get("key2"));

        System.out.println("===========加减设置步长==========");
        System.out.println("设置初始值：" + jedis.set("views", "1"));
        System.out.println("加1：" + jedis.incr("views"));
        System.out.println("获取增加后的值：" + jedis.get("views"));
        System.out.println("减1：" + jedis.decr("views"));
        System.out.println("获取减小后的值：" + jedis.get("views"));
        System.out.println("加3：" + jedis.incrBy("views", 3));
        System.out.println("获取增加后的值：" + jedis.get("views"));
        System.out.println("减4：" + jedis.decrBy("views", 2));
        System.out.println("获取减小后的值：" + jedis.get("views"));
        //当key不存在时会创建并设置个初始值0，在0的基础上进行操作
        System.out.println("减小一个不存在的值：" + jedis.decrBy("views1", 5));


    }
}
