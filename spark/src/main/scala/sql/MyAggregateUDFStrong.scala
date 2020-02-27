package sql

import org.apache.spark.sql.{Encoder, Encoders}
import org.apache.spark.sql.expressions.Aggregator


object MyAggregateUDFStrong extends Aggregator[Person, Average, Double] {

  //TODO 初始化缓冲区的数据
  override def zero: Average = {
    Average(0L, 0L)
  }

  //TODO 更新缓冲区数据
  override def reduce(b: Average, a: Person): Average = {
    b.sum += a.age
    b.count += 1
    b
  }

  //TODO 合并多个缓冲区数据
  override def merge(b1: Average, b2: Average): Average = {
    b1.sum += b2.sum
    b1.count += b2.count
    b1
  }

  //TODO 计算结果
  override def finish(reduction: Average): Double = {
    reduction.sum.toDouble / reduction.count
  }

  //TODO 缓冲区数据编码器
  override def bufferEncoder: Encoder[Average] = {
    Encoders.product
  }

  //TODO 输出数据编码器
  override def outputEncoder: Encoder[Double] = {
    Encoders.scalaDouble
  }
}

//TODO 强类型即把计算过程中创建的变量封装为一个class，注意参数需为var类型即可改变，不标注则默认为val
case class Average(var sum: Long, var count: Long)