package scala.sql

import org.apache.spark.sql.api.java.UDF1

object MyUDF extends UDF1[String, String] {
  override def call(t1: String): String = {
    t1 + ":kobe"
  }
}
