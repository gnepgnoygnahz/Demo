package utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName ConfigUtil
 * @Description TODO 获取配置文件
 * @Author zhangyp
 * @Date 2020/3/29 23:24
 * @Version 1.0
 */
public class ConfigUtil{

    /**
     * 用于读取和处理资源文件中的信息
     */
    private static Properties properties;

    //TODO 类加载的时候被执行一次
    static {
        //TODO 加载本地配置文件
        String resourcePath = "zyp.properties";
        loadProperties(resourcePath);
    }

    private ConfigUtil() {
    }

    /**
     * 根据指定的元数据加载配置文件
     * @param path 元数据位置
     */
    public static void loadProperties(String path){
        try {
            properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据配置名称获取值，带默认值
     * @param name 配置名称
     * @param defaultValue 默认值
     * @return 配置项时返回配置的值，否则返回默认值
     */
    public static String getProperty(String name, String defaultValue) {
        String value = properties.getProperty(name);
        if (null == value) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 根据配置名称获取值，无默认值，
     * @param name 配置名称
     * @return 有配置项时返回配置的值，无配置项时返回null
     */
    public static String getProperty(String name) {
        return getProperty(name, "");
    }

    public static void main(String[] args) {
        //ConfigUtil.loadProperties("aa.properties");
        System.out.println(ConfigUtil.getProperty("user"));
    }

}
