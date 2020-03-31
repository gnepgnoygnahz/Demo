package bean.dataBase.insure;

import lombok.*;

/**
 * @ClassName Tou
 * @Description TODO 被保人信息
 * @Author zhangyp
 * @Date 2020/3/7 22:55
 * @Version 1.0
 */
@Data
public class Insured {
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
     * 被保人客户号
     */
    private String insuredNo;
    /**
     * 被保人姓名
     */
    private String insuredName;
    /**
     * 被保人性别
     */
    private String insuredSex;
    /**
     * 被保人生日yyyymmdd
     */
    private String insuredBirthday;
    /**
     * 被保人证件类型
     */
    private String insuredIDType;
    /**
     * 被保人证件号
     */
    private String insuredIDNo;
    /**
     * 被保人证件是否为长期：0非长期，1长期
     */
    private String insuredLongFlag;
    /**
     * 被保人证件止期
     */
    private String insuredIDTo;
    /**
     * 被保人国籍
     */
    private String insuredNativePlace;
    /**
     * 被保人省份编码
     */
    private String insuredProvCd;
    /**
     * 被保人市编码
     */
    private String insuredCityCd;
    /**
     * 被保人区/县编码
     */
    private String insuredCountyCd;
    /**
     * 被保人家庭地址
     */
    private String insuredHomeAddress;
    /**
     * 被保人家庭地址邮编
     */
    private String insuredHomeZipCode;
    /**
     * 被保人联系地址
     */
    private String insuredContactAddress;
    /**
     * 被保人联系地址编码
     */
    private String insuredContactZipCode;
    /**
     * 被保人手机号
     */
    private String insuredMobile;
    /**
     * 被保人邮箱
     */
    private String insuredEmail;
    /**
     * 被保人职业编码
     */
    private String insuredJobCode;
    /**
     * 被保人年薪
     */
    private String insuredSalary;
    /**
     * 被保人家庭年薪
     */
    private String insuredFSalary;
    /**
     * 被保人与投保关系
     */
    private String relaToAppnt;
    /**
     * 被保人与主被保险人关系
     */
    private String relaToMainInsured;
}
