package sql;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.types.DataTypes.DoubleType;
import static org.apache.spark.sql.types.DataTypes.LongType;

public class MyAggregateUDFWeak extends UserDefinedAggregateFunction {

    //TODO 输入的数据类型
    @Override
    public StructType inputSchema() {
        return new StructType().add("age", LongType);
    }

    //TODO 缓冲区的数据类型，即计算过程中需要创建的变量
    @Override
    public StructType bufferSchema() {
        return new StructType().add("sum", LongType).add("count", LongType);
    }

    //TODO 返回值类型
    @Override
    public DataType dataType() {
        return DoubleType;
    }

    //TODO 是否稳定，即每次返回结果是否相通
    @Override
    public boolean deterministic() {
        return true;
    }

    //TODO 初始化缓冲区的数据
    @Override
    public void initialize(MutableAggregationBuffer buffer) {
        buffer.update(0, 0L);
        buffer.update(1, 0L);

    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
        buffer.update(0, buffer.getLong(0) + input.getLong(0));
        buffer.update(1, buffer.getLong(1) + 1);
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
        buffer1.update(0, buffer1.getLong(0) + buffer2.getLong(0));
        buffer1.update(1, buffer1.getLong(1) + buffer2.getLong(1));
    }

    @Override
    public Object evaluate(Row buffer) {
        return Double.valueOf(buffer.getLong(0)) / buffer.getLong(1);
    }
}
