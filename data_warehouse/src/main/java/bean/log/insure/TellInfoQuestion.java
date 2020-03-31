package bean.log.insure;

import lombok.Data;

/**
 * @ClassName TellInfoQuestion
 * @Description TODO 告知问题信息
 * @Author zhangyp
 * @Date 2020/3/7 23:03
 * @Version 1.0
 */
@Data
public class TellInfoQuestion {
    /**
     * 告知问题编码
     */
    private String tellInfoCode;
    /**
     * 告知问题答案
     */
    private String tellInfoAnswer;
}
