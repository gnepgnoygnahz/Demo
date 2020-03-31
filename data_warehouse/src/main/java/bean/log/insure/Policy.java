package bean.log.insure;

import lombok.Data;

/**
 * @ClassName Tou
 * @Description TODO 保单信息
 * @Author zhangyp
 * @Date 2020/3/7 22:55
 * @Version 1.0
 */
@Data
public class Policy {
    /**
     * 团单号
     */
    private String grpContNo;
    /**
     * 个单号
     */
    private String contNo;
    /**
     * 总单投保单号码
     */
    private String proposalContNo;
    /**
     * 投保单号
     */
    private String prtNo;
    /**
     * 保单类型：1个单，2团单
     */
    private String contType;
    /**
     * 是否为家庭单：0家庭单，1普通团单
     */
    private String familyType;
    /**
     * 管理机构
     */
    private String manageCom;
    /**
     * 代理人组别
     */
    private String agentGroup;
    /**
     * 代理机构
     */
    private String agentCom;
    /**
     * 代理人编码
     */
    private String agentCode;
    /**
     * 代理人姓名
     */
    private String agentName;
    /**
     * 保障计划编码
     */
    private String contPlanCode;
    /**
     * 保障计划名称
     */
    private String contPlanName;
    /**
     * 保单申请日期
     */
    private String polApplyDate;
    /**
     * 保单生效日
     */
    private String contValiDate;
    /**
     * 保单失效日
     */
    private String contEndDate;
    /**
     * 份数
     */
    private String copys;
    /**
     * 总保费
     */
    private String totalPrem;
    /**
     * 总保额
     */
    private String totalAmnt;
    /**
     * 缴费方式：0趸缴，3季缴，6半年缴，12年缴
     */
    private String payIntv;
    /**
     * 缴费年期
     */
    private String payYears;
    /**
     * 保单性质：0纸质保单，1电子保单
     */
    private String disputedFlag;
    /**
     * 特别约定
     */
    private String remark;
    /**
     * 缴费方式：1现金，3转账支票，4银行转账，5内部转账，
     * 6pos机，11银行汇款，12其他银行代收代付，13赠送保费，
     * 17银行缴款单，18网销的“第三方”收付费，19网银支付，
     * 20微信支付，22广州银联-批量，23广州银联-实时
     */
    private String payMethod;
    /**
     * 银行帐户名
     */
    private String accName;
    /**
     * 银行编码
     */
    private String accBankCode;
    /**
     * 银行帐号
     */
    private String bankAccNo;
    /**
     * 银行名称
     */
    private String bankAgentName;
    /**
     * 银行代理编码
     */
    private String bankAgentNo;
    /**
     * 保单核保结论：1谢绝承保，2延期承保，3条件承保，4变更承保，5自核未通过，
     * 6待上级审核，7问题件，8延期承保，9正常承保，a撤销申请，b保险计划变更，
     * c税优待验证，E免责承保，z核保订正
     */
    private String policyUWResult;
}
