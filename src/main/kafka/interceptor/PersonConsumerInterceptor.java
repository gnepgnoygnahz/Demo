package kafka.interceptor;

import kafka.pojo.Person;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

//在这个类中抛出的异常都会被捕获并记录到日志中，但并不会再向上传递。
public class PersonConsumerInterceptor implements ConsumerInterceptor<String, Person> {

    //poll()方法返回之前调用拦截器的onConsume()方法来对消息进行相应的定制化操作，
    //比如修改返回的消息内容、按照某种规则过滤消息（可能会减少poll()方法返回的消息的个数）。
    //如果onConsume()方法中抛出异常，那么会被捕获并记录到日志中， 但是异常不会再向上传递。
    @Override
    public ConsumerRecords<String, Person> onConsume(ConsumerRecords records) {
        Set<TopicPartition> partitions = records.partitions();
        Map<TopicPartition, List<ConsumerRecord<String, Person>>> newRecords = new HashMap<>();
        for (TopicPartition tp : partitions
        ) {
            List<ConsumerRecord<String, Person>> tpRecords = records.records(tp);
            List<ConsumerRecord<String, Person>> newTpRecords = new ArrayList<>();
            for (ConsumerRecord<String, Person> record : tpRecords
            ) {
                Person person = record.value();
                if (person.getName().length() % 2 == 1) {
                    newTpRecords.add(record);
                }
            }
            if (newTpRecords.size() > 0) {
                newRecords.put(tp, newTpRecords);
            }
        }
        return new ConsumerRecords<>(newRecords);
    }

    //主要用于在关闭拦截器时执行一些资源的清理工作
    @Override
    public void close() {

    }

    //提交完消费位移之后调用拦截器的onCommit()方法，可以使用这个方法来记录跟踪所提交的位移信息，
    //比如当消费者使用cornmitSync的无参方法时，我们不知道提交的消费位移的具体细节，而使用拦截器的onCornmit()方法却可以做到这一点。
    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        offsets.forEach((tp, offset) -> System.out.println(tp + ":" + offset.offset()));
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
