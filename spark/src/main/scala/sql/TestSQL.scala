package sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StringType

import scala.sql.{MyAggregateUDFWeak, MyUDF}

object TestSQL {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    //TODO 导入隐式转换才能使RDD转换成DF或DS
    import spark.implicits._
    val df = spark.read.json("spark/data/Person.json")
    df.show()
    df.select("name").show()
    df.select($"name", $"age" + 1).show
    df.filter($"age" > 30).show()
    df.createOrReplaceTempView("person")
    spark.sql("select * from person a where a.age>=30").show()
    val ds = df.as[Person]
    ds.foreachPartition({
      it =>
        it.foreach({
          person => print(person.toString)
        })
    })

    val rdd = spark.sparkContext.textFile("spark/data/Person.txt")
    val df1 = rdd.map(
      person => {
        val data = person.split(",")
        (data(0).toLong, data(1), data(2).toLong)
      }).toDF("id", "name", "age")

    val ds1 = df1.as[Person]
    ds1.show()
    ds1.map {
      person => {
        println(person.getClass.getName)
        person
      }
    }.show()

    ds.createOrReplaceTempView("person")
    //spark.udf.register("addName", ((x:String)=>(x+":kobe")))
    //spark.udf.register("addName", addName _)
    spark.udf.register("addName", MyUDF, StringType)
    spark.sql("select addName(name) from person").show()

    //spark.udf.register("avgAge",MyAggregateUDFWeak)
    //spark.sql("select avgAge(age) from person").show()

    val value = MyAggregateUDFStrong.toColumn.name("avg_age")
    ds.select(value).show()
  }

  def addName(name:String): String ={
    name+":kobe"
  }


}

case class Person(id: Long, name: String, age: Long)
