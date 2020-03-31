package streaming

import org.apache.logging.log4j.scala.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object WC extends Logging {
  def main(args: Array[String]): Unit = {
    //TODO 必须设置appname不然报错
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("zyp")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(5))

    val line: ReceiverInputDStream[String] = ssc.socketTextStream("zyp-2", 9999, StorageLevel.OFF_HEAP)
    //val value: DStream[(String, Int)] = line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    //TODO 有状态转化，即保留上个批次的结果，必须设置不然报错
    //ssc.checkpoint("spark/data/temp")
    //val value: DStream[(String, Int)] = line.flatMap(_.split(" ")).map((_, 1)).updateStateByKey((seq: Seq[Int], op: Option[Int])=> Option(op.getOrElse(0)+seq.sum))
    val value: DStream[(String, Int)] = line.flatMap(_.split(" ")).map((_, 1)).reduceByKeyAndWindow((a: Int, b: Int) => a + b, Seconds(15), Seconds(10))
    value.print()
    ssc.start()

    /*val sc: SparkContext = ssc.sparkContext
    val queue: mutable.Queue[RDD[Int]] = mutable.Queue[RDD[Int]]()
    val value: InputDStream[Int] = ssc.queueStream(queue, true)
    value.reduce(_ + _).print()

    ssc.start()

    for (elem <- 1 to 3) {
      queue += sc.parallelize(1 to 10)
      queue += sc.makeRDD(1 to 5)
      //Thread.sleep(2000)
    }*/

    ssc.awaitTermination()
  }

}
