import org.omg.CORBA.INTERNAL;

import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket socket;
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final int DEFAULT_PORT = 8888;

    public static void main(String[] args) {
        try {
            //建立socket
            socket = new Socket(DEFAULT_IP, DEFAULT_PORT);
            System.out.println("客户端与建立连接" + socket.getPort());
            //获得输入输出流
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            //本地输入
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            while (true) {
                System.out.println("请输入向服务端发送的内容\n");

                String msg = reader.readLine();
                //向服务端写入
                //一开始没写\n,那边readline 读不到
                bufferedWriter.write(msg + "\n");
                bufferedWriter.flush();
                if ("quit".equals(msg)) {
                    break;
                }else {
                    System.out.println("向客户端发送" + msg);
                    //读取服务端发来信息
                    String read = bufferedReader.readLine();
                    System.out.println(read);
                }
            }

            reader.close();
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("close");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("close异常");
                e.printStackTrace();
            }
        }
    }

}
