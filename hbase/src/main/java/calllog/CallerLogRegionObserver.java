package calllog;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;
import utils.NumberUtil;

import java.io.FileWriter;
import java.io.IOException;

public class CallerLogRegionObserver extends BaseRegionObserver {

    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
        super.postPut(e, put, edit, durability);
        FileWriter fw = new FileWriter("/home/zyp/a.txt", true);
        fw.append("start\n");
        String tableName = e.getEnvironment().getRegionInfo().getTable().getNameAsString();
        fw.append(tableName + "\n");
        if ("ns1:calllogs".equals(tableName)) {
            String rowKey = Bytes.toString(put.getRow());
            String[] data = rowKey.split(",");
            if ("1".equals(data[3])) {
                return;
            }
            fw.append("insert\n");
            int hash = Math.abs((data[4] + data[2]).hashCode()) % 100;
            String regNo = NumberUtil.formatNumber(hash, "00");

            String newRowKey = regNo + "," + data[4] + "," + data[2] + ",1," + data[1] + "," + data[5];
            Put newPut = new Put(Bytes.toBytes(newRowKey));
            //必须加一列，不然插入失败
            newPut.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("dummy"), Bytes.toBytes("no"));
            Table table = e.getEnvironment().getTable(TableName.valueOf(e.getEnvironment().getRegionInfo().getTable().getNameAsString()));
            fw.append(table.getTableDescriptor().getTableName().getNameAsString() + "\n");
            table.put(newPut);
        }
        fw.flush();
        fw.close();
    }
}
