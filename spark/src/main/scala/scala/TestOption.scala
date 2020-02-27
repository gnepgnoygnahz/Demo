package scala

object TestOption {
  def main(args: Array[String]): Unit = {
    val m = Map((1, "kobe"), (2, "bryant"))
    println(m.get(1)) //Some(kobe)
    println(m.get(1).isEmpty) //false
    println(m.get(3)) //None
    println(m.get(3).isEmpty) //true
    println(show(m.get(2))) //bryant
    println(m.getOrElse(4, "hehe"))//hehe
    val o: Option[(Int, String, Boolean)] = Some(2, "hehe", false)
    println(None.orElse(o))//Some((2,hehe,false))
    println(o.get) //(2,"hehe",false)
    println(o.productArity) //1
    println(o.productElement(0)) //(2,"hehe",false)
    println(o.exists(_._3 == false)) //true
    println(o.filter(_._1 % 2 == 0)) //Some((2,hehe,false))
    println(o.filterNot(_._1 % 2 == 0)) //None
    o.foreach(println(_)) //(2,hehe,false)
    println(Some(8.0).isDefined)
    println(o.iterator.next())//(2,hehe,false)
    println(None.orNull)
  }

  def show(x: Option[String]) = x match {
    case Some(s) => s
    case None => "none"
  }
}
