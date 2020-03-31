package bean.log.insure;

import lombok.*;

/**
 * @ClassName Tou
 * @Description TODO 交易基本信息
 * @Author zhangyp
 * @Date 2020/3/7 22:55
 * @Version 1.0
 */
@Data
public class BaseInfo {
    /**
     * 银行编码：02工商银行，03农业银行，05建设银行，10广东发展银行，23华夏银行，91邮政储蓄银行，
     */
    private String bankCode;
    /**
     * 行业分类
     */
    private String businessType;
    /**
     * 工商执照编码
     */
    private String businessLicenseCode;
    /**
     * 操作人
     */
    private String handler;
    /**
     * 渠道编码
     */
    private String saleChnl;
    /**
     * 交易日期yyyymmdd
     */
    private String transDate;
    /**
     * 交易时间HH24mmss
     */
    private String transTime;
    /**
     * 交易流水号
     */
    private String transNo;
    /**
     * 交易接口
     */
    private String funcFlag;
    /**
     * 业务来源：0银保柜面，1网络银行，2银保线下，
     * 3手机银行，5微信代理，6微信直销，8自助终端，
     * 9手机银行，10移动展业，16个人营销
     */
    private String sourceType;


}
