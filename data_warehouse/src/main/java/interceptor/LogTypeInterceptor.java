package interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import utils.LogUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LogTypeInterceptor
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/21 17:00
 * @Version 1.0
 */
public class LogTypeInterceptor implements Interceptor {
    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {

        // 区分日志类型：body  header
        // 1 获取body数据
        byte[] body = event.getBody();
        String data = new String(body, Charset.forName("UTF-8"));
        String topic = LogUtil.getTopic(data);
        // 2 获取header
        Map<String, String> headers = event.getHeaders();
        // 3 向Header中赋值
        System.out.println("========" + topic + "========" + data);
        headers.put("topic", topic);
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {

        ArrayList<Event> interceptors = new ArrayList<>();
        for (Event event : events) {
            Event intercept1 = intercept(event);
            interceptors.add(intercept1);
        }

        return interceptors;
    }


    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        @Override
        public Interceptor build() {
            return new LogTypeInterceptor();
        }

        @Override
        public void configure(Context context) {

        }

    }
}
