package bean.dataBase.insure;

import lombok.*;

/**
 * @ClassName DiseaseInfo
 * @Description TODO 智能核保信息
 * @Author zhangyp
 * @Date 2020/3/7 22:59
 * @Version 1.0
 */
@Data
public class DiseaseInfo {
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
     * 一级疾病
     */
    private String firstDiseaseName;
    /**
     * 二级疾病
     */
    private String secondDiseaseName;
    /**
     * 智能核保问题描述
     */
    private String questionDesc;
    /**
     * 智能核保问题答案
     */
    private String questionAnswer;
}
