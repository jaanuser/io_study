import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final int DEFAULT_PORT = 8888;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("serverSocket建立");
            //这个循环是干嘛的,是说一个连接处理完之后,接着处理下一个连接?
            while (true) {
                System.out.println("等待连接");
                Socket socket = serverSocket.accept();
                System.out.println("已连接:" + socket.getPort());
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                //读取客户端的消息
                System.out.println("等待读取客户端消息");
                String msg = bufferedReader.readLine();
                System.out.println("读取" + socket.getPort() + msg);
                //回复客户端的消息
                bufferedWriter.write("回复:" + socket.getPort() + msg);
                System.out.println("log回复:" + socket);
                bufferedWriter.flush();
                bufferedReader.close();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("close");
        } finally {
            try {
                serverSocket.close();
                System.out.println("close异常");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
