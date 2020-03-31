package bean.common;

import lombok.Data;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName PolicyState
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/24 15:55
 * @Version 1.0
 */
@Data
public class PolicyState implements WritableComparable<PolicyState> {
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
     * 保单状态：0投保，1承保，2生效，3终止
     */
    private String stateFlag;
    /**
     * 状态开始时间
     */
    private String startDate;
    /**
     * 状态结束时间
     */
    private String endDate;
    /**
     * 创建日期
     */
    private String makeDate;
    /**
     * 创建时间
     */
    private String makeTime;

    public static void main(String[] args) {
        PolicyState policyState1 = new PolicyState();
        policyState1.setMakeDate("2020-03-26");
        policyState1.setMakeTime("11:11:11");
        PolicyState policyState2 = new PolicyState();
        policyState2.setMakeDate("2020-03-27");
        policyState2.setMakeTime("10:11:11");
        List<PolicyState> arrayList = new ArrayList<>();
        arrayList.add(policyState1);
        arrayList.add(policyState2);
        Collections.sort(arrayList);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(grpContNo);
        dataOutput.writeUTF(contNo);
        dataOutput.writeUTF(prtNo);
        dataOutput.writeUTF(stateFlag);
        dataOutput.writeUTF(startDate);
        dataOutput.writeUTF(endDate);
        dataOutput.writeUTF(makeDate);
        dataOutput.writeUTF(makeTime);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        grpContNo = dataInput.readUTF();
        contNo = dataInput.readUTF();
        prtNo = dataInput.readUTF();
        stateFlag = dataInput.readUTF();
        startDate = dataInput.readUTF();
        endDate = dataInput.readUTF();
        makeDate = dataInput.readUTF();
        makeTime = dataInput.readUTF();
    }

    @Override
    public int compareTo(PolicyState o) {
        return makeDate.equals(o.getMakeDate()) ? makeTime.compareTo(o.getMakeTime()) : makeDate.compareTo(o.getMakeDate());
    }
}
