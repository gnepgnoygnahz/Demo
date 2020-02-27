package calllog;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import utils.DateUtil;
import utils.NumberUtil;

public class DealPhone {
    public static void main(String[] args) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(configuration);
        Table table = connection.getTable(TableName.valueOf("ns1:calllogs"));

        String callerid = "16666666666";
        String calledid = "17777777777";
        String callTime = DateUtil.getCurrentDateWithTime();
        String duration = NumberUtil.formatNumber(100,"00000");
        int hash = Math.abs((callerid+DateUtil.getCurrentDateNoLine()).hashCode()) % 100;
        String regNo = NumberUtil.formatNumber(hash,"00");

        String rowKey = regNo+","+callerid+","+callTime+",0,"+calledid+","+duration;

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("callerPos"),Bytes.toBytes("河南"));
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("calledPos"),Bytes.toBytes("河北"));
        table.put(put);
        System.out.println("over");
    }
}
