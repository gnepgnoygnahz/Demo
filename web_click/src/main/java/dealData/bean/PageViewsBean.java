package dealData.bean;

import lombok.*;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PageViewsBean implements Writable {

    /**
     * session标识，相同表示是一次visit
     */
    private String session;

    /**
     * 记录客户端的ip地址
     */
    private String remote_addr;

    /**
     * 记录访问时间与时区
     */
    private String time_local;

    /**
     * 记录请求的url与http协议，即当前页面
     */
    private String request;

    /**
     * 同一个visit中的第几步
     */
    private int visit_step;

    /**
     * 在这个页面停留多长时间
     */
    private String page_stayLong;

    /**
     * 用来记录从那个页面链接访问过来的，即上一个页面
     */
    private String http_referer;

    /**
     * 记录客户浏览器的相关信息
     */
    private String http_user_agent;

    /**
     * 记录发送给客户端文件主体内容大小
     */
    private String body_bytes_sent;

    /**
     * 记录请求状态；成功是200
     */
    private String status;

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(session);
        out.writeUTF(remote_addr);
        out.writeUTF(time_local);
        out.writeUTF(request);
        out.writeInt(visit_step);
        out.writeUTF(page_stayLong);
        out.writeUTF(http_referer);
        out.writeUTF(http_user_agent);
        out.writeUTF(body_bytes_sent);
        out.writeUTF(status);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.session = in.readUTF();
        this.remote_addr = in.readUTF();
        this.time_local = in.readUTF();
        this.request = in.readUTF();
        this.visit_step = in.readInt();
        this.page_stayLong = in.readUTF();
        this.http_referer = in.readUTF();
        this.http_user_agent = in.readUTF();
        this.body_bytes_sent = in.readUTF();
        this.status = in.readUTF();
    }

    @Override
    public String toString() {
        return session + "," + remote_addr + "," + time_local + "," + request + "," + visit_step + "," + page_stayLong + ","
                + http_referer + "," + http_user_agent + "," + body_bytes_sent + "," + status
                ;
    }
}
