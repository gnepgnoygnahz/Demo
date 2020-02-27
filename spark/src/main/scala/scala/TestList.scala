package scala

object TestList {
  def main(args: Array[String]): Unit = {
    val str = List("kobe","bryant","zhang","yong","peng")
    val str1 = "kobe" :: ("bryant" :: ("zhang" :: ("yong" :: ("peng" :: Nil))))
    val num = List(2,"hehe",8)
    val num1 = 2 :: 4 :: 8 :: Nil
    val empty = List()
    val ls2 = List(List(1,2,3),List(2,3,4))
    val ls21 = List(1,2,3) :: List(2,3,4) :: Nil
    println(ls2)//List(List(1, 2, 3), List(2, 3, 4))
    println(ls21(1)(2))//4
    println(str.head)//kobe
    println(str1.tail)//List(bryant, zhang, yong, peng)
    println(num.isEmpty)//false
    println(num1.reverse)//List(8, 4, 2)
    println(empty.isEmpty)//true
    println(str.grouped(3))
    val strAndNum = str ::: num
    //val strAndNum = str.:::(num)
    //val strAndNum = List.concat(str,num)
    println(strAndNum)//List(kobe, bryant, zhang, yong, peng, 2, 4, 8)
    println(List.fill(2)("hehe"))
    println(List.tabulate(2,3)(_+_+1))//List(List(1, 2, 3), List(2, 3, 4))
    println(10 +: num)//List(10, 2, hehe, 8)
    println(num :+ 10)//List(2, hehe, 8, 10)
    val strsb = new StringBuilder("kobe")
    println(num.addString(strsb,","))//kobe2,hehe,8
    println(num.apply(1))//hehe
    println(num.drop(2))//List(8)
    println(num.dropRight(4))//List()
    val dwnums = List(2,5,6)
    println(dwnums.dropWhile(x => x%2 == 0))//List(5, 6)
    println(dwnums.filter(_%2==0))//List(2, 6)
    println(num.forall(x => x == 2))//判断所有元素都等于2  false
    num.foreach(x => print(x+" "))//2 hehe 8
    println(num.head)//2
    println(num.last)//8
    println(num.init)//List(2, hehe)
    println(num.intersect(dwnums))//List(2)
    println(num.take(2))//List(2, hehe)
    println(num.takeRight(2))//List(hehe, 8)
    println(num.toBuffer)//ArrayBuffer(2, hehe, 8)
    println(num.toArray.mkString(" "))//2 hehe 8
    println(List((1,"kobe"),(2,"bryant")).toMap)//Map(1 -> kobe, 2 -> bryant)
    println(num.toSet)//Set(2, hehe, 8)
    println(num.toSeq)//List(2, hehe, 8)
    println(num.toString)//List(2, hehe, 8)

  }
}
