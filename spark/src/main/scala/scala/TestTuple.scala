package scala

object TestTuple {
  def main(args: Array[String]): Unit = {
    val t = (24,"kobe",8.0,1.1f,30L,true)
    println(t._1)//24
    t.productIterator.foreach(x=>print(x+" "))//24 kobe 8.0 1.1 30 true
    println(t.toString())//(24,kobe,8.0,1.1,30,true)
    val t2 = (24,"zhang")
    println(t2.swap)//(zhang,24)
  }
}
