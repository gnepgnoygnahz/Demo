package sql;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.*;

import java.util.Iterator;


public class TestSql {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local[*]");
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
        Dataset<Row> ds = spark.read().json("spark/data/Person.json");
        ds.show();
        ds.createOrReplaceTempView("person");
        spark.udf().register("avgAge", new MyAggregateUDFWeak());
        Dataset<Row> sql = spark.sql("select avgAge(age) from person");
        //Row[] rows = sql.collect();
        Row[] rows = (Row[])sql.take(1);
        Row row = rows[0];
        System.out.println(row.getDouble(0));

        //JavaSparkContext context = new JavaSparkContext(spark.sparkContext());
        JavaSparkContext context = JavaSparkContext.fromSparkContext(spark.sparkContext());
        JavaRDD<String> rdd = context.textFile("spark/data/Person.txt", 3);

        //JavaRDD<String> rdd = spark.read().textFile("spark/data/Person.txt").javaRDD();

        rdd.foreachPartition(new VoidFunction<Iterator<String>>() {
            @Override
            public void call(Iterator<String> data) throws Exception {
                if (data.hasNext()) {
                    String[] split = data.next().split(",");
                    System.out.println(new PersonJava(Long.valueOf(split[0]), split[1], Long.valueOf(split[2])));
                }
            }
        });

        JavaRDD<PersonJava> personRDD = rdd.map(new Function<String, PersonJava>() {
            @Override
            public PersonJava call(String data) throws Exception {
                String[] split = data.split(",");
                return new PersonJava(Long.valueOf(split[0]), split[1], Long.valueOf(split[2]));
            }
        });

        Dataset<Row> personDF = spark.createDataFrame(personRDD, PersonJava.class);
        personDF.foreachPartition(new ForeachPartitionFunction<Row>() {
            @Override
            public void call(Iterator<Row> t) throws Exception {
                while (t.hasNext()) {
                    Row row = t.next();
                    //TODO 字段顺序跟插入顺序不一样，内部重新排序了
                    System.out.println(row.getLong(0) + ", " + row.getString(2) + ", " + row.getLong(1));
                }
            }
        });


        Encoder<PersonJava> personEncoder = Encoders.bean(PersonJava.class);
        Dataset<PersonJava> personDS = spark.createDataset(personRDD.rdd(), personEncoder);
        personDS.foreachPartition(new ForeachPartitionFunction<PersonJava>() {
            @Override
            public void call(Iterator<PersonJava> t) throws Exception {
                while (t.hasNext()) {
                    System.out.println(t.next());
                }
            }
        });

        /*JavaRDD<Row> rowRDD = rdd.map(new Function<String, Row>() {
            @Override
            public Row call(String data) throws Exception {
                String[] split = data.split(",");
                return RowFactory.create(Long.valueOf(split[0]), split[1], Long.valueOf(split[2]));
            }
        });
        ArrayList<StructField> fields = new ArrayList<StructField>();
        StructField field = null;
        field = DataTypes.createStructField("id", DataTypes.LongType, true);
        fields.add(field);
        field = DataTypes.createStructField("name", DataTypes.StringType, true);
        fields.add(field);
        field = DataTypes.createStructField("age", DataTypes.LongType, true);
        fields.add(field);
        StructType schema = DataTypes.createStructType(fields);
        Dataset<Row> ds2 = spark.createDataFrame(rowRDD, schema);
        ds2.show();*/

    }
}


