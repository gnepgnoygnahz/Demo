package bean.log.insure;

import lombok.Data;

import java.util.List;

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
     * 一级疾病
     */
    private String firstDiseaseName;
    /**
     * 二级疾病
     */
    private String secondDiseaseName;
    /**
     * 三级疾病列表
     */
    private List<QuestionInfo> questionInfos;
}
