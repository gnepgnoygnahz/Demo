package hadoop.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

public class TestMapFile {

    private FileSystem fs = null;
    private Configuration conf = null;
    private final static Logger logger = LogManager.getLogger(TestMapFile.class);

    @Before
    public void init() throws IOException {
        conf = new Configuration();
        conf.set("fs:defaultFS", "file：///");
        fs = FileSystem.get(conf);
    }

    @After
    public void close() throws IOException {
        fs.close();
    }

    @Test
    public void save() throws IOException {
        //Path p = new Path("D:\\Money\\data\\TemperatureMap.seq");
        MapFile.Writer writer = new MapFile.Writer(conf, fs, "D:\\Money\\data\\TemperatureMap", IntWritable.class, Text.class);
        for (int i = 1; i <= 10000; i++) {
            //map输出key只能越来越大
            writer.append(new IntWritable(i), new Text(new Random().nextInt(100) - 30 + ""));
        }
        writer.close();

        /*Configuration conf = new Configuration();
        URI uri = URI.create("file:///Money/data//TemperatureMap");
        FileSystem fs = FileSystem.get(uri, conf);
        MapFile.Writer writer = null;
        writer = new MapFile.Writer(conf, fs, uri.getPath(), IntWritable.class, Text.class);

        //通过writer向文档中写入记录
        for (int i = 1; i <= 10000; i++) {
            //map输出key只能越来越大
            writer.append(new IntWritable(i), new Text(new Random().nextInt(100) - 30+""));
        }
        //writer.append(new Text("key"), new Text("value"));
        IOUtils.closeStream(writer);*/
    }

    @Test
    public void read() throws IOException {
        MapFile.Reader reader = new MapFile.Reader(fs, "D:\\Money\\data\\TemperatureMap\\", conf);
        IntWritable key = new IntWritable();
        Text value = new Text();
        while (reader.next(key, value)) {
            logger.info(key.get() + ":" + value.toString());
        }
        reader.close();

        /*Configuration conf = new Configuration();
        URI uri = URI.create("file:///Money/data//TemperatureMap");
        FileSystem fs = FileSystem.get(uri, conf);
        MapFile.Reader reader = null;
        reader = new MapFile.Reader(fs, uri.getPath(), conf);

        //通过writer向文档中写入记录
        IntWritable key = new IntWritable();
        Text value = new Text();
        while (reader.next(key, value)) {
            logger.info(key.get() + ":" + value.toString());
        }
        IOUtils.closeStream(reader);*///关闭write流

    }
}
