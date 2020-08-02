package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NIOServer {

    int port;
    ServerSocketChannel server;
    Selector selector;
    ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
    ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    Map<SelectionKey, String> sessionMsg = new HashMap<>();


    public NIOServer(int port) throws IOException {
        this.port = port;
        server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(port));
        server.configureBlocking(false);
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NIO服务已经启动，监听端口是：" + this.port);
    }

    public void listener() throws IOException {
        while (true) {
            int i = selector.select();
            if (i == 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                process(iterator.next());
                iterator.remove();
            }
        }
    }

    private void process(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            receiveBuffer.clear();
            SocketChannel client = (SocketChannel) key.channel();
            int len = client.read(receiveBuffer);
            if (len > 0) {
                String msg = new String(receiveBuffer.array(), 0, len);
                sessionMsg.put(key, msg);
                System.out.println("获取客户端发送来的消息" + msg);
            }
            client.register(selector, SelectionKey.OP_WRITE);
        } else if (key.isWritable()) {
            if (!sessionMsg.containsKey(key)) {
                return;
            }
            SocketChannel client = (SocketChannel) key.channel();
            sendBuffer.clear();
            sendBuffer.put(new String(sessionMsg.get(key) + ",您好,您的请求已处理完成").getBytes());
            sendBuffer.flip();
            client.write(sendBuffer);
            client.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOServer(8080).listener();
    }

}
