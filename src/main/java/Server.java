import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final int DEFAULT_PORT = 8888;
    private static ServerSocket serverSocket;
    private static final String QUIT_MARK = "quit";


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

                String msg = null;
                //msg什么时候可能为null呢?
                //服务端之后外面的while的时候 客户端也没有while的时候 客户端不发消息直接断开报异常,
                //发完消息再关闭,不报. 服务端之后外面的while的时候 客户段可以重复发消息的时候,客户端第一次发消息
                //收到的回复就是null
                //服务端有了里面的while之后,直接关闭服务端,报异常
                while((msg = bufferedReader.readLine()) != null){

                    System.out.println("读取" + socket.getPort()+":" + msg);
                    //回复客户端的消息
                    bufferedWriter.write("回复:" + socket.getPort() + msg+"\n");
                    System.out.println("log回复:" + socket.getPort());
                    bufferedWriter.flush();
                    if(QUIT_MARK.equals(msg)){
                        System.out.println(socket.getPort()+"退出");
                        break;
                    }

                }
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
