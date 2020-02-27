package scala.sql

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

object mysql {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().config(new SparkConf().setMaster("local[*]")).getOrCreate()
    import spark.implicits._
    spark.read.format("jdbc")
      .option("url","jdbc:mysql://localhost:3306/weblog?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai")
      .option("driver","com.mysql.cj.jdbc.Driver")
      .option("user","root")
      .option("password","root")
      .option("dbtable","dw_pvs_day")
      .load().show()

    spark.read.format("jdbc").options(
      Map("url"->"jdbc:mysql://localhost:3306/weblog?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai",
        "driver"->"com.mysql.cj.jdbc.Driver",
        "user"->"root",
        "password"->"root",
        "dbtable"->"dw_pvs_day"
      )
    ).load().show()

    val props: Properties = new Properties()
    props.setProperty("user", "root")
    props.setProperty("password", "root")
    spark.read.jdbc("jdbc:mysql://localhost:3306/weblog?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai",
      "dw_pvs_day", props).show()

    val properties = new Properties()
    val in = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
    properties.load(in)
    spark.read.jdbc("jdbc:mysql://localhost:3306/weblog?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai",
      "dw_pvs_day",properties).show()

    /*val rdd:RDD[dw_pvs_day]= spark.sparkContext.makeRDD(List(dw_pvs_day("2020", "02", "27", 8888), dw_pvs_day("2020", "02", "26", 7777)))
    val ds:Dataset[dw_pvs_day] = rdd.toDS()
    ds.write.format("jdbc")
      .option("url","jdbc:mysql://localhost:3306/weblog?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai")
      //.option("driver","com.mysql.cj.jdbc.Driver")
      .option("user","root")
      .option("password","root")
      .option("dbtable","dw_pvs_day")
      .mode(SaveMode.Append).save()

    ds.write.mode(SaveMode.Append).jdbc("jdbc:mysql://localhost:3306/weblog?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai",
      "dw_pvs_day", props)*/

    spark.stop()
  }
}

case class dw_pvs_day(year:String,month:String,day:String,pvs:Long)
