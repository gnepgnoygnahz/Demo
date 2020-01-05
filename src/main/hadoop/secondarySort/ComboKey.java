package hadoop.secondarySort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ComboKey implements WritableComparable<ComboKey> {
    private int year;
    private int temperature;

    @Override
    public int compareTo(ComboKey o) {
        return year == o.getYear() ? o.getTemperature() - temperature : year - o.getYear();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(year);
        dataOutput.writeInt(temperature);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        year = dataInput.readInt();
        temperature = dataInput.readInt();
    }
}
