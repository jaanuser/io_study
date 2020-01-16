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

    @Override
    public void run() {
        try {
            //记录上线用户
            server.addClient(socket);

            //读取用户发送的信息
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                System.out.println("客户端:[" + socket.getPort() + "]" + msg);
                //转发消息给所有客户端,除发送该消息的客户端
                String fwmsg = "客户端:[" + socket.getPort() + "]" + msg + "\n";
                server.forWard(socket, fwmsg);

                //检查用户是否准备退出
                if (server.readToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
