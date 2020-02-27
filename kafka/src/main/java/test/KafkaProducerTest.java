package test;

import interceptor.PersonProducerInterceptor1;
import interceptor.PersonProducerInterceptor2;
import partitioner.PersonPartitioner;
import pojo.Person;
import serializer.PersonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Create By Zhangyp
 * Date:  2019/10/23
 * Desc:
 */
public class KafkaProducerTest {

    private static final String brokerList = "zyp-1:9092,zyp-2:9092,zyp-3:9092";

    private static final String topic = "person";

    private static final String clientId = "Producer-01";

    private static final Logger logger = LogManager.getLogger(KafkaProducerTest.class);

    private static Properties initConfig() {
        logger.info("1");
        Properties props = new Properties();

        //key序列化工具
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //value序列化工具
        //props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PersonSerializer.class.getName());

        //使用自定义分区器
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, PersonPartitioner.class.getName());

        //使用自定义拦截器,多个拦截器用  , 隔开,顺序执行。
        //在拦截链中，如果某个拦截器执行失败，那么下一个拦截器会接着从上－个执行成功的拦截器继续执行
        //拦截器报错不会影响消息的发送，发送依然会成功
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, PersonProducerInterceptor1.class.getName() + "," + PersonProducerInterceptor2.class.getName());

        //broker链接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);

        //客户端ID
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);

        //分区中必须要有多少个副本收到这条消息，之后生产者才会认为这条消息是成功写入的。
        // 1=leader写入成功，0=发送出去即成功 -1或true=所有副本写入成功
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        //生产者客户端能发送的消息的最大值，默认值为 1048576B ，即 lMB,需要小于等于服务端的message.max.bytes，不然报错RecordTooLargeException，服务端接收不了
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 102400);

        //消息发送失败后重试次数，但是如上面提到的消息过大这个不可重试的错误，是不会重试的,直接抛出异常
        props.put(ProducerConfig.RETRIES_CONFIG, 5);

        //两次重试之间的时间间隔，默认100
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 50);

        return props;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        Properties props = initConfig();

        //创建生产者实例
        //KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        KafkaProducer<String, Person> producer = new KafkaProducer<>(props);

        //构建消息必须制定主题跟消息内容，还可以使用其他构造方法添加key，headers等
        Person person = Person.builder().name("张永鹏").sex("男").build();
        //ProducerRecord<String, String> record = new ProducerRecord<>(topic, person.toString());
        //ProducerRecord<String, Person> record = new ProducerRecord<>(topic, 1,"张永鹏",person);
        ProducerRecord<String, Person> record = new ProducerRecord<>(topic, person);

        //发后既忘，即不关心消息是否到达，注意send()方法返回值不是void是Future<RecordMetadata>
        //producer.send(record);

        //同步发送消息,调用Future的get()方法进行阻塞，直到接收到返回值
        //producer.send(record).get();
        //或者
        //Future<RecordMetadata> future = producer.send(record);
        //RecordMetadata metadata = future.get(1000, TimeUnit.MILLISECONDS);
        //System.out.println("topic" + metadata.topic() + ", partition:" + metadata.partition() + ", offset:" + metadata.offset() + ", timestamp" + metadata.timestamp());

        //异步发送消息
        //producer.send(recordl, callbackl);
        //producer.send(record2, callback2);
        //对于同一个 分区而言，如果消息recordl于record2之前先发送，
        //那么KafkaProducer 就可以保证对应的callbackl在callback2之前调用，也就是说，回调函数的调用也可以保证分区有序。
        producer.send(record, (metadata1, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.println("topic:" + metadata1.topic() + ", partition:" + metadata1.partition() + ", offset:" + metadata1.offset() + ", timestamp" + metadata1.timestamp());
            }
        });

        //回收占用资源会阻塞等待之前所有的发送请求完成后再关闭KafkaProducer。
        producer.close();
        //等待1000ms后强行关闭，不管KafkaProducer是否把所有消息发送完毕，不推荐
        //producer.close(1000, TimeUnit.MILLISECONDS);
    }
}
