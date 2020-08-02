import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestGeospatial
 * @Description TODO 地理位置，南北两极无法添加，经度范围-180~+180，纬度范围-85.05112878~85.05112878
 * @Author zhangyp
 * @Date 2020/8/2 14:16
 * @Version 1.0
 */
public class TestGeospatial {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("zyp-1", 6379);
        jedis.flushAll();
        System.out.println("========添加元素========");
        jedis.geoadd("city", 116.40, 39.90, "北京");
        Map<String, GeoCoordinate> citys = new HashMap<>();
        citys.put("上海", new GeoCoordinate(121.47, 31.23));
        citys.put("重庆", new GeoCoordinate(106.50, 29.53));
        citys.put("深圳", new GeoCoordinate(114.05, 22.53));
        jedis.geoadd("city", citys);
        System.out.println("获取北京、上海位置：" + jedis.geopos("city", "北京", "上海"));
        System.out.println("========以110，30为中心方圆1000km的城市=========");
        //m米 km千米 mi英里 ft英尺
        System.out.println("获取北京、上海之间的距离：" + jedis.geodist("city", "北京", "上海", GeoUnit.KM));
        //以110，30为中心方圆1000km的城市
        jedis.georadius("city", 116.40, 39.90, 1100, GeoUnit.KM).forEach(x -> {
            System.out.println(x.getMemberByString() + "-" + x.getDistance() + "-" + x.getCoordinate());
        });
        System.out.println("=============以北京为中心方圆1000km的城市================");
        //以北京为中心方圆1000km的城市
        jedis.georadiusByMember("city", "北京", 1500, GeoUnit.KM).forEach(x -> {
            System.out.println(x.getMemberByString() + "-" + x.getDistance() + "-" + x.getCoordinate());
        });
        System.out.println("获取北京、上海的hash值：" + jedis.geohash("city", "北京", "上海"));
        //geo底层就是zset
        System.out.println(jedis.zcard("city"));
        System.out.println(jedis.zrange("city", 0, -1));
    }
}
