package nio;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SelectorTest {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        Selector s = Selector.open();
        sc.configureBlocking(false);
        SelectionKey key = sc.register(s, SelectionKey.OP_READ);
        int interestOps2 = key.interestOps();//1
        int interestOps = SelectionKey.OP_READ | SelectionKey.OP_ACCEPT;//位运算
        System.out.println(SelectionKey.OP_READ);//1
        System.out.println(SelectionKey.OP_WRITE);//4
        System.out.println(SelectionKey.OP_CONNECT);//8
        System.out.println(SelectionKey.OP_ACCEPT);//16
        int readyOps = key.readyOps();
        System.out.println();
        System.out.println(key.isReadable());
        System.out.println(key.isWritable());
        System.out.println(key.isAcceptable());
        System.out.println(key.isConnectable());
        Channel channel = key.channel();
        Selector selector = key.selector();
    }
}