package nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * position 执行操作的索引
 * limit 缓冲区中可操作的数据大小
 * capacity 缓冲区的大小
 *
 * @author zhang
 */
public class BufferTest {
    public static void main(String[] args) throws IOException {
        //创建一个文件对象
        RandomAccessFile f = new RandomAccessFile("C:\\Users\\zhang\\Desktop\\a.txt", "rw");
        //得到一个通道对象
        FileChannel fc = f.getChannel();
        //创建一个缓冲区 容量为10
        ByteBuffer buf1 = ByteBuffer.allocate(10);
        ByteBuffer buf2 = ByteBuffer.allocate(10);
        ByteBuffer[] bufArr = {buf1, buf2};
        //buf.mark()标记position的当前位置;之后调用buf.reset()回到此位置;buf.rewind()将position设置为0
        //将数据从管道读入到缓冲区中，返回值是读取的字节数
        long i = fc.read(bufArr);
        //如果有数据，进入循环
        while (i != -1) {
            //将缓冲区状态改为可读状态
            buf1.flip();
            //如果还有数据进入循环
            while (buf1.hasRemaining()) {
                System.out.print((char) buf1.get());
            }
            //清空缓冲区，缓冲区状态变为可写入状态，但数据未被清空只是position变为0，数据依然在
            buf1.clear();
            //如果缓冲区存在为被读出的数据，则将这些数据移到缓冲区头部position位置变与为数据大小数值相等的索引处
            //新数据从此处写入
            //buf2.compact();
            //从channle中读数据到缓冲区中,必须写这行代码，不然会一直运行
            i = fc.read(bufArr);
        }
        f.close();
    }
}
