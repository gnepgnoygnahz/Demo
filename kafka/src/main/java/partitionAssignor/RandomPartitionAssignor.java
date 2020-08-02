package partitionAssignor;

import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * @ClassName RandomPartitionAssigment
 * @Description TODO 随机分配策略
 * @Author zhangyp
 * @Date 2020/6/27 15:05
 * @Version 1.0
 */
public class RandomPartitionAssignor extends AbstractPartitionAssignor {
    /**
     * @param partitionsPerTopic 主题名称与分区数
     * @param subscription       消费者id与消费者的订阅信息，Subscription类中有两个属性：topics和userData,分别表示消费者的订阅主题列表和用户自定义信息。
     * @return
     */
    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic, Map<String, Subscription> subscriptions) {
        Map<String, List<String>> consumesPerTopic = consumesPerTopic(subscriptions);
        Map<String, List<TopicPartition>> assigment = new HashMap<>();
        for (String memberId : subscriptions.keySet()) {
            assigment.put(memberId, new ArrayList<>());
        }
        for (Map.Entry<String, List<String>> topicEntry : consumesPerTopic.entrySet()) {
            String topic = topicEntry.getKey();
            //获得分区数
            Integer numPartitionsForTopic = partitionsPerTopic.get(topic);
            if (numPartitionsForTopic == null) continue;
            //当前主题下的所有分区
            List<TopicPartition> partitions = AbstractPartitionAssignor.partitions(topic, numPartitionsForTopic);
            //获得当前主题的所有消费者
            List<String> consumersForTopic = topicEntry.getValue();
            int consumerSize = consumersForTopic.size();

            for (TopicPartition partition : partitions) {
                int rand = new Random().nextInt(consumerSize);
                String randomConsumer = consumersForTopic.get(rand);
                assigment.get(randomConsumer).add(partition);
            }

        }
        return assigment;
    }

    /**
     * RangeAssignor对应的protocol_name为"range", RoundRobinAssignor对应的protocol—name
     * 为"roundrobin", Sticky Assignor对应的protocol_name为"sticky"
     *
     * @return 配策略的名称
     */
    @Override
    public String name() {
        return "random";
    }

    /**
     * 获取每个主题对应的消费者列表
     *
     * @param consumerMetadata
     * @return
     */
    private Map<String, List<String>> consumesPerTopic(Map<String, Subscription> consumerMetadata) {
        Map<String, List<String>> res = new HashMap<>();
        for (Map.Entry<String, Subscription> subscriptionEntry : consumerMetadata.entrySet()) {
            String consumerId = subscriptionEntry.getKey();
            for (String topic : subscriptionEntry.getValue().topics()) {
                put(res, topic, consumerId);
            }
        }
        return res;
    }
}
