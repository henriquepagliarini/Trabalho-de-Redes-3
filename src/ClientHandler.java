import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;

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
                OutputStream output = client.getOutputStream()) {

            String line = input.readLine();
            System.out.println("Requisição: " + line);

            if (line == null || !line.startsWith("GET")) {
                client.close();
                return;
            }

            String[] chunks = line.split(" ");
            String path = chunks[1].equals("/") ? "index.html" : chunks[1];
            System.out.println(path);
            String filePath = "files" + path;
            System.out.println(filePath);

            File file = new File(filePath);
            if (file.exists()) {
                sendHttpResponse(output, file, 200, "OK", "text/html");
            } else {
                File error404 = new File("files/notFound.html");
                sendHttpResponse(output, error404, 404, "Not Found", "text/html");
            }

            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendHttpResponse(OutputStream output, File file, int code, String status, String contentType)
            throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());

        PrintWriter writer = new PrintWriter(output, false);
        writer.println("HTTP/1.1 " + code + " " + status);
        writer.println("Server: ");
        writer.println("Date: ");
        writer.println("Content-Type: " + contentType);
        writer.println("Last-Modified: ");
        writer.println("Content-Length: " + bytes.length);
        writer.println();
        writer.flush();

        output.write(bytes);
        output.flush();
    }
}
