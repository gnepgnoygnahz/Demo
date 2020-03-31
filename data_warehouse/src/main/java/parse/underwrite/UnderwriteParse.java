package parse.underwrite;

import bean.common.Key;
import bean.dataBase.insure.BaseInfo;
import bean.common.PolicyState;
import bean.dataBase.underwrite.PolicyPay;
import bean.log.underwrite.UnderWriteDate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import utils.BeanUtil;
import utils.DateUtil;

import java.util.ArrayList;

/**
 * @ClassName UnderWriteParse
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/24 22:27
 * @Version 1.0
 */
public class UnderwriteParse extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<>();

        fieldNames.add("dwd_PolicyPay");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_baseInfo");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("dwd_policyState");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    @Override
    public void process(Object[] args) throws HiveException {
        String input = args[0].toString();
        // 如果传进来的数据为空，直接返回过滤掉该数据
        if (!StringUtils.isBlank(input)) {

            try {
                JSONObject object = JSON.parseObject(input);
                UnderWriteDate underWriteDate = object.toJavaObject(UnderWriteDate.class);

                String[] result = new String[3];

                //承保信息
                PolicyPay policyPay = BeanUtil.copyProperties(underWriteDate.getConfirmInfo(), new PolicyPay());
                String grpContNo = policyPay.getGrpContNo();
                String contNo = policyPay.getContNo();
                String prtNo = policyPay.getPrtNo();
                result[0] = BeanUtil.objToStringWithBreakLine(policyPay);

                //交易基本信息
                BaseInfo baseInfo = BeanUtil.copyProperties(underWriteDate.getBaseInfo(), new BaseInfo());
                baseInfo.setGrpContNo(grpContNo);
                baseInfo.setContNo(contNo);
                baseInfo.setPrtNo(prtNo);
                result[1] = BeanUtil.objToStringWithBreakLine(baseInfo);

                //保单状态信息
                PolicyState policyState = new PolicyState();
                policyState.setGrpContNo(grpContNo);
                policyState.setContNo(contNo);
                policyState.setPrtNo(prtNo);
                policyState.setStateFlag(Key.POLICYSTATE_CB);
                policyState.setStartDate(DateUtil.getCurrentDateYYMMDD());
                policyState.setEndDate(Key.MAX_DATE);
                result[2] = BeanUtil.objToStringWithBreakLine(policyState);

                forward(result);

            } catch (HiveException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws HiveException {

    }
}
