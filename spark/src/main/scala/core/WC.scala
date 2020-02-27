package core

import org.apache.spark.{SparkConf, SparkContext}

object WC {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WC").setMaster("local")
    val sc = new SparkContext(conf);
    sc.textFile("D:/Money/data/wc.txt")
      .filter(_.contains("CardNo"))
      .saveAsTextFile("D:/Money/data/out");
    sc.stop();
  }
}
