package bio_chat.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private final String IP = "127.0.0.1";
    private final int PORT = 8888;
    public final String QUIT = "quit";
    public Socket socket;

    public void start() throws IOException {
        //socket
        socket = new Socket(IP, PORT);
        System.out.println("连接成功!");
        //获取输入流
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        //写入消息
        Thread thread = new Thread(new UserInputHandler(this));
        thread.start();
        //读取消息
        while (true) {
            String msg = reader.readLine();
            if(msg == null ) break;
            System.out.println(msg);
        }


    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        try {
            chatClient.start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                chatClient.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
