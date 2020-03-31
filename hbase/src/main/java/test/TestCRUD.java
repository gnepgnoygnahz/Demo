package test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

enum CellType1 {
    ROW,
    FAMILY,
    QUALIFY,
    VALUE,
    TIMESTAMP
}

public class TestCRUD {

    private Connection connection;
    private Table table;
    private Admin admin;
    private Scan scan = new Scan();

    @Before
    public void init() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        connection = ConnectionFactory.createConnection(configuration);
        admin = connection.getAdmin();
        TableName tableName = TableName.valueOf("ns1:t1");
        table = connection.getTable(tableName);
    }

    @After
    public void close() throws IOException {
        //顺序不能变，不然删除不了数据
        table.close();
        connection.close();
    }

    @Test
    public void get() throws IOException {
        byte[] rowid = Bytes.toBytes("row2");
        Get get = new Get(rowid);
        Result result = table.get(get);
        byte[] value = result.getValue(Bytes.toBytes("f2"), Bytes.toBytes("id"));
        System.out.println(Bytes.toInt(value));
    }

    /**
     * 命令行插入的数据都是字符串
     *
     * @throws IOException
     */
    @Test
    public void put() throws IOException {

        byte[] rowid = Bytes.toBytes("row2");
        Put put = new Put(rowid);
        byte[] f2 = Bytes.toBytes("f2");
        byte[] id = Bytes.toBytes("id");
        byte[] value = Bytes.toBytes(3);
        put.addColumn(f2, id, value);
        table.put(put);
        table.put(Arrays.asList(put));
    }

    /**
     * 批量插入
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void bigInsert() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("0000");
        List<Row> action = new ArrayList<>();
        //整表关闭写前日志
        HTableDescriptor tableDescriptor =  new HTableDescriptor(TableName.valueOf("ns1:t1"));
        tableDescriptor.setDurability(Durability.SKIP_WAL);
        for (int i = 3; i < 10000; i++) {
            Put put = new Put(Bytes.toBytes("row" + df.format(i)));
            put.addColumn(Bytes.toBytes("f3"), Bytes.toBytes("id"), Bytes.toBytes(i));
            put.addColumn(Bytes.toBytes("f3"), Bytes.toBytes("name"), Bytes.toBytes("tom" + i));
            put.addColumn(Bytes.toBytes("f3"), Bytes.toBytes("age"), Bytes.toBytes(i % 100));
            //单次写前日志
            put.setDurability(Durability.SKIP_WAL);
            action.add(put);
        }
        //批量插入，每执行一个Put就要想区域服务器发一个请求，这种就会将Put添加到写缓冲，等到写缓冲都填满的时候才会发起请求
        table.batch(action, new Object[action.size()]);
        System.out.println(System.currentTimeMillis() - start);
    }

    /**
     * 只能删除最新的一个版本的数据，老的还在
     *
     * @throws IOException
     */
    @Test
    public void delete() throws IOException {
        Delete delete = new Delete(Bytes.toBytes("row2"));
        delete.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("id"));
        table.delete(delete);
    }

    @Test
    public void listNameSpaces() throws IOException {
        NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
        for (NamespaceDescriptor namespaceDescriptor : namespaceDescriptors) {
            System.out.println(namespaceDescriptor.getName());
        }
    }

    @Test
    public void createNameSpace() throws IOException {
        NamespaceDescriptor ns2 = NamespaceDescriptor.create("ns2").build();
        admin.createNamespace(ns2);
    }

    @Test
    public void listNameTables() throws IOException {
        HTableDescriptor[] tableDescriptors = admin.getTableDescriptors(Arrays.asList("ns1:t1"));
        for (HTableDescriptor tableDescriptor : tableDescriptors) {
            System.out.println(Bytes.toString(tableDescriptor.getTableName().getName()));
        }
    }

    @Test
    public void createTable() throws IOException {
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("ns1:t2"));
        HColumnDescriptor hcd = new HColumnDescriptor("f1");
        hcd.setKeepDeletedCells(KeepDeletedCells.TRUE);//保留删除数据以前的版本，正常情况下，删除数据后，以前的版本都不可见
        hcd.setTimeToLive(20);//数据只存活20秒
        htd.addFamily(hcd);
        admin.createTable(htd);
    }

    @Test
    public void deleteTable() throws IOException {
        admin.disableTable(TableName.valueOf("ns1:t2"));
        admin.deleteTable(TableName.valueOf("ns1:t2"));
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
    @Test
    public void scan() throws IOException {
        scan.setBatch(3);//每次返回3列，如若不设置即每次返回全部列，可能造成内存溢出
        scan.setCaching(2);//每次返回3行
        scan.setStartRow(Bytes.toBytes("row2"));
        scan.setStopRow(Bytes.toBytes("row3"));
        ResultScanner scanner = table.getScanner(scan);
        Iterator<Result> iterator = scanner.iterator();

        /*while (iterator.hasNext()) {
            Result result = iterator.next();
            byte[] value = result.getValue(Bytes.toBytes("f3"), Bytes.toBytes("name"));
            System.out.println(Bytes.toString(value));
        }*/

        /*while (iterator.hasNext()){
            Result result = iterator.next();
            //列，值
            NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(Bytes.toBytes("f3"));
            for (Map.Entry<byte[], byte[]> entry : familyMap.entrySet()) {
                System.out.print(Bytes.toString(entry.getKey())+":"+Bytes.toString(entry.getValue()));
            }
            System.out.println();;
        }*/

        sysData(scan);
    }

    /**
     * getxxxArray()返回的都是整行数据组成的数组，可以根据偏移量跟长度取值
     *
     * @throws IOException
     */
    @Test
    public void getWithVersion() throws IOException {
        Table table = connection.getTable(TableName.valueOf("ns1:t2"));
        Get get = new Get(Bytes.toBytes("row1"));
        //get.readAllVersions();
        get.setMaxVersions(5);//创建的时候指定了版本数是4，即使设置为5也只能查出来4行
        Result result = table.get(get);
        List<Cell> columnCells = result.getColumnCells(Bytes.toBytes("f1"), Bytes.toBytes("id"));
        for (Cell columnCell : columnCells) {
            System.out.println(getCellData(columnCell, CellType1.ROW) + " : " + getCellData(columnCell, CellType1.FAMILY)
                    + " : " + getCellData(columnCell, CellType1.QUALIFY) + " : " + getCellData(columnCell, CellType1.VALUE)
                    + " : " + getCellData(columnCell, CellType1.TIMESTAMP)
            );
        }
    }

    /**
     * 行过滤器
     * select * from ns1:t1 where row<=row0100
     */
    @Test
    public void rowFilter() {
        RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("row0100")));
        scan.setFilter(rowFilter);
        sysData(scan);
    }

    /**
     * 列族过滤器
     * select family<=f2 from ns1:t1
     */
    @Test
    public void familyFilter() {
        FamilyFilter familyFilter = new FamilyFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("f2")));
        scan.setFilter(familyFilter);
        sysData(scan);
    }

    /**
     * 列过滤器
     * select qualifier>=id from ns1:t1
     */
    @Test
    public void qualifierFilter() {
        QualifierFilter qualifierFilter = new QualifierFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes("id")));
        scan.setFilter(qualifierFilter);
        sysData(scan);
    }

    /**
     * 值过滤器
     * select * from ns1:t1 where value like '%2222%'
     */
    @Test
    public void valueFilter() {
        ValueFilter valueFilter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("2222"));
        scan.setFilter(valueFilter);
        sysData(scan);
    }

    /**
     * 依赖列过滤器
     * select case true then age,id,name else age,id from ns1:t1 where family=f3 and qualifier=name and value=tom8888
     */
    @Test
    public void dependentColumnFilter() {
        DependentColumnFilter dependentColumnFilter = new DependentColumnFilter(
                Bytes.toBytes("f3"),
                Bytes.toBytes("name"),
                true,
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes("tom8888"))
        );
        scan.setFilter(dependentColumnFilter);
        sysData(scan);
    }

    /**
     * 单列值过滤器
     * select f1.all,f2.all from ns1:t1 union select f3.all from ns1:t1 where family=f3 and qualifier=name and value=tom8888
     */
    @Test
    public void singleColumnValueFilter() {
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                Bytes.toBytes("f3"),
                Bytes.toBytes("name"),
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes("tom8888"))
        );
        scan.setFilter(singleColumnValueFilter);
        scan.setMaxResultSize(10);
        sysData(scan);
    }

    /**
     * 单列值排列过滤器
     * select f1.all,f2.all from ns1:t1 union select f3 exclude name from ns1:t1 where family=f3 and qualifier=name and value=tom8888
     */
    @Test
    public void singleColumnValueExcludeFilter() {
        SingleColumnValueExcludeFilter singleColumnValueExcludeFilter = new SingleColumnValueExcludeFilter(
                Bytes.toBytes("f3"),
                Bytes.toBytes("name"),
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes("tom8888"))
        );
        scan.setFilter(singleColumnValueExcludeFilter);
        scan.setMaxResultSize(10);
        sysData(scan);
    }

    /**
     * 前缀过滤器
     * select * from ns1:t1 where row like 'tom888%'
     */
    @Test
    public void prefixFilter() {
        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes("row888"));
        scan.setFilter(prefixFilter);
        sysData(scan);
    }

    /**
     * 分页过滤器，每个分区都会返回10条，ns1:t1在zyp-2的分区服务器上，但是在两个分区里面，因此返回20条
     */
    @Test
    public void pageFilter() {
        PageFilter pageFilter = new PageFilter(10);
        scan.setFilter(pageFilter);
        sysData(scan);
    }

    /**
     * 只提取key,丢弃value.
     */
    @Test
    public void keyOnlyFilter() {
        KeyOnlyFilter keyOnlyFilter = new KeyOnlyFilter();
        scan.setFilter(keyOnlyFilter);
        scan.setMaxResultSize(10);
        sysData(scan);
    }

    /**
     * 列是自动排序，的跟插入顺序无关
     * limit 2 显示两列
     * offset 1 列偏移1列
     * 即从第2列开始显示，显示2列
     * select ,b,c,... from ns1:t1
     */
    @Test
    public void columnPaginationFilter() {
        ColumnPaginationFilter columnPaginationFilter = new ColumnPaginationFilter(2, 1);
        scan.setFilter(columnPaginationFilter);
        scan.setMaxResultSize(5);
        sysData(scan);
    }

    /**
     * 正则
     * ^tom888  以tom888开头
     * 888$     以888结尾
     */
    @Test
    public void like() {
        ValueFilter valueFilter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("^tom888"));
        scan.setFilter(valueFilter);
        sysData(scan);
    }

    /**
     * 组合过滤
     * select * from ns1:t1 where ((age <= 13) and (name like '%t')
     * or
     * (age > 13) and (name like 't%'))
     */
    @Test
    public void comboFilter() {
        //where ... f2:age <= 13
        SingleColumnValueFilter ftl = new SingleColumnValueFilter(
                Bytes.toBytes("f3"),
                Bytes.toBytes("age"),
                CompareFilter.CompareOp.LESS_OR_EQUAL,
                new BinaryComparator(Bytes.toBytes("13"))
        );

        //where ... f2:name like %t
        SingleColumnValueFilter ftr = new SingleColumnValueFilter(
                Bytes.toBytes("f3"),
                Bytes.toBytes("name"),
                CompareFilter.CompareOp.EQUAL,
                new RegexStringComparator("^t")
        );

        FilterList fl1 = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        fl1.addFilter(ftl);
        fl1.addFilter(ftr);

        //where ... f2:age > 13
        SingleColumnValueFilter fbl = new SingleColumnValueFilter(
                Bytes.toBytes("f2"),
                Bytes.toBytes("age"),
                CompareFilter.CompareOp.GREATER,
                new BinaryComparator(Bytes.toBytes("13"))
        );

        //where ... f2:name like %t
        SingleColumnValueFilter fbr = new SingleColumnValueFilter(
                Bytes.toBytes("f2"),
                Bytes.toBytes("name"),
                CompareFilter.CompareOp.EQUAL,
                new RegexStringComparator("t$")
        );

        FilterList fl2 = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        fl2.addFilter(fbl);
        fl2.addFilter(fbr);

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        filterList.addFilter(fl1);
        filterList.addFilter(fl2);

        scan.setFilter(filterList);
        sysData(scan);
    }

    /**
     * 计数器
     * 1,10,100 即步长，每调一次，该列增长对应的值
     */
    @Test
    public void incr() throws IOException {
        table = connection.getTable(TableName.valueOf("ns1:t2"));
        Increment increment = new Increment(Bytes.toBytes("row1"));
        increment.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("daily"), 1);
        increment.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("weekly"), 10);
        increment.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("monthly"), 100);
        table.increment(increment);
    }

    public void sysData(Scan scan) {
        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<Result> iterator = scanner.iterator();
        while (iterator.hasNext()) {
            Result result = iterator.next();
            String r = Bytes.toString(result.getRow());
            System.out.println("==========" + r + "==========");
            //<列族,<列<值，时间戳>>>
            NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> resultMap = result.getMap();
            for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : resultMap.entrySet()) {
                //得到列族
                String f = Bytes.toString(entry.getKey());
                System.out.println("=====" + f + "=====");
                Map<byte[], NavigableMap<Long, byte[]>> colDataMap = entry.getValue();
                for (Map.Entry<byte[], NavigableMap<Long, byte[]>> ets : colDataMap.entrySet()) {
                    String c = Bytes.toString(ets.getKey());
                    Map<Long, byte[]> tsValueMap = ets.getValue();
                    for (Map.Entry<Long, byte[]> e : tsValueMap.entrySet()) {
                        Long ts = e.getKey();
                        String value = Bytes.toString(e.getValue());
                        System.out.print(c + ":" + ts + "=" + value + ",");
                    }
                }
            }
            System.out.println();
        }
    }

    public String getCellData(Cell cell, CellType1 cellType) {
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

}