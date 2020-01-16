package bio_chat.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private ServerSocket serverSocket;
    //端口常量
    public final int PORT = 8888;
    public final String QUIT = "quit";
    private Map<Integer, Writer> clientMap;

    public ChatServer() {
        this.clientMap = new HashMap<>();
    }

    //开启服务
    public void star() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("server开启成功,等待连接");
        //轮询一直接受新的连接
        while (true) {

            Socket socket = serverSocket.accept();
            System.out.println(socket.getPort() + "已连接");

            addClient(socket);
            System.out.println(socket.getPort() + "已记录");

            //开启一个ServerHandler线程服务该socket
            ChatHandler chatHandler = new ChatHandler(this, socket);
            Thread thread = new Thread(chatHandler,"Thread:"+socket.getPort());
            thread.start();
        }

    }

    public void forWard(Socket socket, String msg) throws IOException {
        for (int port : clientMap.keySet()) {
            if (port == socket.getPort()) continue;
            Writer writer = clientMap.get(port);
            writer.write(port + "用户:" + msg + "\n");
            writer.flush();
        }
    }

    //记录client
    private void addClient(Socket socket) {
        //获取client的write
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //将client添加到map
        clientMap.put(socket.getPort(), writer);


    }

    //移除client
    public void removeClient(Socket socket) {
        clientMap.remove(socket.getPort());
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        try {
            chatServer.star();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                chatServer.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
