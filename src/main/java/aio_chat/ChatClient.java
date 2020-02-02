package aio_chat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

public class ChatClient {
    private final String HOST = "127.0.0.1";
    private final int PORT = 8888;
    private AsynchronousSocketChannel channel = null;

    private void start() {
        try {
            channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress(HOST, PORT), null, new ConnectHandler());
            //如果不写下面的一行,server能接收到连接通知,并能打印有新的连接,但是client执行完上面一句.后面的没执行.
            System.in.read();
//            连接成功
//            等待输入
//            1(后直接退出)
//
//            Process finished with exit code 0
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class ConnectHandler implements CompletionHandler {

        @Override
        public void completed(Object result, Object attachment) {
            System.out.println("连接成功");
            ByteBuffer rbuffer = ByteBuffer.allocate(1024);
            ByteBuffer wbuffer = ByteBuffer.allocate(1024);
            Map<String, Object> rinfo = new HashMap<>();
            Map<String, Object> winfo = new HashMap<>();
            rinfo.put("type", "read");
            rinfo.put("buffer", rbuffer);
            channel.read(rbuffer, rinfo, new ClientHandler());
            winfo.put("type", "write");
            winfo.put("buffer", wbuffer);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    System.out.println("等待输入");
                    String msg = in.readLine();
                    wbuffer.put(msg.getBytes());
                    channel.write(wbuffer, winfo, new ClientHandler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    private class ClientHandler implements CompletionHandler {

        @Override
        public void completed(Object result, Object attachment) {
            Map<String, Object> info = (Map<String, Object>) attachment;
            ByteBuffer buffer = (ByteBuffer) info.get("buffer");
            System.out.println(info.get("type"));
            if ("write".equals(info.get("type"))) {
                System.out.println("已发送");
            } else {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.println(buffer.toString());
                }
                buffer.clear();
                channel.read(buffer, info, new ClientHandler());
            }
        }
        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
    //如果debug运行,clinet打印下面,server显示新连接,但是没能收到消息
//    连接成功
//            等待输入
//1
//    等待输入
//            write
//    已发送
//    Disconnected from the target VM, address: '127.0.0.1:55645', transport: 'socket'
//
//    Process finished with exit code 0
}
