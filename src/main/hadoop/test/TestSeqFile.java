package hadoop.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

/**
 * 数据如下，每5条一个同步点，序列文件有头部大致如下
 * SEQ org.apache.hadoop.io.IntWritable org.apache.hadoop.io.IntWritable *org.apache.hadoop.io.compress.DefaultCodec
 * 因此第一条数据是从159开始
 * <p>
 * 159 : 2019 : 1
 * 183 : 2020 : -29
 * 207 : 2034 : -21
 * 231 : 2041 : 48
 * 255 : 2008 : 9
 * <p>
 * 299 : 2019 : -14
 * 323 : 2041 : 2
 * 347 : 2011 : 8
 * 371 : 2069 : 28
 * 395 : 2060 : 27
 * <p>
 * 439 : 2002 : 11
 * 463 : 2057 : 67
 * 487 : 1974 : -19
 * 511 : 1994 : -3
 * 535 : 1976 : -3
 * <p>
 * 579 : 2053 : 59
 * 603 : 2018 : 62
 * 627 : 2028 : 8
 * 651 : 2049 : -11
 * 675 : 2023 : 68
 * <p>
 * 719 : 2041 : -7
 * 743 : 2024 : 64
 * 767 : 2050 : 29
 * 791 : 2060 : 17
 * 815 : 1973 : -1
 * <p>
 * 859 : 2027 : 9
 * 883 : 1977 : 67
 * 907 : 1975 : 45
 * 931 : 2013 : 38
 * 955 : 2010 : 57
 */
public class TestSeqFile {

    private FileSystem fs = null;
    private Configuration conf = null;
    private final static Logger logger = LogManager.getLogger(TestSeqFile.class);

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
        Path p = new Path("D:\\Money\\data\\Temperature.seq");
        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, p, IntWritable.class, IntWritable.class);
        for (int i = 1; i <= 30; i++) {
            writer.append(new IntWritable(new Random().nextInt(100) + 1970), new IntWritable(new Random().nextInt(100) - 30));
            if (i % 5 == 0) {
                writer.sync();
            }
        }
        writer.close();
    }

    @Test
    public void saveWithGzip() throws IOException {
        Path p = new Path("D:\\Money\\data\\TemperatureGzip.seq");
        //SequenceFile得有本地资源库才能压缩
        //java.lang.IllegalArgumentException: SequenceFile doesn't work with GzipCodec without native-hadoop code!
        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, p, IntWritable.class, IntWritable.class, SequenceFile.CompressionType.BLOCK, new GzipCodec());
        for (int i = 1; i <= 30; i++) {
            writer.append(new IntWritable(new Random().nextInt(100) + 1970), new IntWritable(new Random().nextInt(100) - 30));
            //每5条设置一个同步点（是5条记录，不是5字节）,同步点在数据之前占20字节
            if (i % 5 == 0) {
                writer.sync();
            }
        }
        writer.close();
    }

    @Test
    public void read() throws IOException {
        Path p = new Path("D:\\Money\\data\\Temperature.seq");
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, p, conf);
        IntWritable key = new IntWritable();
        IntWritable value = new IntWritable();
        /*while (reader.next(key, value))
            System.out.println(key.get() + " : " + value.get());*/
        //定位到321字节，但是因为这条是数据是从323处开始的，会报错
        //reader.seek(321);

        //sync会定位到下一个同步点的位置，调取next后会取到同步点后的第一条信息即439处
        reader.sync(321);
        while (reader.next(key)) {
            reader.getCurrentValue(value);
            logger.info(reader.getPosition() + " : " + key.get() + " : " + value.get());
        }
        reader.close();
    }

}
