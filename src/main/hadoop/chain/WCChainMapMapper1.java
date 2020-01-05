package hadoop.chain;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WCChainMapMapper1 extends Mapper<LongWritable, Text, Text, Text> {
    private static final Logger logger = LogManager.getLogger(WCChainMapMapper1.class);
    List<String> list = new ArrayList<>();

    /**
     * 通过阅读父类Mapper的源码，发现 setup方法是在maptask处理数据之前调用一次 可以用来做一些初始化工作
     */
    @Override
    protected void setup(Context context) throws IOException {
        BufferedReader br = null;
        URI[] cacheFiles = context.getCacheFiles();
        String path;
        for (URI cacheFile : cacheFiles) {
            path = cacheFile.getPath();
            logger.info(path);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line;
            while (StringUtils.isNotEmpty(line = br.readLine())) {
                list.add(line);
            }
        }
        br.close();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] data = line.split(" ");
        if (list.contains(data[1])) {
            context.write(new Text(data[0]), new Text(data[1]));
        }
    }
}
