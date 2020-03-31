package bean.log.insure;

import lombok.Data;

/**
 * @ClassName QuestionInfo
 * @Description TODO 智能核保问题信息
 * @Author zhangyp
 * @Date 2020/3/7 23:01
 * @Version 1.0
 */
@Data
public class QuestionInfo {
    /**
     * 智能核保问题描述
     */
    private String questionDesc;
    /**
     * 智能核保问题答案
     */
    private String questionAnswer;
}
