package spark.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Create By Zhangyp
 * Date:  2019/9/30
 * Desc:
 */
public class SparkWordCount {
    private static final Logger logger = LogManager.getLogger(SparkWordCount.class);

    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setAppName("WorldCount");
        conf.setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> rdd = sc.textFile("D:/a.txt");
        JavaRDD<String> rdd1 = rdd.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                List<String> list = new ArrayList<>();
                String[] words = s.split("\\^");
                for (String word :
                        words) {
                    list.add(word);
                }
                return list.iterator();
            }
        });

        JavaPairRDD<String, Integer> rdd2 = rdd1.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        });

        JavaPairRDD<String, Integer> rdd3 = rdd2.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });

        List<Tuple2<String, Integer>> result = rdd3.collect();

        for (Tuple2<String, Integer> tuple :
                result) {
            logger.info(tuple._1 + "-----" + tuple._2);
        }
    }
}
