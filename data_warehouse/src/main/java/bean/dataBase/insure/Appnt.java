package bean.dataBase.insure;

import lombok.*;

/**
 * @ClassName Tou
 * @Description TODO 投保人信息
 * @Author zhangyp
 * @Date 2020/3/7 22:55
 * @Version 1.0
 */
@Data
public class Appnt {
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
     * 投保人客户号
     */
    private String appntNo;
    /**
     * 投保人姓名
     */
    private String appntName;
    /**
     * 投保人性别
     */
    private String appntSex;
    /**
     * 投保人生日yyyymmdd
     */
    private String appntBirthday;
    /**
     * 投保人证件类型
     */
    private String appntIDType;
    /**
     * 投保人证件号
     */
    private String appntIDNo;
    /**
     * 投保人证件是否为长期：0非长期，1长期
     */
    private String appntLongFlag;
    /**
     * 投保人证件止期
     */
    private String appntIDTo;
    /**
     * 投保人国籍
     */
    private String appntNativePlace;
    /**
     * 投保人省份编码
     */
    private String appntProvCd;
    /**
     * 投保人市编码
     */
    private String appntCityCd;
    /**
     * 投保人区/县编码
     */
    private String appntCountyCd;
    /**
     * 投保人家庭地址
     */
    private String appntHomeAddress;
    /**
     * 投保人家庭地址邮编
     */
    private String appntHomeZipCode;
    /**
     * 投保人联系地址
     */
    private String appntContactAddress;
    /**
     * 投保人联系地址编码
     */
    private String appntContactZipCode;
    /**
     * 投保人手机号
     */
    private String appntMobile;
    /**
     * 投保人邮箱
     */
    private String appntEmail;
    /**
     * 投保人职业编码
     */
    private String appntJobCode;

    /**
     * 投保人年薪
     */
    private String appntSalary;
    /**
     * 投保人家庭年薪
     */
    private String appntFSalary;
    /**
     * 公司名称：团单时候有值，个单无值
     */
    private String companyName;
    /**
     * 投保公司地址：团单时候有值，个单无值
     */
    private String companyAddress;
    /**
     * 投保公司地址邮编：团单时候有值，个单无值
     */
    private String companyZipCode;
    /**
     * 投保人与主被保险人关系
     */
    private String relaToMainInsured;
}
