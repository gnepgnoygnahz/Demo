package dealData.process;

import dealData.bean.PageViewsBean;
import dealData.bean.VisitBean;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * drop table if exist ods_click_visit;
 * create table ods_click_visit(
 * session     string,
 * remote_addr string,
 * inTime      string,
 * outTime     string,
 * inPage      string,
 * outPage     string,
 * http_referer string,
 * pageVisits  int)
 * partitioned by (dateStr string)
 * row format delimited
 * fields terminated by ',';
 */
public class VisitProcess {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(VisitProcess.class);

        job.setMapperClass(VisitProcessMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(PageViewsBean.class);

        job.setReducerClass(VisitProcessReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(VisitBean.class);

        FileInputFormat.setInputPaths(job, new Path("D:\\Money\\data\\pageViews"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\Money\\data\\visit"));

        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }

    static class VisitProcessMapper extends Mapper<LongWritable, Text, Text, PageViewsBean> {

        PageViewsBean pvBean;
        Text k = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] fields = line.split(",");
            if (fields.length < 10) return;
            pvBean = new PageViewsBean(fields[0], fields[1], fields[2], fields[3], Integer.parseInt(fields[4]), fields[5], fields[6], fields[7], fields[8], fields[9]);
            k.set(pvBean.getSession());
            context.write(k, pvBean);
        }
    }

    static class VisitProcessReducer extends Reducer<Text, PageViewsBean, NullWritable, VisitBean> {

        @Override
        protected void reduce(Text key, Iterable<PageViewsBean> values, Context context) throws IOException, InterruptedException {
            try {
                // 将pvBeans按照step排序
                ArrayList<PageViewsBean> pvBeansList = new ArrayList<PageViewsBean>();
                for (PageViewsBean pvBean : values) {
                    PageViewsBean bean = new PageViewsBean();
                    BeanUtils.copyProperties(bean, pvBean);
                    pvBeansList.add(bean);
                }

                Collections.sort(pvBeansList, new Comparator<PageViewsBean>() {
                    @Override
                    public int compare(PageViewsBean o1, PageViewsBean o2) {
                        return o1.getVisit_step() > o2.getVisit_step() ? 1 : -1;
                    }
                });

                // 取这次visit的首尾pageview记录，将数据放入VisitBean中
                VisitBean visitBean = new VisitBean();
                // 取visit的首记录
                visitBean.setInPage(pvBeansList.get(0).getRequest());
                visitBean.setInTime(pvBeansList.get(0).getTime_local());
                // 取visit的尾记录
                visitBean.setOutPage(pvBeansList.get(pvBeansList.size() - 1).getRequest());
                visitBean.setOutTime(pvBeansList.get(pvBeansList.size() - 1).getTime_local());
                // visit访问的页面数
                visitBean.setPageVisits(pvBeansList.size());
                // 来访者的ip
                visitBean.setRemote_addr(pvBeansList.get(0).getRemote_addr());
                // 本次visit的referal
                visitBean.setHttp_referer(pvBeansList.get(0).getHttp_referer());
                visitBean.setSession(key.toString());

                context.write(NullWritable.get(), visitBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
