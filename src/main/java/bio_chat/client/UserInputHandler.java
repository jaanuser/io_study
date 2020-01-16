package bio_chat.client;

import java.io.*;
import java.net.Socket;

public class UserInputHandler implements Runnable {

    //客户端
    private ChatClient chatClient;

    public UserInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            //获取本地输入
            reader = new BufferedReader(
                    new InputStreamReader(System.in));
            //循坏等待用户输入
            while (true) {
                String msg = reader.readLine();

                chatClient.send(msg);
                //判断是否退出
                if (chatClient.readToQuit(msg)) {
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
