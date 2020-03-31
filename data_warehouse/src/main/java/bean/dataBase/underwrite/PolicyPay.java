package bean.dataBase.underwrite;

import lombok.Data;

/**
 * @ClassName ConfirmInfo
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/24 14:52
 * @Version 1.0
 */
@Data
public class PolicyPay {

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
     * 保单性质：0纸质保单，1电子保单
     */
    private String disputedFlag;
    /**
     * 银行帐户名
     */
    private String accName;
    /**
     * 银行帐号
     */
    private String bankAccNo;
    /**
     * 银行编码
     */
    private String accBankCode;
    /**
     * 银行名称
     */
    private String bankAgentName;
    /**
     * 总保费
     */
    private String totalPrem;
    /**
     * 总保额
     */
    private String totalAmnt;
}
