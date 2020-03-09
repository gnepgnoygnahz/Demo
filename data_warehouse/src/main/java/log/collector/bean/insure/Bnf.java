package log.collector.bean.insure;

import lombok.Data;

/**
 * @ClassName Bnf
 * @Description TODO 受益人信息
 * @Author zhangyp
 * @Date 2020/3/7 22:59
 * @Version 1.0
 */
@Data
public class Bnf {
    /**
     * 受益人类别：1生存受益人，2死亡受益人
     */
    private String bnfType;
    /**
     * 受益人序号
     */
    private String bnfSeq;
    /**
     * 受益人级别
     */
    private String bnfGrade;
    /**
     * 受益份额
     */
    private String bnfLot;
    /**
     * 受益人客户号
     */
    private String bnfNo;
    /**
     * 受益人姓名
     */
    private String bnfName;
    /**
     * 受益人性别
     */
    private String bnfSex;
    /**
     * 受益人生日yyyymmdd
     */
    private String bnfBirthday;
    /**
     * 受益人证件类型
     */
    private String bnfIDType;
    /**
     * 受益人证件号
     */
    private String bnfIDNo;
    /**
     * 受益人证件是否为长期：0非长期，1长期
     */
    private String bnfLongFlag;
    /**
     * 受益人证件止期
     */
    private String bnfIDTo;
    /**
     * 受益人国籍
     */
    private String bnfNativePlace;
    /**
     * 受益人省份编码
     */
    private String bnfProvCd;
    /**
     * 受益人市编码
     */
    private String bnfCityCd;
    /**
     * 受益人区/县编码
     */
    private String bnfCountyCd;
    /**
     * 受益人家庭地址
     */
    private String bnfHomeAddress;
    /**
     * 受益人家庭地址邮编
     */
    private String bnfHomeZipCode;
    /**
     * 受益人联系地址
     */
    private String bnfContactAddress;
    /**
     * 受益人联系地址编码
     */
    private String bnfContactZipCode;
    /**
     * 受益人手机号
     */
    private String bnfMobile;
    /**
     * 受益人邮箱
     */
    private String bnfEmail;
    /**
     * 受益人职业编码
     */
    private String bnfJobCode;

    /**
     * 受益人年薪
     */
    private String bnfSalary;
    /**
     * 受益人家庭年薪
     */
    private String bnfFSalary;
    /**
     * 受益人与被保险人关系
     */
    private String relationToInsured;
}
