package jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class mysql {

    private static final Logger logger = LogManager.getLogger(mysql.class);
    private Connection connection;
    private Statement statement;
    private Long start;
    private long end;

    @Before
    public void init() throws ClassNotFoundException, SQLException {
        String driverClass = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/print?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "root";
        Class.forName(driverClass);
        connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
        start = System.currentTimeMillis();
    }

    @After
    public void close() throws SQLException {
        connection.commit();
        statement.close();
        connection.close();
        end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * 140818ms
     *
     * @throws SQLException
     */
    @Test
    public void testStatement() throws SQLException {
        statement = connection.createStatement();
        for (int i = 0; i < 1000000; i++) {
            String sql = "insert into user(name,age) values('kobe+" + (i % 100) + "'," + (i % 100) + ")";
            statement.execute(sql);
        }
    }

    /**
     * 82110
     *
     * @throws SQLException
     */
    @Test
    public void testPrepareStatement() throws SQLException {
        String sql = "insert into user(name,age) values(?,?)";
        statement = connection.prepareStatement(sql);
        PreparedStatement preparedStatement = (PreparedStatement) statement;
        for (int i = 0; i < 1000000; i++) {
            preparedStatement.setString(1, "kobe+" + (i % 100));
            preparedStatement.setInt(2, (i % 100));
            preparedStatement.addBatch();
            if (i % 2000 == 0) {
                preparedStatement.executeBatch();
            }
        }
        preparedStatement.executeBatch();
    }

    /**
     * 32040
     * 存储过程 没有返回值  可以在入参定义 (OUT param1 INT)，调用的时候call simpleproc(@a)，然后查询 select @a
     * @throws SQLException
     * CREATE PROCEDURE `batchInsert`( IN n INT )
     * BEGIN
     * 	DECLARE
     * 		name0 VARCHAR ( 20 );
     * 	DECLARE
     * 		age0 INT;
     * 	DECLARE
     * 		i INT DEFAULT 0;
     * 	WHILE
     * 			i < n DO
     * 			SET name0 := CONCAT( 'kobe+', i % 100 );
     * 		SET age0 := i % 100;
     * 		INSERT INTO USER ( name, age )
     * 		VALUES
     * 			( name0, age0 );
     * 		SET i := i + 1;
     * 	END WHILE;
     * END
     */
    @Test
    public void testProcedure() throws SQLException {
        String sql = "{call batchInsert(?)}";
        statement = connection.prepareCall(sql);
        CallableStatement callableStatement = (CallableStatement) statement;
        callableStatement.setInt(1, 1000000);
        callableStatement.execute();
    }

    /**
     * 方法 有返回值的存储过程
     * CREATE FUNCTION ADDS ( a INT, b INT ) RETURNS INT BEGIN
     * RETURN a + b ;
     * END
     *
     * 报错：
     * This function has none of DETERMINISTIC, NO SQL, or READS SQL DATA in its declaration and binary logging is enabled
     * (you *might* want to use the less safe log_bin_trust_function_creators variable)
     * set global log_bin_trust_function_creators=TRUE;
     * 这是我们开启了bin-log, 我们就必须指定我们的函数是否是
     * 1 DETERMINISTIC 不确定的
     * 2 NO SQL 没有SQl语句，当然也不会修改数据
     * 3 READS SQL DATA 只是读取数据，当然也不会修改数据
     * 4 MODIFIES SQL DATA 要修改数据
     * 5 CONTAINS SQL 包含了SQL语句
     * 其中在function里面，只有 DETERMINISTIC, NO SQL 和 READS SQL DATA 被支持。如果我们开启了 bin-log, 我们就必须为我们的function指定一个参数。
     * 在MySQL中创建函数时出现这种错误的解决方法：
     * set global log_bin_trust_function_creators=TRUE;
     *
     * @throws SQLException
     */
    @Test
    public void testFunction() throws SQLException {
        String sql = "{? = call adds(?, ?)}";
        statement = connection.prepareCall(sql);
        CallableStatement callableStatement = (CallableStatement) statement;
        callableStatement.setInt(2, 1);
        callableStatement.setInt(3, 2);
        callableStatement.registerOutParameter(1, Types.INTEGER);
        callableStatement.execute();
        System.out.println("sum ===" + callableStatement.getInt(1));
    }

    /**
     * 事务的并发执行，容易出现的几个现象
     * --------------------------
     * 	1.脏读
     * 		读未提交,一个事务读取了另外一个事务改写还没有提交的数据，如果另外一个
     * 		事务在稍后的时候回滚。
     *
     * 	2.不可重复读
     * 		一个事务进行相同条件查询连续的两次或者两次以上，每次结果都不同。
     * 		有其他事务做了update操作。
     *
     * 	3.幻读
     * 		和(2)很像，其他事务做了insert操作.
     *
     * 	设置事务级别为
     * 	    int TRANSACTION_NONE             = 0; 不支持事务
     * 	    int TRANSACTION_READ_UNCOMMITTED = 1; 读未提交：导致脏读
     * 	    TRANSACTION_READ_COMMITTED       = 2; 读已提交：导致不可重复读（Oracle默认级别）
     * 	    TRANSACTION_REPEATABLE_READ      = 4; 可重复读：导致幻读（MySQL默认级别）
     * 	    int TRANSACTION_SERIALIZABLE     = 8; 串行化：  解决所有问题（代价就是不能并发操作）
     *
     * 	查看事务隔离级别
     * 	5.x   SELECT @@tx_isolation;
     * 	8.x   SELECT @@transaction_isolation;
     * 	设置事务隔离级别
     * 	SET [SESSION | GLOBAL] TRANSACTION ISOLATION LEVEL {READ UNCOMMITTED | READ COMMITTED | REPEATABLE READ | SERIALIZABLE}
     * 	SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;
     *
     * @throws SQLException
     */
    @Test
    public void testIsolationLevel() throws SQLException {
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(0) from user");
        while (resultSet.next()) {
            int result = resultSet.getInt(1);
            System.out.println("count==========" + result);
        }

    }
}
