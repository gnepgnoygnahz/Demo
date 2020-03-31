package utils;

import bean.PropertiesKey;
import lombok.extern.log4j.Log4j2;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName HbaseUtil
 * @Description TODO Hbase操作工具类
 * @Author zhangyp
 * @Date 2020/3/29 22:42
 * @Version 1.0
 */
@Log4j2
public class HbaseUtil {

    private static volatile Connection connection = null;

    private static volatile Admin admin = null;

    /*
     * 初始化数据：配置信息；获取 connection 对象
     */
    static {
        Configuration configuration = HBaseConfiguration.create();
        //configuration.set("hbase.zookeeper.quorum", "zyp-1:2181,zyp-2:2181,zyp-3:2181");
        try {
            connection = ConnectionFactory.createConnection(
                    configuration,
                    new ThreadPoolExecutor(Integer.parseInt(ConfigUtil.getProperty(PropertiesKey.HBASE_CONNECTIONPOOL_COREPOOLSIZE)),
                            Integer.parseInt(ConfigUtil.getProperty(PropertiesKey.HBASE_CONNECTIONPOOL_MAXIMUMPOOLSIZE)),
                            Integer.parseInt(ConfigUtil.getProperty(PropertiesKey.HBASE_CONNECTIONPOOL_KEEPALIVETIME)),
                            TimeUnit.MINUTES,
                            new LinkedBlockingQueue<>(Integer.parseInt(ConfigUtil.getProperty(PropertiesKey.HBASE_CONNECTIONPOOL_QUEUESIZE))),
                            new ThreadFactoryImpl(ConfigUtil.getProperty(PropertiesKey.HBASE_CONNECTIONPOOL_THREADNAMEPREFIX))
                    )
            );
        } catch (IOException e) {
            log.error("Create connection or admin error! " + e.getMessage(), e);
        }
    }

    private HbaseUtil() {
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Admin getAdmin() {
        try {
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return admin;
    }

    /**
     * 初始化命名空间：若命名空间存在则不创建
     *
     * @param namespace 命名空间
     */
    public static void createNameSpace(String namespace) throws IOException {
        try {
            admin = connection.getAdmin();
            //admin.getNamespaceDescriptor(namespace);
            log.error("NameSpace {} is exist!", namespace);
        } catch (NamespaceNotFoundException e) {
            admin.createNamespace(NamespaceDescriptor.create(namespace).build());
            log.info("Created namespace: {}", namespace);
        }
    }

    /**
     * 删除表：先禁用再删除
     *
     * @param name 表名
     * @throws IOException io操作
     */
    public static void deleteTable(String name) throws IOException {
        TableName tableName = getTableName(name);
        admin = connection.getAdmin();
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
        log.info("Deleted table {} !", name);
    }

    /**
     * 创建表：表存在时，先删除再创建；
     * 分区数为默认
     *
     * @param tableName    表名
     * @param columnFamily 列族
     * @throws IOException io操作
     */
    public static void createTable(String tableName, String columnFamily) throws IOException {
        createTable(tableName, 0, Collections.singletonList(columnFamily));
    }

    /**
     * 创建表：表存在时，先删除再创建；
     * 分区数为默认
     *
     * @param tableName    表名
     * @param columnFamily 列族
     * @throws IOException io操作
     */
    public static void createTable(String tableName, List<String> columnFamily) throws IOException {
        createTable(tableName, 0, columnFamily);
    }

    /**
     * 创建表：表存在时，先删除再创建
     *
     * @param tableName    表名
     * @param regionCount  分区数
     * @param columnFamily 列族
     * @throws IOException io操作
     */
    public static void createTable(String tableName, int regionCount, List<String> columnFamily) throws IOException {
        TableName name = getTableName(tableName);
        admin = connection.getAdmin();
        // 存在
        if (admin.tableExists(name)) {
            log.error("Table named {} already exist!", name);
            deleteTable(tableName);
        }

        createTableTemplate(name, regionCount, columnFamily);
    }

    /**
     * 创建表
     *
     * @param tableName    表名
     * @param regionCount  分区数
     * @param columnFamily 列族
     */
    private static void createTableTemplate(TableName tableName, int regionCount, List<String> columnFamily) {
        try {
            HTableDescriptor htd = new HTableDescriptor(tableName);

            for (String familyName : columnFamily) {
                HColumnDescriptor hcd = new HColumnDescriptor(familyName);
                htd.addFamily(hcd);
            }
            admin = connection.getAdmin();
            // 无分区（未指定）
            if (regionCount <= 0) {
                admin.createTable(htd);
            } else {
                // 预分区
                byte[][] splitKey = getSplitKeys(regionCount);
                admin.createTable(htd, splitKey);
            }
            log.info("Created table named {}", tableName);
        } catch (IOException e) {
            log.error("Create table error, " + e.getMessage(), e);
        }
    }

    /**
     * 获取表对象
     *
     * @param tableName 表名
     * @return 表对象
     * @throws IOException
     */
    public static Table getTable(String tableName) throws IOException {
        TableName name = getTableName(tableName);
        return connection.getTable(name);

    }

    /**
     * 获取表名称对象
     *
     * @param tableName 表名
     * @return 表名称对象
     */
    public static TableName getTableName(String tableName) {
        return TableName.valueOf(tableName);
    }

    /**
     * 表是否存在
     *
     * @param tableName 表名
     */
    public static boolean tableExists(String tableName) throws IOException {
        TableName name = getTableName(tableName);
        return getAdmin().tableExists(name);
    }


    /**
     * 插入数据：单行、单列族 => 多列多值
     *
     * @param tableName    表名
     * @param rowKey       行
     * @param columnFamily 列族
     * @param columns      列
     * @param values       值(与列一一对应)
     */
    public static void insert(String tableName, String rowKey, String columnFamily, Map<String, String> columns) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        for (Map.Entry<String, String> kvEntry : columns.entrySet()) {
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(kvEntry.getKey()), Bytes.toBytes(kvEntry.getValue()));
        }
        put(tableName, put);
    }

    /**
     * 插入数据：单行、单列族 => 单列单值
     *
     * @param tableName    表名
     * @param rowKey       行
     * @param columnFamily 列族
     * @param column       列名
     * @param value        列值
     */
    public static void insert(String tableName, String rowKey, String columnFamily, String column, String value) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(
                Bytes.toBytes(columnFamily),
                Bytes.toBytes(column),
                Bytes.toBytes(value));
        put(tableName, put);
    }

    public static void put(String tableName, Put put) throws IOException {
        getTable(tableName).put(put);
    }

    /**
     * 批量插入
     *
     * @param tableName 表名
     * @param puts      多个put
     * @param skipWAL   是否跳过写前日志
     * @throws IOException
     * @throws InterruptedException
     */
    public static void bigInsert(String tableName, List<Put> puts) throws IOException, InterruptedException {
        bigInsert(tableName, puts, false);
    }

    /**
     * 批量插入
     *
     * @param tableName 表名
     * @param puts      多个put
     * @param skipWAL   是否跳过写前日志
     * @throws IOException
     * @throws InterruptedException
     */
    public static void bigInsert(String tableName, List<Put> puts, boolean skipWAL) throws IOException, InterruptedException {
        Table table = getTable(tableName);
        if (skipWAL) {
            //整表关闭写前日志
            table.getTableDescriptor().setDurability(Durability.SKIP_WAL);
        }
        //批量插入，每执行一个Put就要想区域服务器发一个请求，这种就会将Put添加到写缓冲，等到写缓冲都填满的时候才会发起请求
        table.batch(puts, new Object[puts.size()]);

        /*List<Row> action = new ArrayList<>();
        for (Put put : puts) {
            put.setDurability(Durability.SKIP_WAL);
            action.add(put);
        }
        getTable(tableName).batch(action, new Object[action.size()]);*/
    }

    /**
     * 删除一行数据
     *
     * @param tableName 表名
     * @param rowKey    行名
     */
    public static void deleteRow(String tableName, String rowKey) throws IOException {
        delete(tableName, new Delete(rowKey.getBytes()));
    }

    /**
     * 删除多行数据
     *
     * @param tableName 表名
     * @param rowKey    行名
     */
    public static void deleteRow(String tableName, List<String> rowKeys) throws IOException {
        List<Delete> deletes = new ArrayList<>();
        for (String rowKey : rowKeys) {
            deletes.add(new Delete(rowKey.getBytes()));
        }
        delete(tableName, deletes);
    }

    /**
     * 删除单行单列族记录
     *
     * @param tableName    表名
     * @param rowKey       行名
     * @param columnFamily 列族
     */
    public static void deleteColumnFamily(String tableName, String rowKey, String columnFamily) throws IOException {
        delete(tableName, new Delete(rowKey.getBytes()).addFamily(Bytes.toBytes(columnFamily)));
    }

    /**
     * 删除单行多列族记录
     *
     * @param tableName    表名
     * @param rowKey       行名
     * @param columnFamily 列族
     */
    public static void deleteColumnFamily(String tableName, String rowKey, List<String> columnFamilys) throws IOException {
        List<Delete> deletes = new ArrayList<>();
        for (String columnFamily : columnFamilys) {
            deletes.add(new Delete(rowKey.getBytes()).addFamily(Bytes.toBytes(columnFamily)));
        }
        delete(tableName, deletes);
    }

    /**
     * 删除单行单列族单列记录
     *
     * @param tableName    表名
     * @param rowKey       行名
     * @param columnFamily 列族
     * @param column       列
     */
    public static void deleteColumn(String tableName, String rowKey, String columnFamily, String column) throws IOException {
        delete(tableName, new Delete(rowKey.getBytes()).addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column)));
    }

    /**
     * 删除单行单列族多列记录
     *
     * @param tableName    表名
     * @param rowKey       行名
     * @param columnFamily 列族
     * @param column       列
     */
    public static void deleteColumn(String tableName, String rowKey, String columnFamily, List<String> columns) throws IOException {
        List<Delete> deletes = new ArrayList<>();
        for (String column : columns) {
            deletes.add(new Delete(rowKey.getBytes()).addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column)));
        }
        delete(tableName, deletes);
    }

    public static void delete(String tableName, Delete delete) throws IOException {
        getTable(tableName).delete(delete);
    }

    public static void delete(String tableName, List<Delete> deletes) throws IOException {
        getTable(tableName).delete(deletes);
    }

    /**
     * 查找一行记录
     *
     * @param tableName 表名
     * @param rowKey    行名
     * @return 结果
     */
    public static String getRow(String tableName, String rowKey) throws IOException {
        Table table = getTable(tableName);
        Result result = table.get(new Get(rowKey.getBytes()));
        StringBuffer sb = new StringBuffer();
        resultToString(sb, result);
        return sb.toString();
    }

    /**
     * 查询单行、单列族、单列的值
     *
     * @param tableName    表名
     * @param rowKey       行名
     * @param columnFamily 列族
     * @param column       列名
     * @return 列值
     */
    public static String getValue(String tableName, String rowKey, String columnFamily, String column) throws IOException {
        Get get = new Get(rowKey.getBytes());
        get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        Result result = getTable(tableName).get(get);
        return Bytes.toString(result.value());
    }

    /**
     * getxxxArray()返回的都是整行数据组成的数组，可以根据偏移量跟长度取值
     *
     * @throws IOException
     */
    public static List<String> getValueWithVersion(String tableName, String rowKey, String columnFamily, String column, int versionCount) throws IOException {
        List<String> data = new ArrayList<>();
        Table table = connection.getTable(TableName.valueOf("ns1:t2"));
        Get get = new Get(Bytes.toBytes("row1"));
        //get.readAllVersions();
        //创建的时候指定了版本数是4，即使设置为5也只能查出来4行
        get.setMaxVersions(versionCount);
        Result result = table.get(get);
        List<Cell> columnCells = result.getColumnCells(Bytes.toBytes("f1"), Bytes.toBytes("id"));
        StringBuilder sb = new StringBuilder();
        for (Cell columnCell : columnCells) {
            sb.append(getCellData(columnCell, CellType.ROW)).append(":")
                    .append(getCellData(columnCell, CellType.FAMILY)).append(":")
                    .append(getCellData(columnCell, CellType.QUALIFY)).append(":")
                    .append(getCellData(columnCell, CellType.TIMESTAMP)).append(":")
                    .append(getCellData(columnCell, CellType.VALUE));
            data.add(sb.toString());

        }
        return data;
    }

    /**
     * 全表扫描
     *
     * @param tableName 表名
     * @see Scan
     */
    public static String getAll(String tableName) throws IOException {
        Table table = getTable(tableName);
        Scan scan = new Scan();
        StringBuffer sb = new StringBuffer();
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                resultToString(sb, result);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * 列出的也是最新的数据，并不是该列所有版本的数据
     * 数据如下
     * f1              f2
     * id name age     id name
     * id              name age
     * 若设置为3列2行
     * 则每次rpc调用返回数据如下
     * 第一次
     * f1：id name age（这是第一行） f2：id name（这是第二行，虽然是3列，但是这一行结束前只有2列，因此只返回两列）
     * 第二次
     * f1：id f2：name age（这是第一行）
     * <p>
     * 开启缓存
     * (全局)
     * <property>
     * <name>java.hbase.client.scanner.caching</name>
     * <!-- 整数最大值（默认是-1，即不开启） -->
     * <value>2147483647</value>
     * <source>java.hbase-default.xml</source>
     * </property>
     *
     * @throws IOException
     */
    public String scan(String tableName, String startRow, String stopRow) throws IOException {
        Table table = getTable(tableName);
        Scan scan = new Scan();
        //每次返回几列，如若不设置即每次返回全部列，可能造成内存溢出
        scan.setBatch(Integer.parseInt(PropertiesKey.HBASE_SCAN_BATCH));
        //每次返回几行
        scan.setCaching(Integer.parseInt(PropertiesKey.HBASE_SCAN_CACHING));
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow));
        ResultScanner scanner = table.getScanner(scan);
        Iterator<Result> iterator = scanner.iterator();
        StringBuffer sb = new StringBuffer();
        while (iterator.hasNext()) {
            resultToString(sb, iterator.next());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 拼接结果
     */
    private static void resultToString(StringBuffer sb, Result result) {
        for (Cell cell : result.rawCells()) {
            sb.append(Bytes.toString(cell.getRowArray())).append("\t")
                    .append(Bytes.toString(cell.getFamilyArray())).append("\t")
                    .append(Bytes.toString(cell.getQualifierArray())).append("\t")
                    .append(cell.getTimestamp()).append("\t")
                    .append(Bytes.toString(cell.getValueArray())).append("~");
        }
    }

    /**
     * 过滤器扫描：参考
     * https://www.cnblogs.com/Transkai/p/10727257.html<br>
     * 该 API 较老，使用时要注意查看过时方法上的注释，找新的 API 使用
     *
     * @param tableName 表名
     * @param filter    过滤器
     */
    public static List<Cell> scanByFilter(String tableName, Filter filter) throws IOException {
        List<Cell> resultList = new ArrayList<>();
        Table table = getTable(tableName);
        Scan scan = new Scan();
        scan.setFilter(filter);
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                resultList.addAll(Arrays.asList(result.rawCells()));
            }
        }

        return resultList;
    }




    /**
     * 生成分区键
     *
     * @param regionCount 分区数
     * @return 多个分区键
     */
    private static byte[][] getSplitKeys(int regionCount) {
        int splitKeyCount = regionCount - 1;
        byte[][] bytes = new byte[splitKeyCount][];

        List<byte[]> byteList = new ArrayList<>();
        for (int i = 0; i < splitKeyCount; i++) {
            String key = i + "|";
            byteList.add(Bytes.toBytes(key));
        }

        byteList.toArray(bytes);
        return bytes;
    }

    public static Map<CellType, String> transDataToMap(Result result) {

        Map<CellType, String> data = new HashMap<>();
        String r = Bytes.toString(result.getRow());
        //<列族,<列<值，时间戳>>>
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> resultMap = result.getMap();
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : resultMap.entrySet()) {
            //得到列族
            String f = Bytes.toString(entry.getKey());
            Map<byte[], NavigableMap<Long, byte[]>> colDataMap = entry.getValue();
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> ets : colDataMap.entrySet()) {
                String c = Bytes.toString(ets.getKey());
                Map<Long, byte[]> tsValueMap = ets.getValue();
                for (Map.Entry<Long, byte[]> e : tsValueMap.entrySet()) {
                    Long ts = e.getKey();
                    String value = Bytes.toString(e.getValue());

                    data.put(CellType.ROW,r);
                    data.put(CellType.FAMILY,f);
                    data.put(CellType.QUALIFY,c);
                    data.put(CellType.TIMESTAMP,ts.toString());
                    data.put(CellType.VALUE,value);
                }
            }
        }
        return data;
    }

    public static String getCellData(Cell cell, CellType cellType) {
        byte[] data = cell.getRowArray();
        switch (cellType) {
            case ROW:
                int rowOffset = cell.getRowOffset();
                short rowLength = cell.getRowLength();
                byte[] rowArray = cell.getRowArray();
                byte[] row = new byte[rowLength];
                //数据源，开始位置，目的地，目的数组起始位置，拷贝长度
                System.arraycopy(rowArray, rowOffset, row, 0, rowLength);
                return Bytes.toString(row);
            case FAMILY:
                int familyOffset = cell.getFamilyOffset();
                byte familyLength = cell.getFamilyLength();
                byte[] familyArray = cell.getFamilyArray();
                byte[] family = new byte[familyLength];
                System.arraycopy(familyArray, familyOffset, family, 0, familyLength);
                return Bytes.toString(family);
            case QUALIFY:
                int qualifierLength = cell.getQualifierLength();
                byte[] qualifier = new byte[qualifierLength];
                System.arraycopy(cell.getQualifierArray(), cell.getQualifierOffset(), qualifier, 0, qualifierLength);
                return Bytes.toString(qualifier);
            case VALUE:
                int valueLength = cell.getValueLength();
                byte[] value = new byte[valueLength];
                System.arraycopy(cell.getValueArray(), cell.getValueOffset(), value, 0, valueLength);
                return Bytes.toString(value);
            case TIMESTAMP:
                return String.valueOf(cell.getTimestamp());
            default:
                return null;
        }
    }


    /**
     * 用于创建线程池。
     * ThreadFactory实现类：重命名线程
     */
    private static class ThreadFactoryImpl implements ThreadFactory {
        private final String name;
        private AtomicInteger id = new AtomicInteger(1);

        private ThreadFactoryImpl(String name) {
            this.name = name + "-" + id.getAndIncrement();
        }

        @Override
        public Thread newThread(@Nonnull Runnable runnable) {
            return new Thread(runnable, name);
        }
    }
}

enum CellType {
    ROW,
    FAMILY,
    QUALIFY,
    VALUE,
    TIMESTAMP
}
