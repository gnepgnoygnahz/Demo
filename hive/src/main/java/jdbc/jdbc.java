package jdbc;

import java.sql.*;

public class jdbc {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection("jdbc:hive2://zyp-3:10000/hive");
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from dual");
        while (resultSet.next()) {
            System.out.println(resultSet.getInt(1) + "," + resultSet.getString(2) + ","
                    + resultSet.getFloat(3) + "," + resultSet.getInt(4));
        }
        resultSet.close();
        statement.close();
        conn.close();
    }
}
