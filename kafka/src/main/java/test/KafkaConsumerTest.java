package test;

import deserializer.PersonDeseralizer;
import interceptor.PersonConsumerInterceptor;
import pojo.Person;
import thread.KafkaConsumerThread;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create By Zhangyp
 * Date:  2019/10/23
 * Desc:
 */
public class KafkaConsumerTest {

    private static final String brokerList = "zyp-1:9092,zyp-2:9092,zyp-3:9092";

    private static final List<String> topic = Arrays.asList("person", "test");

    private static final String groupId = "Group-01";

    private static final String clientId = "Consumer-01";

    private static final AtomicBoolean isRunning = new AtomicBoolean(true);

    private static final Logger logger = LogManager.getLogger(KafkaConsumerTest.class);

    private static Properties initConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PersonDeseralizer.class.getName());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        //关闭消费位移自动提交，默认true
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //使用自定义拦截器,多个拦截器用  , 隔开,顺序执行。
        //在拦截链中，如果某个拦截器执行失败，那么下一个拦截器会接着从上－个执行成功的拦截器继续执行
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, PersonConsumerInterceptor.class.getName());
        return props;
    }

    public static void main(String[] args) {
        Properties props = initConfig();
        KafkaConsumer<String, Person> consumer = new KafkaConsumer<>(props);
        Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
        consumer.subscribe(topic, new ConsumerRebalanceListener() {
            //这个方法会在再均衡开始之前和消费者停止读取消息之后被调用。
            //可以通过这个回调方法来处理消费位移的提交，以此来避免一些不必要的重复消费现象的发生。
            //参数partitions表示再均衡前所分配到的分区。
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                consumer.commitSync(currentOffsets);
                currentOffsets.clear();
            }

            //这个方法会在重新分配分区之后和消费者开始读取消费之前被调用。参数partitions表示再均衡后所分配到的分区。
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

            }
        });
        try {
            while (isRunning.get()) {

                System.out.println(System.currentTimeMillis() + ", 开始拉取数据=============================");

                //多线程实现,需props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
                new KafkaConsumerThread(props, topic, Runtime.getRuntime().availableProcessors()).start();


                //在消费者的缓冲区里没有可用数据时会阻塞2000ms，若设置为0，则方法立即返回，消费者不会分配到任何分区
                //如果对未分配到的分区执行seek()方法，那么会报出IllegalStateException的异常
                //seek()方法只能重置消费者分配到的分区的消费位置，而分区的分配是在poll()方法的调用过程中实现的。
                //也就是说，在执行seek()方法之前需要先执行一次poll()方法，等到分配到分区之后才可以重置消费位置
                consumer.poll(2000);
                Set<TopicPartition> assignment = new HashSet<>();
                while (assignment.size() == 0) {
                    consumer.poll(2000);
                    //获取消费者所分配到的分区信息
                    assignment = consumer.assignment();
                }
                //设置等待获取分区最后偏移量的超时时间为10秒。如果没有指定timeout参数的值，那么endOffsets()方法的等待时间由客户端参数request.timeout.ms来设置，默认值为30000
                Map<TopicPartition, Long> endOffsets = consumer.endOffsets(assignment);
                Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(assignment);
                Map<TopicPartition, Long> timeOffsets = new HashMap<>();
                for (TopicPartition tp : assignment) {

                    consumer.seek(tp, endOffsets.get(tp));
                    consumer.seekToBeginning(Collections.singletonList(tp));

                    consumer.seek(tp, beginningOffsets.get(tp));
                    consumer.seekToEnd(Collections.singletonList(tp));
                    //检索3天前的消息
                    //key为待查询的分区，而value为待查询的时间戳
                    //该方法会返回时间戳大于等于待查询时间的第一条消息对应的位置和时间戳
                    //对应于OffsetAndTimestamp中的offset和timestamp字段
                    timeOffsets.put(tp, System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000);
                }

                Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timeOffsets);
                for (TopicPartition tp : assignment) {
                    OffsetAndTimestamp offsetAndTimestamp = offsets.get(tp);
                    if (offsetAndTimestamp != null) {
                        consumer.seek(tp, offsetAndTimestamp.offset());
                    }
                }

                ConsumerRecords<String, Person> records = consumer.poll(2000);

                for (ConsumerRecord<String, Person> record : records) {
                    System.out.println("topic = " + record.topic() + ", partition = " + record.partition()
                            + ", offset = " + record.offset());
                    System.out.println("key = " + record.key() + ", value = " + record.value());
                }

                //获取消息集合中消息所包含的分区，再获取分区中的所有消息
                for (TopicPartition tp : records.partitions()) {
                    List<ConsumerRecord<String, Person>> partitionRecords = records.records(tp);
                    for (ConsumerRecord<String, Person> record : partitionRecords) {
                        System.out.println("topic = " + record.topic() + ", partition = " + record.partition()
                                + ", offset = " + record.offset());
                        System.out.println("key = " + record.key() + ", value = " + record.value());
                        //同步提交位移，会阻塞直到提交完成
                        //consumer.commitSync();
                        currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                    }
                    //提交所有消费位移
                    consumer.commitSync(currentOffsets);
                    //消费完分区再提交
                    long lastConsumedOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                    consumer.commitSync(Collections.singletonMap(tp, new OffsetAndMetadata(lastConsumedOffset + 1)));
                    //异步提交，不会发生阻塞
                    //consumer.commitAsync();
                    consumer.commitAsync(Collections.singletonMap(tp, new OffsetAndMetadata(lastConsumedOffset + 1)), (offset, exception) -> {
                        if (exception == null) {
                            System.out.println(offset);
                        } else {
                            logger.error("fail to commit offsets ");
                        }
                    });

                    //暂停拉取数据
                    consumer.pause(Collections.singletonList(tp));
                    //恢复拉取数据
                    consumer.resume(Collections.singletonList(tp));
                    //获得已暂停的分区
                    //Set<TopicPartition> paused = consumer.paused();

                    //退出while循环
                    consumer.wakeup();
                    isRunning.set(false);


                    //根据主题名称获取消息
                    for (String topicName : topic) {
                        for (ConsumerRecord<String, Person> record : records.records(topicName)) {
                            System.out.println("topic = " + record.topic() + ", partition = " + record.partition()
                                    + ", offset = " + record.offset());
                            System.out.println("key = " + record.key() + ", value = " + record.value());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                consumer.commitSync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                consumer.close();
                consumer.close(10, TimeUnit.MILLISECONDS);
            }
        }
    }
}