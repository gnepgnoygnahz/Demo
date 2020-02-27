package customerOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderKey implements WritableComparable<CustomerOrderKey> {

    private int type;

    private int cid;

    private int oid;

    private String customerInfo = "";

    private String orderInfo = "";

    @Override
    public int compareTo(CustomerOrderKey o) {
        int type1 = o.type;
        int cid1 = o.cid;
        int oid1 = o.oid;
        if (cid == cid1) {
            if (type == type1) {
                return oid - oid1;
            } else {
                return type - type1;
            }
        } else {
            return cid - cid1;
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(type);
        out.writeInt(cid);
        out.writeInt(oid);
        out.writeUTF(customerInfo);
        out.writeUTF(orderInfo);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        type = in.readInt();
        cid = in.readInt();
        oid = in.readInt();
        customerInfo = in.readUTF();
        orderInfo = in.readUTF();
    }
}
