
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Integer port = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado na porta " + port + ". Aguardando conexões...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: (" + clientSocket.getInetAddress().getHostAddress() + ")");

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
