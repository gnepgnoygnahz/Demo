package scala.sql

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, LongType, StructType}

object MyAggregateUDFWeak extends UserDefinedAggregateFunction {

  //TODO 输入的数据类型
  override def inputSchema: StructType = {
    new StructType().add("age", LongType)
  }

  //TODO 缓冲区的数据类型，即计算过程中需要创建的变量
  override def bufferSchema: StructType = {
    new StructType().add("sum", LongType).add("count", LongType)
  }

  //TODO 返回值类型
  override def dataType: DataType = {
    DoubleType
  }

  //TODO 是否稳定，即每次返回结果是否相通
  override def deterministic: Boolean = {
    true
  }

  //TODO 初始化缓冲区的数据
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0L
  }

  //TODO 更新缓冲区数据
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getLong(0) + input.getLong(0)
    buffer(1) = buffer.getLong(1) + 1

  }

  //TODO 合并多个缓冲区数据
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)

  }

  //TODO 计算结果
  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0).toDouble / buffer.getLong(1)
  }
}
