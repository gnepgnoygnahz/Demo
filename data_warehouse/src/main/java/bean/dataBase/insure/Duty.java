package bean.dataBase.insure;

import lombok.*;

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
     * 团单号
     */
    private String grpContNo;
    /**
     * 个单号
     */
    private String contNo;
    /**
     * 投保单号
     */
    private String prtNo;
    /**
     * 险种号
     */
    private String polNo;
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
