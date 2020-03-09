package log.collector.bean.insure;

import lombok.Data;

import java.util.List;

/**
 * @ClassName Tou
 * @Description TODO 险种信息
 * @Author zhangyp
 * @Date 2020/3/7 22:55
 * @Version 1.0
 */
@Data
public class Risk {
    /**
     * 险种名称
     */
    private String riskName;
    /**
     * 险种编码
     */
    private String riskCode;
    /**
     * 主险编码
     */
    private String mainRiskCode;
    /**
     * 险种保额
     */
    private String amnt;
    /**
     * 险种保费
     */
    private String prem;
    /**
     * 险种份数
     */
    private String copys;
    /**
     * 缴费方式：0趸缴，3季缴，6半年缴，12年缴
     */
    private String payIntv;
    /**
     * 缴费年期
     */
    private String payYears;
    /**
     * 终交年龄年期标志
     */
    private String payEndYearFlag;
    /**
     * 终交年龄年期
     */
    private String payEndYear;
    /**
     * 保险年龄年期标志
     */
    private String insuYearFlag;
    /**
     * 保险年龄年期
     */
    private String insuYear;
    /**
     * 终交日期
     */
    private String payEndDate;
    /**
     * 险种生效日
     */
    private String riskValiDate;
    /**
     * 险种失效日
     */
    private String riskEndDate;
    /**
     * 险种核保结论：1谢绝承保，2延期承保，3条件承保，4变更承保，5自核未通过，
     * 6待上级审核，7问题件，8延期承保，9正常承保，a撤销申请，b保险计划变更，
     * c税优待验证，E免责承保，z核保订正
     */
    private String riskUWResult;
    /**
     * 险种核保描述
     */
    private String riskUWInfo;
    /**
     * 险种责任列表
     */
    private List<Duty> dutys;
    /**
     * 受益人列表
     */
    private List<Bnf> bnfs;
    /**
     * 智能核保列表
     */
    private List<DiseaseInfo> diseaseInfos;
}
