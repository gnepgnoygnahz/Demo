package scala

object TestString {

  def main(args: Array[String]): Unit = {
    var m1 = new StringBuilder
    m1 += 'k' //+=只能是char类型
    m1 ++= "obe" //++=只能是String类型
    //print(m)与print(m.toString())在idea里面打印出来的结果是一样的但实际上不同，如下面打印返回false
    //m.toString与m.toString()效果一样，估计是因为toString方法没有参数列表
    println(m1.toString())
    val m = "kobe"
    println("kobe".equals(m))//false
    println(m.charAt(3))//e
    println(m.compareTo("koae"))//1
    println(m.compareToIgnoreCase("KOBE"))//0
    println(m.concat(" bryant"))
    //kobe bryant
    val m2 = new StringBuffer("Kobe")
    println(m.contentEquals(m2))
    //true
    val char = Array('z', 'h', 'a', 'n', 'g')
    println(String.copyValueOf(char))//zhang
    println(String.copyValueOf(char,2,2))//an
    println(m.endsWith("e"))//true
    println(m.equals("kobe"))//true
    println(m.equalsIgnoreCase("KoBe"))//true
    println(new String(m.getBytes))//kobe
    println(new String(m.getBytes("UTF-8")))//kobe
    val carr = new Array[Char](10)
    m.getChars(1,3,carr,3)
    println(carr.mkString(","))// , , ,o,b, , , , ,
    println(m.indexOf("o"))

  }
}
