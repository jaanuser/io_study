package aio_chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private final int PORT = 8888;
    private AsynchronousServerSocketChannel serverChannel = null;

    private void start() {
        try {
            serverChannel = AsynchronousServerSocketChannel.open();

            serverChannel.bind(new InetSocketAddress(PORT));
            System.out.println("server已启动");

            serverChannel.accept(null, new AcceptHandler());
            System.out.println("accept");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

        @Override
        public void completed(AsynchronousSocketChannel result, Object attachment) {
            System.out.println("有新的连接");
            if (serverChannel.isOpen()) {
                serverChannel.accept(null, new AcceptHandler());
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Map<String, Object> info = new HashMap<>();
            info.put("type", "read");
            info.put("buffer", buffer);
            result.read(buffer, info, new ClientHandler(result));

        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    private class ClientHandler implements CompletionHandler {
        private AsynchronousSocketChannel channel;

        public ClientHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Object result, Object attachment) {
            Map<String, Object> info = (Map<String, Object>) attachment;
            ByteBuffer buffer = (ByteBuffer) info.get("buffer");
            if ("read".equals(info.get("type"))) {
                System.out.println(buffer.toString());
                buffer.flip();
                info.put("type", "write");
                channel.write(buffer, info, new ClientHandler(channel));
            } else {
                buffer.clear();
                buffer.flip();
                info.put("type", "read");
                channel.read(buffer, info, new ClientHandler(channel));
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}
