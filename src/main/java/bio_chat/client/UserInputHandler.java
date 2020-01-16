package bio_chat.client;

import java.io.*;
import java.net.Socket;

public class UserInputHandler implements Runnable {

    //客户端
    ChatClient chatClient;
    private Socket socket;

    public UserInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
        socket = chatClient.socket;
    }

    public void start() {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            //获取本地输入和客户端soket输出
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(
                    new InputStreamReader(System.in));
            //循坏等待用户输入
            while (true) {
                String msg = reader.readLine();
                writer.write(msg + '\n');
                writer.flush();
                //判断是否退出
                if (chatClient.QUIT.equals(msg)) {
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        start();
    }
}
