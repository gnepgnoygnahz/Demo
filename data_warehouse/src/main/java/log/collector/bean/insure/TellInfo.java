package log.collector.bean.insure;

import lombok.Data;

import java.util.List;

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
     * 告知版本编码
     */
    private String tellInfoVersion;
    /**
     * 告知问题列表
     */
    private List<TellInfoQuestion> tellInfoQuestions;
}
