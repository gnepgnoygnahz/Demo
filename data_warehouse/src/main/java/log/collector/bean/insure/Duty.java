package log.collector.bean.insure;

import lombok.Data;

/**
 * @ClassName Duty
 * @Description TODO 责任信息
 * @Author zhangyp
 * @Date 2020/3/7 23:00
 * @Version 1.0
 */
@Data
public class Duty {
    /**
     * 责任编码
     */
    private String dutyCode;
    /**
     * 责任保额
     */
    private String dutyAmnt;
    /**
     * 责任保费
     */
    private String dutyPrem;

}
