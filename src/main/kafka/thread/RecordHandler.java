package kafka.thread;

import kafka.pojo.Person;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecordHandler implements Runnable {

    private static final Logger logger = LogManager.getLogger(RecordHandler.class);
    private final ConsumerRecords<String, Person> records;
    private final Map<TopicPartition, OffsetAndMetadata> offsets;
    private final Map<ConsumerRecords<String, Person>, String> recordsToFlag;

    public RecordHandler(ConsumerRecords<String, Person> records, Map<ConsumerRecords<String, Person>, String> recordsToFlag, Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.records = records;
        this.offsets = offsets;
        this.recordsToFlag = recordsToFlag;
    }

    @Override
    public void run() {
        Set<TopicPartition> partitions = records.partitions();
        for (TopicPartition tp : partitions
        ) {
            List<ConsumerRecord<String, Person>> records = this.records.records(tp);
            for (ConsumerRecord<String, Person> record : records
            ) {
                logger.info("topic = " + record.topic() + ", partition = " + record.partition()
                        + ", offset = " + record.offset());
                logger.info("key = " + record.key() + ", value = " + record.value());
            }
            long lastConsumerOffset = records.get(records.size() - 1).offset();
            synchronized (offsets) {
                if (offsets.containsKey(tp)) {
                    long position = offsets.get(tp).offset();
                    if (position < lastConsumerOffset + 1) {
                        offsets.put(tp, new OffsetAndMetadata(lastConsumerOffset + 1));
                    }
                } else {
                    offsets.put(tp, new OffsetAndMetadata(lastConsumerOffset + 1));
                }
            }
        }
        recordsToFlag.put(records, "1");
    }

}
