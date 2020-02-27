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
public class VisitBean implements Writable {

    /**
     * session标识，相同表示是一次visit
     */
    private String session;

    /**
     * 记录客户端的ip地址
     */
    private String remote_addr;

    /**
     * 本次visit的开始时间
     */
    private String inTime;

    /**
     * 本次visit的结束时间
     */
    private String outTime;

    /**
     * 本次visit的开始页面
     */
    private String inPage;

    /**
     * 本次visit的结束页面
     */
    private String outPage;

    /**
     * 用来记录从那个页面链接访问过来的，即上一个页面
     */
    private String http_referer;

    /**
     * 本次visit共经历过几个页面
     */
    private int pageVisits;



    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(session);
        out.writeUTF(remote_addr);
        out.writeUTF(inTime);
        out.writeUTF(outTime);
        out.writeUTF(inPage);
        out.writeUTF(outPage);
        out.writeUTF(http_referer);
        out.writeInt(pageVisits);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.session = in.readUTF();
        this.remote_addr = in.readUTF();
        this.inTime = in.readUTF();
        this.outTime = in.readUTF();
        this.inPage = in.readUTF();
        this.outPage = in.readUTF();
        this.http_referer = in.readUTF();
        this.pageVisits = in.readInt();
    }

    @Override
    public String toString() {
        return session + "," + remote_addr + "," + inTime + "," + outTime + ","
                + inPage + "," + outPage + "," + http_referer + "," + pageVisits
                ;
    }
}
