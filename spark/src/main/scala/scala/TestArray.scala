package scala

import scala.Array.{concat, fill, ofDim, range}
import scala.util.Random

object TestArray {

  def main(args: Array[String]): Unit = {
    val arr = new Array[Int](3) //创建长度为3的数组
    arr(0) = 3
    arr(1) = 23
    arr(2) = 4
    arr.foreach(print(_))//3234
    println()
    for(i <- arr) print(i)//3234
    println()
    for(i <- arr.indices if i%2==0) print(arr(i))//indices index的复数  34
    println()
    for (i <- arr.indices) print(arr(i))//3234
    println(arr.sum)//30
    println(arr.max)//23
    println(arr.min)//3
    println(arr.sorted.mkString("").equals("3423"))//true
    println(arr.sortBy(x => x.toString.length).mkString(""))//3423
    val arr1 = Array(1,4,7)
    for(i <- concat(arr1,arr)) print(i+" ")//1 4 7 3 23 4
    println()
    val arr2 = ofDim[Int](2, 3)
    for(i <- 0 to 1){
      for(j <- 0 to 2){
        arr2(i)(j) = Random.nextInt(24)
      }
    }
    for(i <- 0 to 1){
      for(j <- 0 to 2){
        print(arr2(i)(j)+" ")//11 20 14    10 3 8
      }
      println()
    }

    val arr3 = range(10,20)
    println(arr3.mkString(""))//10111213141516171819
    val arr4 = range(10,20,2)
    println(arr4.mkString(""))//1012141618
    val arr5 = Array.iterate(0,4)(x => x+3)
    println(arr5.mkString(","))//0,3,6,9
    val arr6 = fill(3)(7)
    println(arr6.mkString(" "))//7 7 7
    val arr7 = fill(2,3)(5,6,7)
    for(i <- arr7){
      println(i.mkString(" "))//(5,6,7) (5,6,7) (5,6,7)
    }
    val arr8 = Array.tabulate(5,3)((x,y) => x+" "+y)
    for(i <- arr8){
      println(i.mkString(" "))//(5,6,7) (5,6,7) (5,6,7)
    }/*0 0 0 1 0 2
    1 0 1 1 1 2
    2 0 2 1 2 2
    3 0 3 1 3 2
    4 0 4 1 4 2*/



  }
}
