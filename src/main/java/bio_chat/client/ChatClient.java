package bio_chat.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private final String IP = "127.0.0.1";
    private final int PORT = 8888;
    private final String QUIT = "quit";
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public void start() {
        //socket
        try {
            socket = new Socket(IP, PORT);
            System.out.println("连接成功!");
            //获取输入输出流
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            //写入消息
            new Thread(new UserInputHandler(this)).start();

            //读取消息
            String msg = null;
            while ((msg = receive()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    //发送消息到server
    public void send(String msg) throws IOException {
        if (!socket.isOutputShutdown()) {
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    //接受server消息
    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    //判断是否准备退出
    public boolean readToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
