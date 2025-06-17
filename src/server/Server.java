package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private static final Integer port = 6666;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado na porta " + port + ". Aguardando conexões...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
