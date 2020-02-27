package scala

object TestIterator {
  def main(args: Array[String]): Unit = {
    val i2 = Iterator(1,2,3,4,5)
    val i1 = Iterator("Baidu", "Google", "Runoob", "Taobao")
    //while(i1.hasNext) print(i1.next()+" ")//Baidu Google Runoob Taobao
    val ita = Iterator(20,40,2,50,69,90)
    val itb = Iterator(20,40,2,50,69,90)
    val str = List("kobe","bryant","zhang","yong","peng")
    val it = str.grouped(3)
    println(it.isInstanceOf[Iterator[List[Int]]])
   // while(it.hasNext) print(it.next()+" ")
    println(it.toList)
    val aa = it.toList.flatMap(x=>x)
    println(aa)
    val lines = List("hello tom hello jerry", "hello jerry", "hello kitty")
    lines.flatMap(_.split(" ")).map((_,1)).groupBy(_._1)
    println(lines.map(_.split(" ")).flatten)
    println(lines.map(_.split(" ").mkString(" ")))
    /*println(ita.max)
    println(ita.min)
    println(i2.max)
    println(i2.min)
    println(i2.sum)
    println(i2.size)
    println(i2.length)*/

  }
}
