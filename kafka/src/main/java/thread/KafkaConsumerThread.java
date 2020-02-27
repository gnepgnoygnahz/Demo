package thread;

import pojo.Person;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KafkaConsumerThread extends Thread {
    private final List<ConsumerRecords<String, Person>> recordsWindows = new ArrayList<>();
    private final Map<ConsumerRecords<String, Person>, String> recordsToFlag = new HashMap<>();
    private final Map<ConsumerRecords<String, Person>, Map<TopicPartition, OffsetAndMetadata>> recordsToOffset = new HashMap<>();

    private KafkaConsumer<String, Person> consumer;
    private ExecutorService threadPol;


    public KafkaConsumerThread(Properties prop, Collection<String> topic, Integer threadNum) {
        consumer = new KafkaConsumer<>(prop);
        consumer.subscribe(topic);
        threadPol = new ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void run() {
        try {
            while (true) {
                /*synchronized (offsets) {
                    if (!offsets.isEmpty()) {
                        consumer.commitSync(offsets);
                        offsets.clear();
                    }
                }*/
                while (recordsWindows.size() > 0) {
                    if ("1".equals(recordsToFlag.get(recordsWindows.get(0)))) {
                        consumer.commitSync(recordsToOffset.get(recordsWindows.get(0)));
                        recordsWindows.remove(recordsWindows.get(0));
                    } else {
                        break;
                    }
                }
                int windowSize = 5;
                while (recordsWindows.size() < windowSize) {
                    ConsumerRecords<String, Person> records = consumer.poll(10);
                    if (!records.isEmpty()) {
                        //threadPol.submit(new RecordHandler(records, offsets));
                        recordsWindows.add(records);
                    }
                    recordsToFlag.put(records, "0");
                    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                    recordsToOffset.put(records, offsets);
                    threadPol.submit(new RecordHandler(records, recordsToFlag, offsets));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
