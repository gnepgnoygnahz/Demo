package hadoop.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Create By Zhangyp
 * Date:  2019/11/5
 * Desc:
 */
public class TestHDFS {

    private FileSystem fs = null;
    private Configuration conf = null;
    private final static Logger logger = LogManager.getLogger(TestHDFS.class);

    @Before
    public void init() throws URISyntaxException, IOException, InterruptedException {
        conf = new Configuration();
        //conf.set("fs.defaultFS","hdfs://zyp-1:9000");
        conf.set("dfs.replication", "3");
        //hdfs目录用户是zyp，跑程序电脑用户是zhang，对hdfs文件操作时会报错权限不足，需要指定用户。或者启动程序时配置参数：-DHADOOP_USER_NAME=zyp
        fs = FileSystem.get(new URI("hdfs://zyp-1:9000"), conf, "zyp");
    }

    @After
    public void close() throws IOException {
        fs.close();
    }

    @Test
    public void readFile() throws IOException {
        //URL不支持hdfs协议，需要注册到URL上面
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        URL url = new URL("hdfs://zyp-1:9000/start-all.sh");
        URLConnection conn = url.openConnection();
        InputStream inputStream = conn.getInputStream();
        byte[] buf = new byte[inputStream.available()];
        inputStream.read(buf);
        inputStream.close();
        String str = new String(buf);
        logger.info(str);
    }

    @Test
    public void readFileByAPI() throws IOException {
        Path path = new Path("/start-all.sh");
        FSDataInputStream fis = fs.open(path);
        ByteArrayOutputStream bp = new ByteArrayOutputStream();
        IOUtils.copyBytes(fis, bp, 1024);
        logger.info(new String(bp.toByteArray()));
    }

    @Test
    public void createFile() throws IOException {
        //目录不存在会自动创建
        //FSDataOutputStream out = fs.create(new Path("/user/a.txt"));
        //没有写路径会放到用户家目录下，hadoop家目录在/user/zyp下
        FSDataOutputStream out = fs.create(new Path("a.txt"));
        out.write("Hello,Hadoop!".getBytes());
        out.close();
    }

    @Test
    public void copyFile() throws IOException {
        fs.copyFromLocalFile(new Path("D:/a.txt"), new Path("/jaychou/a.txt"));
    }

    @Test
    public void removeFile() throws IOException {
        //直接写delete("/zyp")(此函数不推荐使用)等价于delete("/zyp",true)，第二个参数代表递归
        fs.delete(new Path("/zyp"), true);
    }

    @Test
    public void downLoadFile() throws IOException {
        fs.copyToLocalFile(new Path("/start-all.sh"), new Path("D:/start-all.sh"));
    }

    @Test
    public void mkdir() throws IOException {
        fs.mkdirs(new Path("/jaychou"));
    }

    @Test
    public void ls() throws IOException {
        //只能展示  /  目录下文件或文件夹，不能递归展示所有的
        FileStatus[] listStatus = fs.listStatus(new Path("/"));
        for (FileStatus file :
                listStatus) {
            logger.info((file.isFile() ? "file" : "directory") + "=========" + file.getPath().getName());
        }
    }

    @Test
    public void lsAll() throws IOException {
        //显示所有文件信息,不能展示目录
        RemoteIterator<LocatedFileStatus> listFile = fs.listFiles(new Path("/"), true);
        while (listFile.hasNext()) {
            LocatedFileStatus fileStatus = listFile.next();
            logger.info("---------------start-------------");
            logger.info("blocksize: " + fileStatus.getBlockSize());
            logger.info("owner: " + fileStatus.getOwner());
            logger.info("Replication: " + fileStatus.getReplication());
            logger.info("Permission: " + fileStatus.getPermission());
            logger.info("Path: " + fileStatus.getPath());
            logger.info("Name: " + fileStatus.getPath().getName());
            logger.info("------------------");
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            for (BlockLocation block :
                    blockLocations) {
                logger.info("块起始偏移量: " + block.getOffset());
                logger.info("块长度:" + block.getLength());
                //块所在的datanode节点
                String[] datanodes = block.getHosts();
                for (String dn : datanodes) {
                    logger.info("datanode:" + dn);
                }
            }
            logger.info("----------------end--------------");
        }
    }

    @Test
    public void testConf() {
        //打印参数
        Iterator<Map.Entry<String, String>> it = conf.iterator();

        /*while(it.hasNext()){
            Map.Entry<String, String> ent = it.next();
           logger.info(ent.getKey() + " : " + ent.getValue());
        }*/

        for (; it.hasNext(); ) {
            Map.Entry<String, String> confEntry = it.next();
            logger.info(confEntry.getKey() + " : " + confEntry.getValue());
        }

    }


}
