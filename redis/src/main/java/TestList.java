import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

/**
 * @ClassName TestList
 * @Description TODO 同java的list，有序集合
 * @Author zhangyp
 * @Date 2020/8/2 11:50
 * @Version 1.0
 */
public class TestList {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("zyp-1", 6379);
        jedis.flushAll();
        System.out.println("===========添加一个list===========");
        jedis.lpush("collections", "ArrayList", "Vector", "Stack", "HashMap", "WeakHashMap", "LinkedHashMap");
        jedis.lpush("collections", "HashSet");
        jedis.lpush("collections", "TreeSet");
        jedis.lpush("collections", "TreeMap");
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));//-1代表倒数第一个元素，-2代表倒数第二个元素,end为-1表示查询全部
        System.out.println("collections区间0-3的元素：" + jedis.lrange("collections", 0, 3));
        jedis.flushAll();
        jedis.rpush("collections", "ArrayList", "Vector", "Stack", "HashMap", "WeakHashMap", "LinkedHashMap");
        jedis.rpush("collections", "HashSet");
        jedis.rpush("collections", "TreeSet");
        jedis.rpush("collections", "TreeMap");
        jedis.rpush("collections", "HashMap");
        //没有rrange
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));//-1代表倒数第一个元素，-2代表倒数第二个元素,end为-1表示查询全部
        System.out.println("collections区间0-3的元素：" + jedis.lrange("collections", 0, 3));
        System.out.println("===============================");
        // 删除列表指定的值 ，第二个参数为删除的个数（有重复时），
        System.out.println("删除指定元素个数：" + jedis.lrem("collections", 2, "HashMap"));
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));
        System.out.println("删除下表0-3区间之外的元素：" + jedis.ltrim("collections", 0, 3));
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));
        System.out.println("collections列表出栈（左端）：" + jedis.lpop("collections"));
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));
        System.out.println("collections添加元素，从列表右端，与lpush相对应：" + jedis.rpush("collections", "EnumMap"));
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));
        System.out.println("collections列表出栈（右端）：" + jedis.rpop("collections"));
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));
        System.out.println("修改collections指定下标1的内容：" + jedis.lset("collections", 1, "LinkedArrayList"));
        //如果不存在会报错redis.clients.jedis.exceptions.JedisDataException: ERR index out of range
        //System.out.println("修改collections指定下标10的内容："+jedis.lset("collections", 10, "LinkedArrayList"));
        //redis.clients.jedis.exceptions.JedisDataException: ERR no such key
        //System.out.println("修改collections10指定下标1的内容："+jedis.lset("collections10", 1, "LinkedArrayList"));
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));
        System.out.println("===============================");
        System.out.println("collections的长度：" + jedis.llen("collections"));
        System.out.println("获取collections下标为2的元素：" + jedis.lindex("collections", 2));
        System.out.println("获取collections下标为20的元素：" + jedis.lindex("collections", 20));
        System.out.println("===============================");
        jedis.lpush("sortedList", "3", "6", "2", "0", "7", "6");
        System.out.println("sortedList排序前：" + jedis.lrange("sortedList", 0, -1));
        System.out.println(jedis.sort("sortedList"));//排序不改变原集合
        System.out.println("sortedList排序后：" + jedis.lrange("sortedList", 0, -1));
        System.out.println(jedis.linsert("sortedList", ListPosition.BEFORE, "6", "8"));//只会在第一个出现的元素前加
        System.out.println(jedis.linsert("sortedList", ListPosition.AFTER, "6", "9"));//只会在第一个出现的元素前加
        System.out.println("sortedList：" + jedis.lrange("sortedList", 0, -1));
        System.out.println("===============================");
        System.out.println(jedis.rpoplpush("sortedList", "sortedList1"));//只有这一种
        System.out.println("sortedList：" + jedis.lrange("sortedList", 0, -1));
        System.out.println("sortedList1：" + jedis.lrange("sortedList1", 0, -1));

    }
}