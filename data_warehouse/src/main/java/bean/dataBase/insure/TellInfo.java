package bean.dataBase.insure;

import lombok.*;

/**
 * @ClassName TellInfo
 * @Description TODO 告知信息
 * @Author zhangyp
 * @Date 2020/3/7 23:02
 * @Version 1.0
 */
@Data
public class TellInfo {
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
     * 告知版本编码
     */
    private String tellInfoVersion;
    /**
     * 告知问题编码
     */
    private String tellInfoCode;
    /**
     * 告知问题答案
     */
    private String tellInfoAnswer;
}
