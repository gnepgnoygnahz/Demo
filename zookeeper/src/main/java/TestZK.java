package zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * zk工作流程
 * ----------------
 * zk集群启动后，client连接到其中的一个节点，这个节点可以leader，也可以follower。
 * 连通后，node分配一个id给client，发送ack信息给client。
 * 如果客户端没有收到ack，连接到另一个节点。
 * client周期性发送心跳信息给节点保证连接不会丢失。
 * <p>
 * 如果client读取数据，发送请求给node，node读取自己数据库，返回节点数据给client.
 * <p>
 * 如果client存储数据，将路径和数据发送给server，server转发给leader。
 * leader再补发请求给所有follower。只有大多数(超过半数)节点成功响应，则
 * 写操作成功。
 */
public class TestZK {

    private ZooKeeper zk;

    @Before
    public void init() throws IOException {
        zk = new ZooKeeper("zyp-1:2181,zyp-2:2181,zyp-3:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("========连接zookeeper成功========");
            }
        });
    }

    @After
    public void close() throws InterruptedException {
        zk.close();
        System.out.println("========关闭zookeeper成功========");
    }

    @Test
    public void ls() throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren("/", null);
        for (String child : children) {
            System.out.println("========" + child);
        }
    }

    @Test
    public void lsAll() throws KeeperException, InterruptedException {
        ls("/");
    }

    public void ls(String path) throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren(path, null);
        for (String child : children) {
            child = (path + "/" + child).replace("//", "/");
            System.out.println("========" + child);
            ls(child);
        }
    }

    /**
     * 创建节点只能一级一级的创建，不能多级一起创建，且每一级都得带数据,ACL是权限控制
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void createEmphoral() throws KeeperException, InterruptedException {
        //临时节点，当前会话关闭后会将节点删除
        zk.create("/a", "kobe".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        //临时序列
        zk.create("/a1", "kobe".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        //持久性节点，当前会话关闭后会将节不会被删除
        zk.create("/b", "kobe".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //持久序列 节点名字为get /b10000000005 即原有名字+10个数字，再次添加一个相同名字的节点，不会被覆盖，而是又创建一个/b10000000006,即序列递增
        zk.create("/b1", "kobe".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

    }

    /**
     * 修改数据，只能修改zk上当前的版本号
     * 如下面，zk上/b版本为0，修改0可以成功，但是指定的修改版本为1时就会失败
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void setData() throws KeeperException, InterruptedException {
        zk.setData("/b", "bryant".getBytes(), 0);
    }

    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = zk.getData("/b", null, null);
        System.out.println(new String(data));
    }

    /**
     * 观察
     * client能够通过watch机制在数据发生变化时收到通知。
     * client可以在read 节点时设置观察者。watch机制会发送通知给注册的客户端。
     * 观察模式只触发一次。
     * session过期，watch机制删除了。
     * 因此要持续观察，就必须不断注册观察者
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void watch() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    byte[] data = zk.getData("/b", this, stat);
                    System.out.println(new String(data) + "========" + stat.getVersion());
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        byte[] data = zk.getData("/b", watcher, stat);
        System.out.println(new String(data) + "========" + stat.getVersion());
        while (true) {
            Thread.sleep(1000);
        }
    }
}
