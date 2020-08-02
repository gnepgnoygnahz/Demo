import redis.clients.jedis.Jedis;

/**
 * @ClassName TestKey
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/7/25 17:05
 * @Version 1.0
 */
public class TestKey {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("zyp-1", 6379);
        System.out.println("当前库所有的键如下：" + jedis.keys("*"));
        System.out.println("删除当前数据库中的所有key：" + jedis.flushDB());
        System.out.println("删除所有数据库中的所有key：" + jedis.flushAll());
        System.out.println("判断键username是否存在：" + jedis.exists("username"));
        System.out.println("新增<'username','zyp'>的键值对：" + jedis.set("username", "zyp"));//键相同值覆盖
        System.out.println("新增<'password','password'>的键值对：" + jedis.set("password", "123"));
        System.out.println("判断键username是否存在：" + jedis.exists("username"));
        System.out.println("查看键username所存储的值的类型：" + jedis.type("username"));
        System.out.println("删除键password:" + jedis.del("password"));
        System.out.println("判断键password是否存在：" + jedis.exists("password"));
        System.out.println("重命名key：" + jedis.rename("username", "name"));
        System.out.println("取出改后的name：" + jedis.get("name"));
        System.out.println("设置过期时间：" + jedis.expire("name", 4));
        Thread.sleep(2000);
        System.out.println("查看name是否存在：" + jedis.get("name"));
        System.out.println("查看当前key剩余时间：" + jedis.ttl("name"));
        Thread.sleep(3000);
        System.out.println("查看name是否存在：" + jedis.get("name"));
        System.out.println("判断键username是否存在：" + jedis.exists("username"));
        System.out.println("新增<'username1','zyp1'>的键值对：" + jedis.set("username1", "zyp1"));
        System.out.println("新增<'username2','zyp2'>的键值对：" + jedis.set("username2", "zyp2"));
        System.out.println("新增<'username3','zyp3'>的键值对：" + jedis.set("username3", "zyp3"));
        System.out.println("新增<'username4','zyp4'>的键值对：" + jedis.set("username4", "zyp4"));
        System.out.println("新增<'username5','zyp5'>的键值对：" + jedis.set("username5", "zyp5"));
        System.out.println("随机返回key空间的一个：" + jedis.randomKey());
        //当key所在的库与移动的库相同时报错ERR source and destination objects are the same
        //如果目标库已有相同key则移动失败
        System.out.println("将username3移动到3库：" + jedis.move("username3", 3));
        System.out.println("取出username3：" + jedis.get("username3"));
        System.out.println("系统中所有的键如下：" + jedis.keys("*"));
        System.out.println("切换数据库：" + jedis.select(3));//redis有15个数据库编号0-15，默认数据存在0库
        System.out.println("取出username3：" + jedis.get("username3"));
        System.out.println("系统中所有的键如下：" + jedis.keys("*"));


    }
}
