import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket client;
    private String clientAdress;

    public ClientHandler(Socket client) {
        this.client = client;
        clientAdress = client.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter output = new PrintWriter(client.getOutputStream(), true);) {

            String line = input.readLine();

            while ((line = input.readLine()) != null) {
                System.out.println("Recebido: " + line);
                if (line.isEmpty())
                    break;
            }

            output.println("Servidor recebeu a conexão!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
