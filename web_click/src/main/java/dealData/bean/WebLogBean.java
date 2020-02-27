package dealData.bean;

import lombok.*;
import org.apache.hadoop.io.Writable;
import utils.DateUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class WebLogBean implements Writable {

    /**
     * 判断数据是否合法
     */
    private boolean valid = true;

    /**
     * 记录客户端的ip地址
     */
    private String remote_addr;

    /**
     * 记录客户端用户名称,忽略属性"-"
     */
    private String remote_user;

    /**
     * 记录访问时间与时区
     */
    private String time_local;

    /**
     * 记录请求的url与http协议，即当前页面
     */
    private String request;

    /**
     * 记录请求状态；成功是200
     */
    private String status;

    /**
     * 记录发送给客户端文件主体内容大小
     */
    private String body_bytes_sent;

    /**
     * 用来记录从那个页面链接访问过来的，即上一个页面
     */
    private String http_referer;

    /**
     * 记录客户浏览器的相关信息
     */
    private String http_user_agent;

    /**
     * 将数据转换为WebLogBean对象
     * 1              2 3  4                    5       6    7                                           8          9  10 11  12
     * 194.237.142.21 - - [18/Sep/2013:06:49:18 +0000] "GET /wp-content/uploads/2013/07/rstudio-git3.png HTTP/1.1" 304 0 "-" "Mozilla/4.0 (compatible;)"
     *
     * @return WebLogBean
     */
    public static WebLogBean parser(String line) {
        String[] data = line.split(" ");
        if (data.length > 11) {
            WebLogBean webLogBean = new WebLogBean();
            webLogBean.setRemote_addr(data[0]);
            webLogBean.setRemote_user(data[1]);
            String time_local = DateUtil.formatDate(data[3].substring(1));
            if (null == time_local) {
                time_local = "-invalid_time-";
                webLogBean.setValid(false);
            }
            webLogBean.setTime_local(time_local);
            webLogBean.setRequest(data[6]);
            webLogBean.setStatus(data[8]);
            // 大于400，HTTP错误
            if (Integer.parseInt(webLogBean.getStatus()) >= 400) {
                webLogBean.setValid(false);
            }
            webLogBean.setBody_bytes_sent(data[9]);
            webLogBean.setHttp_referer(data[10]);

            //如果useragent元素较多，拼接useragent
            if (data.length > 12) {
                StringBuilder sb = new StringBuilder();
                for (int i = 11; i < data.length; i++) {
                    sb.append(data[i]);
                }
                webLogBean.setHttp_user_agent(sb.toString());
            } else {
                webLogBean.setHttp_user_agent(data[11]);
            }
            return webLogBean;
        }
        return null;
    }

    /**
     * 过滤js/图片/css等静态资源
     *
     * @param bean  WebLogBean
     * @param pages 过滤规则
     */
    public static void filterStaticResource(WebLogBean bean, Set<String> pages) {
        for (String page : pages) {
            if (bean.getRequest().startsWith(page)) {
                bean.setValid(false);
            }
        }
        /*if (pages.contains(bean.getRequest())) {
            bean.setValid(false);
        }*/
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(this.valid);
        out.writeUTF(null == remote_addr ? "" : remote_addr);
        out.writeUTF(null == remote_user ? "" : remote_user);
        out.writeUTF(null == time_local ? "" : time_local);
        out.writeUTF(null == request ? "" : request);
        out.writeUTF(null == status ? "" : status);
        out.writeUTF(null == body_bytes_sent ? "" : body_bytes_sent);
        out.writeUTF(null == http_referer ? "" : http_referer);
        out.writeUTF(null == http_user_agent ? "" : http_user_agent);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.valid = in.readBoolean();
        this.remote_addr = in.readUTF();
        this.remote_user = in.readUTF();
        this.time_local = in.readUTF();
        this.request = in.readUTF();
        this.status = in.readUTF();
        this.body_bytes_sent = in.readUTF();
        this.http_referer = in.readUTF();
        this.http_user_agent = in.readUTF();
    }

    @Override
    public String toString() {
        return valid + "," + remote_addr + "," + remote_user + "," + time_local + ","
                + request + "," + status + "," + body_bytes_sent + "," + http_referer + "," + http_user_agent
                ;
    }
}
