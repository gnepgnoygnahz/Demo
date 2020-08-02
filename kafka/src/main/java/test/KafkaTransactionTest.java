package test;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName KafkaTransactionTest
 * @Description TODO 事务
 * @Author zhangyp
 * @Date 2020/6/29 19:47
 * @Version 1.0
 */
public class KafkaTransactionTest {
    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConsumerTest.initConfig());
        consumer.subscribe(KafkaConsumerTest.topic);

        KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaProducerTest.initConfig());
        //初始化事务
        producer.initTransactions();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            if (!records.isEmpty()) {
                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                //开启事务
                producer.beginTransaction();
                try {
                    for (TopicPartition partition : records.partitions()) {
                        List<ConsumerRecord<String, String>> partitionRecordes = records.records(partition);
                        for (ConsumerRecord<String, String> record : partitionRecordes) {

                        }
                        long lastConsumedOffset = partitionRecordes.get(partitionRecordes.size() - 1).offset();
                        offsets.put(partition, new OffsetAndMetadata(lastConsumedOffset + 1));
                    }
                    //提交消费位移
                    producer.sendOffsetsToTransaction(offsets, KafkaConsumerTest.groupId);
                    //提交事务
                    producer.commitTransaction();
                } catch (ProducerFencedException e) {
                    e.printStackTrace();
                    //中止事务
                    producer.abortTransaction();
                }
            }
        }
    }
}
