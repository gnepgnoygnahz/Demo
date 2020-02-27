package mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 不需要实现Writable
 */
public class WCMysqlDBWritable implements DBWritable/*,Writable*/ {

    private String content;
    private String word;
    private Integer count;


    //Writable
   /* @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(word);
        out.writeInt(count);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        content = in.readUTF();
    }*/


    //DBWritable

    /**
     * 写入数据库
     *
     * @param statement
     * @throws SQLException
     */
    @Override
    public void write(PreparedStatement statement) throws SQLException {
        statement.setString(1, word);
        statement.setInt(2, count);
    }

    /**
     * 去读数据库
     *
     * @param resultSet
     * @throws SQLException
     */
    @Override
    public void readFields(ResultSet resultSet) throws SQLException {
        content = resultSet.getString(1);
    }
}
