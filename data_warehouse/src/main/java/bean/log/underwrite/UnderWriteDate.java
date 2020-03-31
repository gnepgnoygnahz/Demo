package bean.log.underwrite;


import lombok.Data;

/**
 * @ClassName UnderWriteDate
 * @Description TODO 承保信息总和
 * @Author zhangyp
 * @Date 2020/3/8 0:01
 * @Version 1.0
 */
@Data
public class UnderWriteDate {

    /**
     * 交易基本信息
     */
    private BaseInfo baseInfo;
    /**
     * 承保信息
     */
    private ConfirmInfo confirmInfo;
}
