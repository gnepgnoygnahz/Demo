package interceptor;

import pojo.Person;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


import java.util.Map;

/**
 * Create By Zhangyp
 * Date:  2019/11/9
 * Desc:
 */
public class PersonProducerInterceptor2 implements ProducerInterceptor<String, Person> {

    private volatile long sendSuccess = 0;
    private volatile long sendFailure = 0;

    @Override
    public ProducerRecord<String, Person> onSend(ProducerRecord<String, Person> record) {
        String newName = record.value().getName() + "-bryant";
        record.value().setName(newName);
        return new ProducerRecord<String, Person>(record.topic(), record.partition(), record.timestamp(), record.key(), record.value(), record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (null == exception) {
            sendSuccess++;
        } else {
            sendFailure++;
        }
    }

    @Override
    public void close() {
        double successRatio = (double) sendSuccess / (sendFailure + sendSuccess);
        System.out.println(this.getClass().getName() + "===发送成功率=" + String.format("%f", successRatio * 100) + "%");
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
