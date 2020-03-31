package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName LogUtil
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/21 17:17
 * @Version 1.0
 */
public class LogUtil {

    public static String getTopic(String data) {

        String topic = "error";

        try {
            JSONObject object = JSON.parseObject(data);
            String baseInfo = object.getString("baseInfo");
            JSONObject baseInfoObject = JSON.parseObject(baseInfo);
            String funcFlag = baseInfoObject.getString("funcFlag");
            String transNo = baseInfoObject.getString("transNo");

            if (!"".equals(transNo) && null != transNo) {
                switch (funcFlag) {
                    case "00":
                        topic = "underwrite";
                        break;
                    case "01":
                        topic = "insure";
                        break;
                    default:
                        topic = "error";
                }
            }

        } catch (Exception e) {
            topic = "error";
            e.printStackTrace();
        }
        return topic;
    }

    public static void main(String[] args) {
        String topic = getTopic("{\"baseInfo\":{\"bankCode\":\"91\",\"bankDate\":\"20200303\",\"bankTime\":\"145412\",\"zoneNo\":\"1100\",\"brNo\":\"1199984Q\",\"tellerNo\":\"20070406591\",\"saleChnl\":\"0\",\"insuID\":\"0026\",\"transNo\":\"2020030359381600004\",\"funcFlag\":\"00\",\"SourceType\":\"3\"},\"confirmInfo\":{\"disputedFlag\":\"1\",\"prtNo\":\"202003012182850\",\"grpContNo\":\"202003012182850\",\"totalPrem\":\"40000.00\",\"totalAmnt\":\"7125.00\",\"accName\":\"江会东\",\"accBankCode\":\"91\",\"bankAccNo\":\"6217994710024902054\",\"bankAgentName\":\"中国邮政储蓄银行股份有限公司临清市大桥营业所\",\"contNo\":\"P2020110000087488\"}}");
        System.out.println(topic);
    }

}
