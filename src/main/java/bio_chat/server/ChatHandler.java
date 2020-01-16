package bio_chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatHandler implements Runnable {
    private ChatServer server;
    private Socket socket;

    public ChatHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void start() throws IOException {
        //获取输入流
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        while (true) {
            String msg = reader.readLine();
            if(msg == null) break;
            System.out.println("收到一掉消息来自" + socket.getPort());
            server.forWard(socket, msg);
            System.out.println("已送达全部客户端");
            if (server.QUIT.equals(msg)) {
                server.removeClient(socket);
            }
        }
        reader.close();

    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
