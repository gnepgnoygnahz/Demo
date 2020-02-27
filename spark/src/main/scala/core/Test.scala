package core

import org.apache.spark.{SparkConf, SparkContext}

object Test {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WC").setMaster("local")
    val sc = new SparkContext(conf)
   /* val rdd = sc.textFile("D:/a.txt",3)
    rdd.cache()//等于rdd.persist(StorageLevel.MEMORY_ONLY)
   // rdd.persist(StorageLevel.MEMORY_ONLY_2)//两个备份
   // sc.setCheckpointDir("hdfs://kobe-01:9000/checkpoint")
   // rdd.checkpoint()
    rdd.first()// String = zhang yong peng
    rdd.count()//Long = 6
    rdd.reduce(_+_)//String = zhang hong liangkobe bryantjay chou zhang yong pengchen yue hong
    rdd.take(3)//Array[String] = Array(zhang yong peng, chen yue hong, zhang hong liang)
    rdd.top(3)//Array(zhang yong peng, zhang hong liang, kobe bryant)  默认降序
    rdd.takeOrdered(3)//Array[String] = Array(" ", chen yue hong, jay chou)  与top相反

    val rdd1 = rdd.map(_.split("\\s+")).collect()
    rdd1.toList.foreach(_.foreach( x=> print(x+"-")))
    println()
    val rdd2 = rdd.flatMap(_.split("\\s")).collect()
    rdd2.foreach(t => print(t+"-"))
    println()
    val rdd3 = rdd.flatMap(_.split("\\s")).distinct().collect()
    rdd3.foreach(t => print(t+"-"))
    println()
    println(rdd.partitions.size)
    val rdd4 = rdd.coalesce(4)//比原分区数目大时候需指定第二个参数为true
    println(rdd4.partitions.size)//3
    val rdd5 = rdd.coalesce(4,true)
    println(rdd5.partitions.size)//4
    val rdd6 = rdd.repartition(4)//repartition等同于coalesce(numPartitions, shuffle = true)
    println(rdd6.partitions.size)//4
    val rdd7 = rdd.flatMap(_.split("\\s+")).map((_,1))// Array((zhang,1), (yong,1), (peng,1), (chen,1), (yue,1), (hong,1), (zhang,1), (hong,1), (liang,1), (kobe,1), (bryant,1), (jay,1), (chou,1))
    println(rdd7)
    rdd7.dependencies.foreach{dep =>
       println("dependency type:"+dep.getClass)
       println("dependency RDD:"+dep.rdd)
       println("dependency partitions:"+dep.rdd.partitions)
       println("dependency partitions size:"+dep.rdd.partitions.size)
    }
    val rdd8 = rdd7.reduceByKey(_+_)//返回值是个RDD Array((peng,1), (yue,1), (kobe,1), (chen,1), (jay,1), (yong,1), (bryant,1), (liang,1), (zhang,2), (hong,2), (chou,1))
    rdd7.reduceByKeyLocally(_+_)//返回值是个Map  scala.collection.Map[String,Int] = Map(hong -> 2, chen -> 1, jay -> 1, kobe -> 1, chou -> 1, bryant -> 1, yong -> 1, yue -> 1, peng -> 1, zhang -> 2, liang -> 1)
    println(rdd8)
    rdd8.dependencies.foreach(dep =>{
      println("dependency type:"+dep.getClass)
      println("dependency RDD:"+dep.rdd)
      println("dependency partitions:"+dep.rdd.partitions)
      println("dependency partitions size:"+dep.rdd.partitions.size)
    })

    val a = sc.parallelize(1 to 9 , 3)
    def iterfunc[T](iter: Iterator[T]) ={
      var res = List[(T,T)]()
      var per = iter.next
      while(iter.hasNext){
        val cur = iter.next
        //(per,cur) :: res
        res ::= (per,cur)
        per = cur
      }
      res.iterator
    }
    val rdd9 = a.mapPartitions(iterfunc).collect()//Array[(Int, Int)] = Array((2,3), (1,2), (5,6), (4,5), (8,9), (7,8))
    println("rdd9:"+rdd9)
    rdd9.foreach(print)
    println()
    println(rdd.partitioner)
    val rdd10 = rdd.flatMap( x => x.split("\\s")).map((_,1)).groupByKey(new HashPartitioner(4))
    //  Array((zhang,CompactBuffer(1, 1)), (hong,CompactBuffer(1, 1)), (yue,CompactBuffer(1)), (liang,CompactBuffer(1)), (peng,CompactBuffer(1)), (chen,CompactBuffer(1)), (bryant,CompactBuffer(1)), (jay,CompactBuffer(1)), (yong,CompactBuffer(1)), (kobe,CompactBuffer(1)), (chou,CompactBuffer(1)))
    println(rdd10.partitioner)
    val collect = Seq((1 to 10, Seq("master","slave1")),(11 to 15 , Seq("slave2","slave3")))
    val rdd11 = sc.makeRDD(collect)
    println(rdd11.partitions.size)
    println(rdd11.preferredLocations(rdd11.partitions(0)))
    println(rdd11.preferredLocations(rdd11.partitions(1)))
    val rdd12 = sc.makeRDD(collect,4)
    println(rdd12.partitions.size)
    println(rdd12.preferredLocations(rdd12.partitions(0)))
    println(rdd12.preferredLocations(rdd12.partitions(1)))
    println(rdd12.preferredLocations(rdd12.partitions(2)))
    println(rdd12.preferredLocations(rdd12.partitions(3)))
    val splitRDD = rdd.randomSplit(Array(0.1,0.2,0.3,0.4,0.5))
    println(splitRDD.size)
    splitRDD(0).collect().foreach(t => print("1:"+t))//Array[String] = Array(kobe bryant)
    splitRDD(1).collect().foreach(t => print("2:"+t))//Array[String] = Array(zhang yong peng)
    splitRDD(2).collect().foreach(t => print("3:"+t))//Array[String] = Array(zhang hong liang, jay chou)
    splitRDD(3).collect().foreach(t => print("4:"+t))//Array[String] = Array()
    splitRDD(4).collect().foreach(t => print("5:"+t))//Array[String] = Array(chen yue hong, " ")
    println()
    val rdd13 = rdd.glom().collect()//Array(Array(zhang yong peng, chen yue hong, zhang hong liang), Array(kobe bryant, jay chou, " "))
    rdd13.foreach(x => x.foreach(y => print(y+"-")))
    println()
    val union1 = sc.makeRDD(1 to 2,1)
    val union2 = sc.makeRDD(2 to 3,1)
    val rdd14 = union1.union(union2).collect()//Array[Int] = Array(1, 2, 2, 3)
    val rdd15 = union1.intersection(union2).collect()//Array[Int] = Array(2)
    val rdd16 = union1.subtract(union2).collect()//Array[Int] = Array(1)
    val rdd17 = union2.subtract(union1).collect()//Array[Int] = Array(3)
    val rdd18= rdd.mapPartitions(x => {
      var result = List[String]()
      var i = ""
      while(x.hasNext){
        i+=x.next()
      }
      result.::(i).iterator

    }).collect().foreach( t => print(t+"-"))//Array(zhang yong pengchen yue hong, zhang hong liang, kobe bryantjay chou )
    println()
    val rdd19= rdd.mapPartitionsWithIndex((x,iter) => {
      var result = List[(Int,String)]()
      var i = ""
      while(iter.hasNext){
        i+=iter.next()
      }
      ((x,i) :: result).iterator
    })
    rdd19.collect().foreach( t => print(t+"-"))// Array((0,zhang yong pengchen yue hong), (1,zhang hong liang), (2,"kobe bryantjay chou "))
    println()
    val zipRDD1 = sc.makeRDD(1 to 5,3)
    val zipRDD2 = sc.makeRDD(Seq("A","B","C","D","E"),3)
    val rdd20 = zipRDD1.zip(zipRDD2).collect().foreach( t => print(t+"-"))//分区数元素个数必须相同  Array[(Int, String)] = Array((1,A), (2,B), (3,C), (4,D), (5,E))
    println()
    val rdd21 = zipRDD1.zipWithIndex().collect().foreach( t => print(t+"-"))//Array((1,0), (2,1), (3,2), (4,3), (5,4))
    println()
    val rdd22 = zipRDD2.zipWithUniqueId().collect().foreach( t => print(t+"-"))//Array[(String, Long)] = Array((A,0), (B,1), (C,4), (D,2), (E,5))
    //zip规则：每个分区中第一个元素唯一ID为该分区的索引号，每个分区中第N个元素为前一个元素的ID值加上分区个数
    //rdd分区数为3
    //第一个分区：A 索引 0 (A,0)
    //第二个分区：B、C 索引 1 (B,1)  (C,1+3)
    //第三个分区：D、E 索引 2 (D,2)  (E,2+3)
    println()
    def testPartitionBy(tuple:(Int,Iterator[(Int,String)])) = {
      var part_name = mutable.HashMap[Int,String]()
      while(tuple._2.hasNext){
        var name = tuple._2.next()._2.split("\\s")
        name.foreach(word => {
          if(part_name.contains(tuple._1)){
            val words = part_name(tuple._1)+word
            part_name(tuple._1) = words
          }else{
            part_name(tuple._1) = word
          }
        })
      }
      part_name.iterator
    }
    rdd19.mapPartitionsWithIndex(
      (partIdx,iter) => testPartitionBy(partIdx,iter)
    )//Array[(Int, String)] = Array((0,zhangyongpengchenyuehong), (1,zhanghongliang), (2,kobebryantjaychou))
    val rdd23 = rdd19.partitionBy(new HashPartitioner(2))
    rdd23.mapPartitionsWithIndex(
      (partIdx,iter) => testPartitionBy(partIdx,iter)
    )//Array[(Int, String)] = Array((0,zhangyongpengchenyuehongkobebryantjaychou), (1,zhanghongliang))
    rdd19.mapPartitionsWithIndex(
      (partIdx,iter) => {
        var part_map = mutable.HashMap[String,List[(Int,String)]]()
        var part_name = "part_"+partIdx
        var elem = iter.next()
        if(part_map.contains(part_name)){
          var elems = part_map(part_name)
          elem :: elems
          part_map(part_name) = elems
        }else{
          part_map(part_name) = List[(Int,String)](elem)
        }
        part_map.iterator
      }
    )//Array[(String, List[(Int, String)])] = Array((part_0,List((0,zhang yong pengchen yue hong))), (part_1,List((1,zhang hong liang))), (part_2,List((2,"kobe bryantjay chou "))))
    val rdd24 = rdd19.mapValues(x => x+"$").collect()//Array[(Int, String)] = Array((0,zhang yong pengchen yue hong$), (1,zhang hong liang$), (2,kobe bryantjay chou $))
    val rdd25 = rdd19.flatMapValues(x => x+"$").collect()//Array((0,z), (0,h), (0,a), (0,n), (0,g), (0, ), (0,y),.... (0,$)......)
    val combineRDD = sc.makeRDD(Array(("A","1"),("A","2"),("A","3"),("A","4"),("A","5"),("B","1"),("B","2"),("B","3"),("B","4"),("B","5")),3)
    combineRDD.lookup("A")//Seq[String] = WrappedArray(1, 2, 3, 4, 5)
    combineRDD.countByKey()//scala.collection.Map[String,Long] = Map(B -> 5, A -> 5)
    combineRDD.sortBy(x => x._1,false)//Array[(String, String)] = Array((B,1), (B,2), (B,3), (B,4), (B,5), (A,1), (A,2), (A,3), (A,4), (A,5))
    combineRDD.sortBy(x=>x._2)// Array[(String, String)] = Array((A,1), (B,1), (A,2), (B,2), (A,3), (B,3), (A,4), (B,4), (A,5), (B,5))
    combineRDD.foreachPartition{
      iter => {
        var string = ""
        while(iter.hasNext){
          string += iter.next()
        }
        println(string)
      }
    }//(A,4)(A,5)(B,1)    (B,2)(B,3)(B,4)(B,5)   (A,1)(A,2)(A,3)
    val rdd26 = combineRDD.combineByKey(
      (v:String) => v+"_",
      (c:String,v:String) => c+"@"+v,
      (c1:String,c2:String) => c1+"$"+c2
    ).collect()//Array((B,1_$2_@3@4@5), (A,1_@2@3$4_@5))
    val rdd27 = combineRDD.foldByKey("-")(_+_).collect()//Array((B,-1-2345), (A,-123-45))   每个分区中第一次遇到该key时会应用一下初始值
    val cogropRDD1 = sc.makeRDD(Array(("A","1"),("B","2"),("C","3")),2)
    val cogropRDD2 = sc.makeRDD(Array(("A","a"),("C","c"),("D","d")),3)
    val cogropRDD3 = sc.makeRDD(Array(("A","A"),("E","E")),2)
    val rdd28 = cogropRDD1.cogroup(cogropRDD2).collect()// Array[(String, (Iterable[String], Iterable[String]))] = Array((B,(CompactBuffer(2),CompactBuffer())), (C,(CompactBuffer(3),CompactBuffer(c))), (A,(CompactBuffer(1),CompactBuffer(a))), (D,(CompactBuffer(),CompactBuffer(d))))
    val rdd29 = cogropRDD1.cogroup(cogropRDD2,cogropRDD3).collect()// Array[(String, (Iterable[String], Iterable[String], Iterable[String]))] = Array((B,(CompactBuffer(2),CompactBuffer(),CompactBuffer())), (E,(CompactBuffer(),CompactBuffer(),CompactBuffer(E))), (C,(CompactBuffer(3),CompactBuffer(c),CompactBuffer())), (A,(CompactBuffer(1),CompactBuffer(a),CompactBuffer(A))), (D,(CompactBuffer(),CompactBuffer(d),CompactBuffer())))
    val rdd30 = cogropRDD1.join(cogropRDD2).collect()//Array[(String, (String, String))] = Array((C,(3,c)), (A,(1,a)))
    val rdd31 = cogropRDD1.fullOuterJoin(cogropRDD2).collect()// Array[(String, (Option[String], Option[String]))] = Array((B,(Some(2),None)), (C,(Some(3),Some(c))), (A,(Some(1),Some(a))), (D,(None,Some(d))))
    val rdd32 = cogropRDD1.leftOuterJoin(cogropRDD2).collect()// Array[(String, (String, Option[String]))] = Array((B,(2,None)), (C,(3,Some(c))), (A,(1,Some(a))))
    val rdd33 = cogropRDD1.rightOuterJoin(cogropRDD2).collect()// Array[(String, (Option[String], String))] = Array((C,(Some(3),c)), (A,(Some(1),a)), (D,(None,d)))
    val rdd34 = cogropRDD1.subtractByKey(cogropRDD2).collect()//Array[(String, String)] = Array((B,2))
    val aggregateRDD = sc.makeRDD(1 to 10,3)
    aggregateRDD.aggregate("-")((x:String,y:Int) => x+y,(a:String,b:String) => a+b)//String = --78910-123-456    每个分区都会应用一次初始值，合并的时候再次应用初始值
    aggregateRDD.fold(1)((x,y) => x+y)//Int = 59
    def x = {println(1);false}
    def y = {println(2);false}
    if(x && !y){
      println(3)
    }else if(!x && y){
      println(4)
    }else if(x && y){
      println(5)
    }else{
      println(6)
    }*/

    val rddA = sc.makeRDD(1 to 10,3)
    val rddB = rddA.map(x=>(x,x))
    val rddC = sc.makeRDD(1 to 10,3)
    val rddD = rddC.map(x=>(x,x))
    val rddE = rddD.groupBy(_._1)
    val rddF = rddE.filter(_._1%2==1)
    val rddG = rddB.join(rddF)
    rddG.collect()

    sc.stop()
  }
}
