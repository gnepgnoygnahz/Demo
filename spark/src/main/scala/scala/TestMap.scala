package scala

object TestMap {
  def main(args: Array[String]): Unit = {
    var m = Map(1->"kobe",2->"bryant")
    m+=(3->"zhang")
    val m1 = Map((3,"zhangyongpeng"),(4,"jay"))
    println(m)//Map(2 -> bryant, 1 -> kobe, 3 -> zhang)
    println(m.keys)//Set(2, 1, 3)
    println(m.values)//HashMap(bryant, kobe, zhang)
    println(m++m1)//Map(2 -> bryant, 4 -> jay, 1 -> kobe, 3 -> zhangyongpeng)
    m.keys.foreach(x=>print(m(x)))//bryantkobezhang
    println(m.contains(3))//true
    m-=1
    println(m)//Map(2 -> bryant, 3 -> zhang)
  }
}
