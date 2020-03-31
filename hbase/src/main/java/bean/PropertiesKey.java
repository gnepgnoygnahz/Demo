package bean;

/**
 * @ClassName PropertiesKey
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/30 0:32
 * @Version 1.0
 */
public class PropertiesKey {
    /**
     * hbase连接池中连接数
     */
    public static final String HBASE_CONNECTIONPOOL_COREPOOLSIZE = "hbase.connectionPool.corePoolSize";
    /**
     * hbase连接池中最大连接数
      */
    public static final String HBASE_CONNECTIONPOOL_MAXIMUMPOOLSIZE = "hbase.connectionPool.maximumPoolSize";
    /**
     * hbase连接池中连接存活时间
     */
    public static final String HBASE_CONNECTIONPOOL_KEEPALIVETIME = "hbase.connectionPool.keepAliveTime";
    /**
     * hbase工作线程等待队列
     */
    public static final String HBASE_CONNECTIONPOOL_QUEUESIZE = "hbase.connectionPool.queueSize";
    /**
     * hbase线程池名称
     */
    public static final String HBASE_CONNECTIONPOOL_THREADNAMEPREFIX = "hbase.connectionPool.threeadNamePrefix";
    /**
     * 调用scan时每次返回几列，如若不设置即每次返回全部列，可能造成内存溢出
     */
    public static final String HBASE_SCAN_BATCH = "hbase.scan.batch";
    /**
     * 调用scan时每次返回几行
     */
    public static final String HBASE_SCAN_CACHING = "hbase.scan.caching";
    /**
     * 获取数据时的字段分隔符
     */
    public static final String HBASE_DATA_SEPARATOR = "hbase.data.separator";


}
