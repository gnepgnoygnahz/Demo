package parse.insure;

import bean.common.Key;
import bean.common.PolicyState;
import bean.dataBase.insure.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import utils.BeanUtil;
import utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName InsureDataParseUDTF
 * @Description TODO 将原始数据分为几个表，每个表的数据在一个字段里面
 * @Author zhangyp
 * @Date 2020/3/16 0:48
 * @Version 1.0
 */
@Log4j2
public class InsureDataParse extends GenericUDTF {

    public static void main(String[] args) {
        /*Object[] objects = new Object[1];
        objects[0] = "{\"baseInfo\":{\"bankCode\":\"91\",\"businessType\":\"1100\",\"businessLicenseCode\":\"1199984Q\",\"tellerNo\":\"20070406591\",\"handler\":\"0\",\"transDate\":\"20200307\",\"transTime\":\"11:05:18\",\"transNo\":\"202003012182849222\",\"funcFlag\":\"01\",\"sourceType\":\"3\"},\"policy\":{\"grpContNo\":\"00000000000000000000\",\"contNo\":\"P2020110000087488\",\"prtNo\":\"202003012182850\",\"contType\":\"1\",\"familyType\":\"\",\"manageCom\":\"86110000\",\"agentGroup\":\"11001000003\",\"agentCom\":\"1100010040\",\"agentCode\":\"110010000004\",\"agentName\":\"e昆仑-北京\",\"contPlanCode\":\"YB707\",\"contPlanName\":\"福享金生保险产品计划（尊享版）\",\"polApplyDate\":\"20200307\",\"contValiDate\":\"20200309\",\"contEndDate\":\"21160309\",\"copys\":\"40\",\"totalPrem\":\"40000\",\"totalAmnt\":\"7125.00\",\"payIntv\":\"0\",\"payYears\":\"\",\"disputedFlag\":\"1\",\"renark\":\"1.您购买的保险产品为《福享金生保险产品计划（尊享版）》。该计划由康福来长期护理保险、附加康福来长期意外伤害保险、金银花长期护理保险（万能型）、附加聚宝盆长期意外伤害保险组成。2、首期保险费为康福来长期护理保险保费13972元、附加康福来长期意外伤害保险保费28元、金银花长期护理保险（万能型）及附加聚宝盆长期意外伤害保险保费合计26000元。\",\"payMethod\":\"17\",\"accName\":\"江会东\",\"accBankCode\":\"91\",\"bankAccNo\":\"6217994710024902054\",\"bankAgentName\":\"中国邮政储蓄银行股份有限公司临清市大桥营业所\",\"bankAgentNo\":\"20070406591\",\"policyUWResult\":\"1\"},\"appnt\":{\"appntNo\":\"C00000048671\",\"appntName\":\"江会东\",\"appntSex\":\"0\",\"appntBirthday\":\"19700301\",\"appntIDType\":\"0\",\"appntIDNo\":\"130535197003010238\",\"appntLongFlag\":\"0\",\"appntIDTo\":\"20261023\",\"appntNativePlace\":\"001\",\"appntProvCd\":\"130000\",\"appntCityCd\":\"130100\",\"appntCountyCd\":\"130102\",\"appntHomeAddress\":\"河北省省邢台市临西县河西镇第二中学家属院157号\",\"appntHomeZipCode\":\"252600\",\"appntContactAddress\":\"中国河北省邢台市临西县河西镇第二中学家属院157号\",\"appntContactZipCode\":\"252600\",\"appntMobile\":\"18830974806\",\"appntEmail\":\"18830974806@163.com\",\"appntJobCode\":\"1003002\",\"appntSalary\":\"100000\",\"appntFSalary\":\"0\",\"companyName\":\"\",\"companyAddress\":\"\",\"companyZipCode\":\"\",\"relaToMainInsured\":\"00\"},\"insureds\":[{\"insuredNo\":\"C00000048671\",\"insuredName\":\"江会东\",\"insuredsex\":\"0\",\"insuredBirthday\":\"19700301\",\"insuredIDType\":\"0\",\"insuredIDNo\":\"130535197003010238\",\"insuredLongFlag\":\"0\",\"insuredIDTo\":\"20261023\",\"insuredNativePlace\":\"001\",\"insuredProvCd\":\"130000\",\"insuredCityCd\":\"130100\",\"insuredCountyCd\":\"130102\",\"insuredHomeAddress\":\"河北省省邢台市临西县河西镇第二中学家属院157号\",\"insuredHomeZipCode\":\"252600\",\"insuredContactAddress\":\"中国河北省邢台市临西县河西镇第二中学家属院157号\",\"insuredContactZipCode\":\"252600\",\"InsuredMobile\":\"18830974806\",\"insuredEmail\":\"18830974806@163.com\",\"insuredJobCode\":\"1003002\",\"insuredSalary\":\"100000\",\"insuredFSalary\":\"0\",\"relaToAppnt\":\"00\",\"relaToMainInsured\":\"00\",\"risks\":[{\"polNo\":\"2198091\",\"riskName\":\"康福来长期护理保险\",\"riskCode\":\"C163\",\"mainRiskCode\":\"C163\",\"amnt\":\"15442.00\",\"prem\":\"13972.00\",\"copys\":\"14.0\",\"payIntv\":\"0\",\"payYears\":\"5\",\"payEndYearFlag\":\"Y\",\"payEndYear\":\"5\",\"insuYearFlag\":\"Y\",\"insuYear\":\"5\",\"payEndDate\":\"2025-03-02\",\"riskValiDate\":\"2020-03-02\",\"riskEndDate\":\"2025-03-02\",\"riskUWResult\":\"4\",\"riskUWInfo\":\"对乳腺原位癌、乳腺恶性肿瘤及其转移癌出险，我司不承担相关保险责任，给予客户两年后保全申请权利，提供三级以上医院确诊病历或由公司安排复查BI-RADS分级，如BI-RADS分级仍为3级或以下，经公司审核同意后，则可取消该除外约定\",\"dutys\":[{\"dutyCode\":\"167004\",\"dutyAmnt\":\"400000.00\",\"dutyPrem\":\"5152.00\"},{\"dutyCode\":\"167005\",\"dutyAmnt\":\"400000.00\",\"dutyPrem\":\"5152.00\"}],\"bnfs\":[{\"bnfType\":\"2\",\"bnfSeq\":\"1\",\"bnfGrade\":\"1\",\"bnfLot\":\"50\",\"bnfNo\":\"C00000048671\",\"bnfName\":\"刘烨\",\"bnfsex\":\"0\",\"bnfBirthday\":\"19890101\",\"bnfIDType\":\"0\",\"bnfIDNo\":\"140105198901012557\",\"bnfLongFlag\":\"0\",\"bnfIDTo\":\"20380301\",\"bnfNativePlace\":\"001\",\"bnfProvCd\":\"130000\",\"bnfCityCd\":\"130100\",\"bnfCountyCd\":\"130102\",\"bnfHomeAddress\":\"河北省省邢台市临西县河西镇第二中学家属院157号\",\"bnfHomeZipCode\":\"252600\",\"bnfContactAddress\":\"中国河北省邢台市临西县河西镇第二中学家属院157号\",\"bnfContactZipCode\":\"252600\",\"bnfMobile\":\"18830974806\",\"bnfEmail\":\"18830974806@163.com\",\"bnfJobCode\":\"1003002\",\"bnfSalary\":\"100000\",\"bnfFSalary\":\"0\",\"relationToInsured\":\"32\"},{\"bnfType\":\"2\",\"bnfNo\":\"2\",\"bnfGrade\":\"1\",\"bnfLot\":\"50\",\"bnfNo\":\"C00000048671\",\"bnfName\":\"刘烨\",\"bnfsex\":\"0\",\"bnfBirthday\":\"19890101\",\"bnfIDType\":\"0\",\"bnfIDNo\":\"140105198901012557\",\"bnfLongFlag\":\"0\",\"bnfIDTo\":\"20380301\",\"bnflace\":\"001\",\"bnfProvCd\":\"130000\",\"bnfCityCd\":\"130100\",\"bnfCountyCd\":\"130102\",\"bnfHomeAddress\":\"河北省省邢台市临西县河西镇第二中学家属院157号\",\"bnfHomeZipCode\":\"252600\",\"bnfContactAddress\":\"中国河北省邢台市临西县河西镇第二中学家属院157号\",\"bnfContactZipCode\":\"252600\",\"bnfMobile\":\"18830974806\",\"bnfEmail\":\"18830974806@163.com\",\"bnfJobCode\":\"1003002\",\"bnfSalary\":\"100000\",\"bnfFSalary\":\"0\",\"relationToInsured\":\"32\"}],\"diseaseInfos\":[{\"firstDiseaseName\":\"女性乳腺及妇科疾病、肿瘤（乳腺、卵巢、子宫）\",\"secondDiseaseName\":\"乳腺结节、囊肿（未行手术切）\",\"questionInfos\":[{\"questionDesc\":\"是否有近6个月内乳腺钼靶X线或乳腺彩色超声，并有明确BI-RADS分级报告？\",\"questionAnswer\":\"是\"},{\"questionDesc\":\"BI-RADS分级：1.BI-RADSII级（含）及以下；2.BI-RADSIII级；3..BI-RADSIV级（含）及以上\",\"questionAnswer\":\"2\"},{\"questionDesc\":\"是否已通过发送邮件申请核保，并已得到我公司回同意除外乳腺肿瘤相关保险责任承保的通知？\",\"questionAnswer\":\"是\"}]}]},{\"polNo\":\"2198092\",\"riskName\":\"附加康福来长期意外伤害保险\",\"riskCode\":\"A185\",\"mainRiskCode\":\"C163\",\"amnt\":\"15442.00\",\"prem\":\"28.00\",\"copys\":\"14.0\",\"payIntv\":\"0\",\"payYears\":\"5\",\"payEndYearFlag\":\"Y\",\"payEndYear\":\"5\",\"insuYearFlag\":\"Y\",\"insuYear\":\"5\",\"payEndDate\":\"2025-03-02\",\"riskValiDate\":\"2020-03-02\",\"riskEndDate\":\"2025-03-02\",\"uwResult\":\"9\",\"uwInfo\":\"\",\"dutys\":[{\"dutyCode\":\"168004\",\"dutyAmnt\":\"400000.00\",\"dutyPrem\":\"28.00\"},{\"dutyCode\":\"168005\",\"dutyAmnt\":\"400000.00\",\"dutyPrem\":\"28.00\"}],\"bnfs\":[{\"bnfType\":\"2\",\"bnfNo\":\"1\",\"bnfGrade\":\"1\",\"bnfLot\":\"50\",\"bnfNo\":\"C00000048671\",\"bnfName\":\"刘烨\",\"bnfsex\":\"0\",\"bnfBirthday\":\"19890101\",\"bnfIDType\":\"0\",\"bnfIDNo\":\"140105198901012557\",\"bnfLongFlag\":\"0\",\"bnfIDTo\":\"20380301\",\"bnflace\":\"001\",\"bnfProvCd\":\"130000\",\"bnfCityCd\":\"130100\",\"bnfCountyCd\":\"130102\",\"bnfHomeAddress\":\"河北省省邢台市临西县河西镇第二中学家属院157号\",\"bnfHomeZipCode\":\"252600\",\"bnfContactAddress\":\"中国河北省邢台市临西县河西镇第二中学家属院157号\",\"bnfContactZipCode\":\"252600\",\"bnfMobile\":\"18830974806\",\"bnfEmail\":\"18830974806@163.com\",\"bnfJobCode\":\"1003002\",\"bnfSalary\":\"100000\",\"bnfFSalary\":\"0\",\"relationToInsured\":\"32\"},{\"bnfType\":\"2\",\"bnfNo\":\"2\",\"bnfGrade\":\"1\",\"bnfLot\":\"50\",\"bnfNo\":\"C00000048671\",\"bnfName\":\"刘烨\",\"bnfsex\":\"0\",\"bnfBirthday\":\"19890101\",\"bnfIDType\":\"0\",\"bnfIDNo\":\"140105198901012557\",\"bnfLongFlag\":\"0\",\"bnfIDTo\":\"20380301\",\"bnflace\":\"001\",\"bnfProvCd\":\"130000\",\"bnfCityCd\":\"130100\",\"bnfCountyCd\":\"130102\",\"bnfHomeAddress\":\"河北省省邢台市临西县河西镇第二中学家属院157号\",\"bnfHomeZipCode\":\"252600\",\"bnfContactAddress\":\"中国河北省邢台市临西县河西镇第二中学家属院157号\",\"bnfContactZipCode\":\"252600\",\"bnfMobile\":\"18830974806\",\"bnfEmail\":\"18830974806@163.com\",\"bnfJobCode\":\"1003002\",\"bnfSalary\":\"100000\",\"bnfFSalary\":\"0\",\"relationToInsured\":\"32\"}],\"diseaseInfos\":[]}]}],\"tellInfo\":{\"tellInfoVersion\":\"09201913\",\"tellInfoQuestions\":[{\"tellInfoCode\":\"001\",\"tellInfoAnswer\":\"N,N\"},{\"tellInfoCode\":\"002\",\"tellInfoAnswer\":\"N\"},{\"tellInfoCode\":\"003\",\"tellInfoAnswer\":\"N\"},{\"tellInfoCode\":\"004\",\"tellInfoAnswer\":\"N,N\"},{\"tellInfoCode\":\"005\",\"tellInfoAnswer\":\"N,N,N\"}]}}";
        InsureDataParse insureDataParse = new InsureDataParse();
        //insureDataParse.process(objects);

        JSONObject object = JSON.parseObject((String) objects[0]);
        String baseInfo = object.getString("baseInfo");
        JSONObject baseInfoObject = JSON.parseObject(baseInfo);
        String funcFlag = baseInfoObject.getString("funcFlag");
        String contNo = object.getString("contNo");*/

        PolicyState policyState = new PolicyState();
        policyState.setGrpContNo("00000000000000000000");
        policyState.setContNo("P2020110000087488");
        policyState.setPrtNo("202003012182850");
        policyState.setStateFlag(Key.POLICYSTATE_TB);
        policyState.setStartDate(DateUtil.getCurrentDateYYMMDD());
        policyState.setEndDate(DateUtil.getCurrentDateYYMMDD());
        policyState.setMakeDate(DateUtil.getCurrentDateYYMMDD());
        policyState.setMakeTime(DateUtil.getCurrentTimeHHMMSS());
        //result[9] = BeanUtil.objToStringWithBreakLine(policyState);
        System.out.println((JSONObject.toJSON(policyState)));
    }

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) {

        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<>();

        fieldNames.add("dwd_policy");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_baseInfo");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_appnt");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_insured");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_risk");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_duty");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_bnf");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_diseaseInfo");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_tellInfo");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);

    }

    @Override
    public void process(Object[] args) {

        String input = args[0].toString();
        // 如果传进来的数据为空，直接返回过滤掉该数据
        if (!StringUtils.isBlank(input)) {

            try {
                JSONObject object = JSON.parseObject(input);
                bean.log.insure.InsureData insureData = object.toJavaObject(bean.log.insure.InsureData.class);

                String[] result = new String[9];

                //保单信息
                Policy policy = BeanUtil.copyProperties(insureData.getPolicy(), new Policy());
                String grpContNo = policy.getGrpContNo();
                String contNo = policy.getContNo();
                String prtNo = policy.getPrtNo();
                result[0] = BeanUtil.objToStringWithBreakLine(policy);

                //交易基本信息
                BaseInfo baseInfo = BeanUtil.copyProperties(insureData.getBaseInfo(), new BaseInfo());
                baseInfo.setGrpContNo(grpContNo);
                baseInfo.setContNo(contNo);
                baseInfo.setPrtNo(prtNo);
                result[1] = BeanUtil.objToStringWithBreakLine(baseInfo);

                //投保人信息
                Appnt appnt = BeanUtil.copyProperties(insureData.getAppnt(), new Appnt());
                appnt.setGrpContNo(grpContNo);
                appnt.setContNo(contNo);
                appnt.setPrtNo(prtNo);
                result[2] = BeanUtil.objToStringWithBreakLine(appnt);

                StringBuilder insuredBuilder = new StringBuilder();
                StringBuilder riskBuilder = new StringBuilder();
                StringBuilder dutyBuilder = new StringBuilder();
                StringBuilder bnfBuilder = new StringBuilder();
                StringBuilder diseaseInfoBuilder = new StringBuilder();

                //被保人信息
                List<bean.log.insure.Insured> insureds = insureData.getInsureds();
                for (bean.log.insure.Insured lInsured : insureds) {
                    Insured insured = BeanUtil.copyProperties(lInsured, new Insured());
                    insured.setGrpContNo(grpContNo);
                    insured.setContNo(contNo);
                    insured.setPrtNo(prtNo);
                    insuredBuilder.append(BeanUtil.objToStringWithBreakLine(insured)).append(Key.SEPARATORTAB);

                    //险种信息
                    List<bean.log.insure.Risk> risks = lInsured.getRisks();
                    for (bean.log.insure.Risk lRisk : risks) {
                        Risk risk = BeanUtil.copyProperties(lRisk, new Risk());
                        risk.setGrpContNo(grpContNo);
                        risk.setContNo(contNo);
                        risk.setPrtNo(prtNo);
                        risk.setInsuredNo(insured.getInsuredNo());
                        riskBuilder.append(BeanUtil.objToStringWithBreakLine(risk)).append(Key.SEPARATORTAB);

                        //险种责任信息
                        List<bean.log.insure.Duty> dutys = lRisk.getDutys();
                        for (bean.log.insure.Duty lDuty : dutys) {
                            Duty duty = BeanUtil.copyProperties(lDuty, new Duty());
                            duty.setGrpContNo(grpContNo);
                            duty.setContNo(contNo);
                            duty.setPrtNo(prtNo);
                            duty.setPolNo(risk.getPolNo());
                            dutyBuilder.append(BeanUtil.objToStringWithBreakLine(duty)).append(Key.SEPARATORTAB);
                        }

                        //受益人信息
                        List<bean.log.insure.Bnf> bnfs = lRisk.getBnfs();
                        for (bean.log.insure.Bnf lBnf : bnfs) {
                            Bnf bnf = BeanUtil.copyProperties(lBnf, new Bnf());
                            bnf.setGrpContNo(grpContNo);
                            bnf.setContNo(contNo);
                            bnf.setPrtNo(prtNo);
                            bnf.setPolNo(risk.getPolNo());
                            bnfBuilder.append(BeanUtil.objToStringWithBreakLine(bnf)).append(Key.SEPARATORTAB);
                        }

                        //智能核保信息
                        List<bean.log.insure.DiseaseInfo> diseaseInfos = lRisk.getDiseaseInfos();
                        for (bean.log.insure.DiseaseInfo lDiseaseInfo : diseaseInfos) {
                            DiseaseInfo diseaseInfo = BeanUtil.copyProperties(lDiseaseInfo, new DiseaseInfo());
                            diseaseInfo.setGrpContNo(grpContNo);
                            diseaseInfo.setContNo(contNo);
                            diseaseInfo.setPrtNo(prtNo);
                            diseaseInfo.setPolNo(risk.getPolNo());
                            List<bean.log.insure.QuestionInfo> questionInfos = lDiseaseInfo.getQuestionInfos();
                            for (bean.log.insure.QuestionInfo questionInfo : questionInfos) {
                                diseaseInfo.setQuestionDesc(questionInfo.getQuestionDesc());
                                diseaseInfo.setQuestionAnswer(questionInfo.getQuestionAnswer());
                                diseaseInfoBuilder.append(BeanUtil.objToStringWithBreakLine(diseaseInfo)).append(Key.SEPARATORTAB);
                            }
                        }
                    }
                }

                result[3] = insuredBuilder.length() > 0 ? insuredBuilder.substring(0, insuredBuilder.length() - 1) : "";
                result[4] = riskBuilder.length() > 0 ? riskBuilder.substring(0, riskBuilder.length() - 1) : "";
                result[5] = dutyBuilder.length() > 0 ? dutyBuilder.substring(0, dutyBuilder.length() - 1) : "";
                result[6] = bnfBuilder.length() > 0 ? bnfBuilder.substring(0, bnfBuilder.length() - 1) : "";
                result[7] = diseaseInfoBuilder.length() > 0 ? diseaseInfoBuilder.substring(0, diseaseInfoBuilder.length() - 1) : "";

                //健康告知信息
                StringBuilder tellInfoBuilder = new StringBuilder();
                bean.log.insure.TellInfo lTellInfo = insureData.getTellInfo();
                String tellInfoVersion = lTellInfo.getTellInfoVersion();
                List<bean.log.insure.TellInfoQuestion> tellInfoQuestions = lTellInfo.getTellInfoQuestions();
                for (bean.log.insure.TellInfoQuestion lTellInfoQuestion : tellInfoQuestions) {
                    TellInfo tellInfo = BeanUtil.copyProperties(lTellInfoQuestion, new TellInfo());
                    tellInfo.setGrpContNo(grpContNo);
                    tellInfo.setContNo(contNo);
                    tellInfo.setPrtNo(prtNo);
                    tellInfo.setTellInfoVersion(tellInfoVersion);
                    tellInfoBuilder.append(BeanUtil.objToStringWithBreakLine(tellInfo)).append(Key.SEPARATORTAB);
                }
                result[8] = tellInfoBuilder.length() > 0 ? tellInfoBuilder.substring(0, tellInfoBuilder.length() - 1) : "";

                //保单状态信息
                PolicyState policyState = new PolicyState();
                policyState.setGrpContNo(grpContNo);
                policyState.setContNo(contNo);
                policyState.setPrtNo(prtNo);
                policyState.setStateFlag(Key.POLICYSTATE_TB);
                policyState.setStartDate(DateUtil.getCurrentDateYYMMDD());
                policyState.setEndDate(DateUtil.getCurrentDateYYMMDD());
                log.info(JSONObject.toJSON(policyState));

                // 将结果返回
                forward(result);

            } catch (HiveException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {

    }

}
