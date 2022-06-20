package ir.pi.project.server.controller;


import ir.pi.project.server.config.SocketConfig;
import ir.pi.project.server.controller.netwrok.SocketResponseSender;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager extends Thread{
    private final SocketConfig socketConfig=new SocketConfig();

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(socketConfig.getPort());
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(new SocketResponseSender(socket));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
