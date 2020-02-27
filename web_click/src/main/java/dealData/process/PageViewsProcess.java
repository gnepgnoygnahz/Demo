package dealData.process;

import dealData.bean.PageViewsBean;
import dealData.bean.WebLogBean;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import utils.DateUtil;

import java.io.IOException;
import java.util.*;

/**
 * drop table if exists ods_click_page_views;
 * create table ods_click_page_views(
 * Session string,
 * remote_addr string,
 * time_local string,
 * request string,
 * visit_step string,
 * page_stayLong string,
 * http_referer string,
 * http_user_agent string,
 * body_bytes_sent string,
 * status string)
 * partitioned by (dateStr string)
 * row format delimited
 * fields terminated by ',';
 */
public class PageViewsProcess {

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(PageViewsProcess.class);

        job.setMapperClass(PageViewsProcessMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(WebLogBean.class);

        job.setReducerClass(PageViewsProcessReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path("D:\\Money\\data\\pre"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\Money\\data\\pageViews"));

        job.waitForCompletion(true);

    }

    static class PageViewsProcessMapper extends Mapper<LongWritable, Text, Text, WebLogBean> {
        Text k = new Text();
        WebLogBean v;

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] fields = line.split(",");
            if (fields.length < 9) return;
            // true,
            // 194.237.142.21,
            // -,
            // 2013-09-18 06:49:18,
            // /wp-content/uploads/2013/07/rstudio-git3.png,
            // 304,
            // 0,
            // "-",
            // "Mozilla/4.0(compatible;)"
            v = new WebLogBean(true, fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8]);
            k.set(v.getRemote_addr());
            context.write(k, v);
        }
    }

    static class PageViewsProcessReducer extends Reducer<Text, WebLogBean, NullWritable, PageViewsBean> {

        PageViewsBean v;

        @Override
        protected void reduce(Text key, Iterable<WebLogBean> values, Context context) {
            ArrayList<WebLogBean> beans = new ArrayList<WebLogBean>();
            try {
                for (WebLogBean bean : values) {
                    WebLogBean webLogBean = new WebLogBean();

                    BeanUtils.copyProperties(webLogBean, bean);

                    beans.add(webLogBean);
                }
                Collections.sort(beans, new Comparator<WebLogBean>() {
                    @Override
                    public int compare(WebLogBean o1, WebLogBean o2) {
                        try {
                            Date d1 = DateUtil.toDate(o1.getTime_local());
                            Date d2 = DateUtil.toDate(o2.getTime_local());
                            if (d1 == null || d2 == null)
                                return 0;
                            return d1.compareTo(d2);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });

                int step = 1;
                String session = UUID.randomUUID().toString();
                for (int i = 1; i < beans.size(); i++) {
                    WebLogBean bean = beans.get(i);

                    // 9f316c1c-dbbf-4d76-928a-4c371ccb26a8,
                    // 1.202.186.37,
                    // -,
                    // 2013-09-18 15:39:11,
                    // /wp-content/uploads/2013/08/windjs.png,
                    // 1,
                    // 7,
                    // "http://cnodejs.org/topic/521a30d4bee8d3cb1272ac0f",
                    // "Mozilla/5.0(Macintosh;IntelMacOSX10_8_4)AppleWebKit/537.36(KHTML,
                    // 34613,
                    // 200
                    // 如果仅有1条数据，则直接输出
                    if (1 == beans.size()) {
                        // 设置默认停留市场为60s
                        v = new PageViewsBean(session, key.toString(), bean.getTime_local(), bean.getRequest(), step, "60",
                                bean.getHttp_referer(), bean.getHttp_user_agent(), bean.getBody_bytes_sent(), bean.getStatus());
                        context.write(NullWritable.get(), v);
                        break;
                    }

                    // 如果不止1条数据，则将第一条跳过不输出，遍历第二条时再输出
                    if (i == 0) {
                        continue;
                    }

                    // 求近两次时间差
                    long timeDiff = DateUtil.timeDiff(DateUtil.toDate(bean.getTime_local()), DateUtil.toDate(beans.get(i - 1).getTime_local()));
                    // 如果本次-上次时间差<30分钟，则输出前一次的页面访问信息
                    if (timeDiff < 30 * 60 * 1000) {
                        v = new PageViewsBean(session, key.toString(), beans.get(i - 1).getTime_local(),
                                beans.get(i - 1).getRequest(), step, String.valueOf(timeDiff / 1000),
                                beans.get(i - 1).getHttp_referer(), beans.get(i - 1).getHttp_user_agent(),
                                beans.get(i - 1).getBody_bytes_sent(), beans.get(i - 1).getStatus());
                        context.write(NullWritable.get(), v);
                        step++;
                    } else {
                        // 如果本次-上次时间差>30分钟，则输出前一次的页面访问信息且将step重置，以分隔为新的visit
                        v = new PageViewsBean(session, key.toString(), beans.get(i - 1).getTime_local(),
                                beans.get(i - 1).getRequest(), step, "60",
                                beans.get(i - 1).getHttp_referer(), beans.get(i - 1).getHttp_user_agent(),
                                beans.get(i - 1).getBody_bytes_sent(), beans.get(i - 1).getStatus());
                        context.write(NullWritable.get(), v);
                        // 输出完上一条之后，重置step编号与session
                        step = 1;
                        session = UUID.randomUUID().toString();
                    }

                    // 如果此次遍历的是最后一条，则将本条直接输出
                    if (i == beans.size() - 1) {
                        // 设置默认停留市场为60s
                        v = new PageViewsBean(session, key.toString(), bean.getTime_local(),
                                bean.getRequest(), step, "60", bean.getHttp_referer(),
                                bean.getHttp_user_agent(), bean.getBody_bytes_sent(), bean.getStatus());
                        context.write(NullWritable.get(), v);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
