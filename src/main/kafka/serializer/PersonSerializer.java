package kafka.serializer;

import kafka.pojo.Person;
import org.apache.kafka.common.serialization.Serializer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Create By Zhangyp
 * Date:  2019/11/9
 * Desc:
 */
public class PersonSerializer implements Serializer<Person> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Person data) {
        if (data == null) {
            return null;
        }
        byte[] name, sex;
        try {
            if (data.getName() != null) {
                name = data.getName().getBytes("UTF-8");
            } else {
                name = new byte[0];
            }
            if (data.getSex() != null) {
                sex = data.getSex().getBytes("UTF-8");
            } else {
                sex = new byte[0];
            }
            ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + name.length + sex.length);
            buffer.putInt(name.length);
            buffer.put(name);
            buffer.putInt(sex.length);
            buffer.put(sex);
            return buffer.array();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
        }
        return new byte[0];
    }

    /*@Override
    public byte[] serialize(String topic, Headers headers, Person data) {
        return new byte[0];
    }*/

    @Override
    public void close() {

    }
}
