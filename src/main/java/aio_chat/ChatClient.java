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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChatClient {
    private final String HOST = "127.0.0.1";
    private final int PORT = 8888;
    private AsynchronousSocketChannel channel = null;

    private void start(){
        //创建channel
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            channel = AsynchronousSocketChannel.open();
            Future<Void> future = channel.connect(new InetSocketAddress(HOST, PORT));
            future.get();
            //等待用户输入
            while (true) {
                String input = in.readLine();

                byte[] inputBytes = input.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(inputBytes);
                Future<Integer> writeResult = channel.write(buffer);

                writeResult.get();
                buffer.flip();
                Future<Integer> readResult = channel.read(buffer);

                readResult.get();
                String echo = new String(buffer.array());
                buffer.clear();

                System.out.println(echo);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();

    }
}
