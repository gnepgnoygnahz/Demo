package bean.log.insure;


import lombok.Data;

import java.util.List;

/**
 * @ClassName InsureData
 * @Description TODO 投保交易信息总和
 * @Author zhangyp
 * @Date 2020/3/7 23:47
 * @Version 1.0
 */
@Data
public class InsureData {
    /**
     * 交易基本信息
     */
    private BaseInfo baseInfo;
    /**
     * 保单信息
     */
    private Policy policy;
    /**
     * 投保人信息
     */
    private Appnt appnt;
    /**
     * 被保人信息
     */
    private List<Insured> insureds;
    /**
     * 告知信息
     */
    private TellInfo tellInfo;
}
