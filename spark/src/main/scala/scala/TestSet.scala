package scala

object TestSet {
  def main(args: Array[String]): Unit = {
    var s = Set(1,2,3)
    println(s.getClass.getName)//scala.collection.mutable.HashSet
    //println(s.add(4))
    println(s+=4)//Set(1, 2, 3, 4)
    //println(s.remove(2))
    println(s -= 2)//Set(1, 3, 4)
    val s1 = Set(3,5,8)
    println(s ++ s1)//Set(1, 5, 3, 4, 8)
    println(s.&(s1))//Set(3)
    println(s.&~(s1))//Set(1, 4)
    println(s.diff(s1))//Set(1, 4)
    println(s.+(2,6,3))//Set(1, 2, 6, 3, 4)
    println(s.-(4,1))//Set(3)
    println(s)//Set(1, 3, 4)
    println(s.addString(new StringBuilder,"").toString())//134
    println(s.apply(1))//true
    println(s.count(x => x==x))//3
    println(s.find(x => x%2==0))//Some(4)
    println(s.forall(x => x%2==0))//false
    println(s.product)//12
    println(s.size)//3
    println(s.splitAt(2))//(Set(1, 3),Set(4))
    println(s)



  }
}
