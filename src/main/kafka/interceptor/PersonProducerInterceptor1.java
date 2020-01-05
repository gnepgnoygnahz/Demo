package kafka.interceptor;

import kafka.pojo.Person;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Create By Zhangyp
 * Date:  2019/11/9
 * Desc:
 */
//在这个类中抛出的异常都会被捕获并记录到日志中，但并不会再向上传递。
public class PersonProducerInterceptor1 implements ProducerInterceptor<String, Person> {

    private volatile long sendSuccess = 0;
    private volatile long sendFailure = 0;
    private static final Logger logger = LogManager.getLogger(PersonProducerInterceptor1.class);

    //消息序列化和计算分区之前会调用
    @Override
    public ProducerRecord<String, Person> onSend(ProducerRecord<String, Person> record) {
        String newName = record.value().getName() + "kobe";
        record.value().setName(newName);
        return new ProducerRecord<String, Person>(record.topic(), record.partition(), record.timestamp(), record.key(), record.value(), record.headers());
    }

    //消息被应答（Acknowledgement）之前或消息发送失败时调用，优先于用户设定的Callback之前执行。
    //这个方法运行在Producer的I/O线程中，所以这个方法中实现的代码逻辑越简单越好，否则会影响消息的发送速度
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (null == exception) {
            sendSuccess++;
        } else {
            sendFailure++;
        }
    }

    //主要用于在关闭拦截器时执行一些资源的清理工作
    @Override
    public void close() {
        double successRatio = (double) sendSuccess / (sendFailure + sendSuccess);
        logger.info(this.getClass().getName() + "===发送成功率=" + String.format("%f", successRatio * 100) + "%");
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
