package hive.jdbc;

import lombok.extern.log4j.Log4j2;

import java.sql.*;

@Log4j2
public class Jdbc {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection("jdbc:hive2://zyp-3:10000/hive");
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from orders");
        while (resultSet.next()) {
            log.info(resultSet.getInt(1) + "," + resultSet.getString(2) + ","
                    + resultSet.getFloat(3) + "," + resultSet.getInt(4));
        }
        resultSet.close();
        statement.close();
        conn.close();
    }
}
