package kafka.test;

import kafka.admin.AdminUtils;
import kafka.admin.BrokerMetadata;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.convert.Decorators;
import scala.collection.mutable.Buffer;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaAdminClientTest {

    private static final Logger logger = LogManager.getLogger(KafkaAdminClientTest.class);

    private static final String brokerList = "zyp-1:9092,zyp-2:9092,zyp-3:9092";

    private static final List<String> topic = Collections.singletonList("test");

    private AdminClient client;

    @Before
    public void initConfig() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        client = AdminClient.create(props);
    }

    @After
    public void close() {
        client.close();
    }

    /**
     * 创建主题
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void create() throws ExecutionException, InterruptedException {
        NewTopic topic1 = new NewTopic("test1", 3, (short) 3);
        NewTopic topic2 = new NewTopic("test2", 3, (short) 3);
        CreateTopicsResult topics = client.createTopics(Arrays.asList(topic1, topic2));
        topics.all().get();
    }

    /**
     * 以指定分区副本在哪个broker的方式创建主题
     * 我的kafka集群的brokerid在 kafka/config/server.properties 里面配置的 broker.id 是从1开始配置的
     * (1)客户端根据方法的调用创建相应的协议请求，比如创建主题的createTopics方法，其内部就是发送CreateTopicRequest请求。
     * (2)客户端将请求发送至服务端。
     * (3)服务端处理相应的请求并返回响应，比如这个与CreateTopicRequest请求对应的就是CreateTopicResponse。
     * (4)客户端接收相应的响应并进行解析处理。
     * 和协议相关的请求和相应的类基本都在org.apache.kafka.common.requests包下，
     * AbstractRequest和  AbstractResponse是这些请求和响应类的两个基本父类。
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void createWithAssignment() throws ExecutionException, InterruptedException {
        Map<Integer, List<Integer>> replicaseAssignments = new HashMap<>();
        replicaseAssignments.put(0, Arrays.asList(1, 2));
        replicaseAssignments.put(1, Arrays.asList(1, 3));
        replicaseAssignments.put(2, Arrays.asList(3, 1));
        NewTopic assignmentsTopic = new NewTopic("test3", replicaseAssignments);
        CreateTopicsResult topics = client.createTopics(Arrays.asList(assignmentsTopic));
        Map<String, KafkaFuture<Void>> values = topics.values();
        Set<Map.Entry<String, KafkaFuture<Void>>> entries = values.entrySet();
        for (Map.Entry<String, KafkaFuture<Void>> entry : entries) {
            logger.info(entry.getKey());//test3
        }
    }

    /**
     * 以指定分区副本在哪个broker的方式创建主题且带配置信息
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void createWithConfigs() throws ExecutionException, InterruptedException {
        Map<Integer, List<Integer>> replicaseAssignmentsConfigs = new HashMap<>();
        replicaseAssignmentsConfigs.put(0, Arrays.asList(1, 2));
        replicaseAssignmentsConfigs.put(1, Arrays.asList(2, 3));
        replicaseAssignmentsConfigs.put(2, Arrays.asList(3, 1));
        NewTopic assignmentsTopic = new NewTopic("test4", replicaseAssignmentsConfigs);
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "compact");
        assignmentsTopic.configs(configs);
        CreateTopicsResult topics = client.createTopics(Arrays.asList(assignmentsTopic));
        topics.all().get();
    }

    /**
     * 列出所有可用主题
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void listTopics() throws ExecutionException, InterruptedException {
        ListTopicsResult topics = client.listTopics();
        KafkaFuture<Collection<TopicListing>> listings = topics.listings();
        Collection<TopicListing> topicListings = listings.get();
        for (TopicListing topicListing : topicListings) {
            logger.info(topicListing.name() + topicListing.toString());
        }
    }

    /**
     * 查看主题信息
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void describeTopic() throws ExecutionException, InterruptedException {
        DescribeTopicsResult describeTopicsResult = client.describeTopics(Arrays.asList("test3"));
        Map<String, KafkaFuture<TopicDescription>> values = describeTopicsResult.values();
        Set<Map.Entry<String, KafkaFuture<TopicDescription>>> entries = values.entrySet();
        for (Map.Entry<String, KafkaFuture<TopicDescription>> entry : entries) {
            String name = entry.getKey();
            TopicDescription topicDescription = entry.getValue().get();
            logger.info("======== topicName : " + name + "========");
            List<TopicPartitionInfo> partitions = topicDescription.partitions();
            for (TopicPartitionInfo partition : partitions) {
                logger.info("======== partition id :" + partition.partition() + " ========");
                Node leader = partition.leader();
                logger.info("======== leader ======== ");
                logger.info("host : " + leader.host() + ", port ： " + leader.port() + ", id : " + leader.id() + ", rack ：" + leader.rack());
                List<Node> replicas = partition.replicas();
                logger.info("======== replace ========");
                for (Node replica : replicas) {
                    logger.info("host : " + replica.host() + ", port ： " + replica.port() + ", id : " + replica.id() + ", rack ：" + replica.rack());
                }
                logger.info("========isr ========");
                List<Node> isr = partition.isr();
                for (Node node : isr) {
                    logger.info("host : " + node.host() + ", port ： " + node.port() + ", id : " + node.id() + ", rack ：" + node.rack());
                }
            }
        }
    }

    /**
     * 列出主题中所有的配置信息
     * Config(
     * entries=[
     * ConfigEntry(name=compression.type, value=producer, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=message.format.version, value=1.1-IV0, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=file.delete.delay.ms, value=60000, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=leader.replication.throttled.replicas, value=, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=max.message.bytes, value=1000012, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=min.compaction.lag.ms, value=0, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=message.timestamp.type, value=CreateTime, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=min.insync.replicas, value=1, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=segment.jitter.ms, value=0, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=preallocate, value=false, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=min.cleanable.dirty.ratio, value=0.5, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=index.interval.bytes, value=4096, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=unclean.leader.election.enable, value=false, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=retention.bytes, value=-1, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=delete.retention.ms, value=86400000, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=cleanup.policy, value=delete, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=flush.ms, value=9223372036854775807, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=follower.replication.throttled.replicas, value=, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=segment.ms, value=604800000, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=segment.bytes, value=1073741824, source=STATIC_BROKER_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=retention.ms, value=604800000, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=message.timestamp.difference.max.ms, value=9223372036854775807, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=segment.index.bytes, value=10485760, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[]),
     * ConfigEntry(name=flush.messages, value=9223372036854775807, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[])
     * ]
     * )
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void describeTopicConfig() throws ExecutionException, InterruptedException {
        ConfigResource resource1 = new ConfigResource(ConfigResource.Type.TOPIC, "test3");
        ConfigResource resource2 = new ConfigResource(ConfigResource.Type.TOPIC, "person");
        DescribeConfigsResult result = client.describeConfigs(Arrays.asList(resource1, resource2));
        Map<ConfigResource, Config> configMap = result.all().get();
        Config config = configMap.get(resource1);
        logger.info(config.get("max.message.bytes"));
        //ConfigEntry(name=max.message.bytes, value=1000012, source=DEFAULT_CONFIG, isSensitive=false, isReadOnly=false, synonyms=[])
    }

    /**
     * 修改配置
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void alterConfig() throws ExecutionException, InterruptedException {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, "test3");
        ConfigEntry entry1 = new ConfigEntry("cleanup.policy", "compact");
        ConfigEntry entry2 = new ConfigEntry("max.message.bytes", "1024000");

        Config config = new Config(Arrays.asList(entry1, entry2));
        Map<ConfigResource, Config> configs = new HashMap<>();
        configs.put(resource, config);
        AlterConfigsResult alterConfigsResult = client.alterConfigs(configs);

        //kafka服务器版本为1.1.0，以下方法从2.3.0开始支持
        /*AlterConfigOp alterConfigOp1 = new AlterConfigOp(entry1, AlterConfigOp.OpType.SUBTRACT);
        AlterConfigOp alterConfigOp2 = new AlterConfigOp(entry2, AlterConfigOp.OpType.SUBTRACT);
        Map<ConfigResource, Collection<AlterConfigOp>> configs = new HashMap<>();
        configs.put(resource, Arrays.asList(alterConfigOp1, alterConfigOp2));
        AlterConfigsResult alterConfigsResult = client.incrementalAlterConfigs(configs, new AlterConfigsOptions().timeoutMs(10000));//10秒超时*/
        alterConfigsResult.all().get();
    }

    /**
     * 增加分区到5个
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void addPartition() throws ExecutionException, InterruptedException {
        NewPartitions newPartitions = NewPartitions.increaseTo(5);
        Map<String, NewPartitions> newPartitionsMap = new HashMap<>();
        newPartitionsMap.put("test3", newPartitions);
        CreatePartitionsResult partitions = client.createPartitions(newPartitionsMap, new CreatePartitionsOptions().timeoutMs(10000));
        partitions.all().get();
    }

    /**
     * 计算副本分配方式
     */
    @Test
    public void computeReplicaDistribution() {
        int partitions = 3;
        int replicationFactor = 2;
        List<BrokerMetadata> brokerMetadata = new ArrayList<>();
        BrokerMetadata brokerMetadata1 = new BrokerMetadata(1, Option.apply("rack1"));
        BrokerMetadata brokerMetadata2 = new BrokerMetadata(2, Option.apply("rack1"));
        BrokerMetadata brokerMetadata3 = new BrokerMetadata(3, Option.apply("rack1"));
        brokerMetadata.add(brokerMetadata1);
        brokerMetadata.add(brokerMetadata2);
        brokerMetadata.add(brokerMetadata3);
        Decorators.AsScala<Buffer<BrokerMetadata>> bufferAsScala = JavaConverters.asScalaBufferConverter(brokerMetadata);
        scala.collection.Map<Object, Seq<Object>> partitionToBroker = AdminUtils.assignReplicasToBrokers(bufferAsScala.asScala(), partitions, replicationFactor, -1, -1);
        logger.info(partitionToBroker);//Map(2 -> ArrayBuffer(3, 1), 1 -> ArrayBuffer(2, 3), 0 -> ArrayBuffer(1, 2))
    }
}
