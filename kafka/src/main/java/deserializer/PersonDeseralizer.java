package deserializer;

import pojo.Person;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Create By Zhangyp
 * Date:  2019/11/9
 * Desc:
 */
public class PersonDeseralizer implements Deserializer<Person> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Person deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length < 8) {
            throw new SerializationException("Size of data received is shorter than expected!");
        }
        int nameLen, sexLen;
        String name = null, sex = null;
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            nameLen = buffer.getInt();
            byte[] nameBytes = new byte[nameLen];
            buffer.get(nameBytes);
            sexLen = buffer.getInt();
            byte[] sexBytes = new byte[sexLen];
            buffer.get(sexBytes);
            name = new String(nameBytes, "UTF-8");
            sex = new String(sexBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
        }
        return Person.builder().name(name).sex(sex).build();

    }

    /*@Override
    //不需要重写这个方法
    public Person deserialize(String topic, Headers headers, byte[] data) {
        return null;
    }*/

    @Override
    public void close() {

    }
}
