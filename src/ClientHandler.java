import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    private Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream output = client.getOutputStream()) {

            String line = input.readLine();
            System.out.println("Requisicao: " + line);

            if (line == null || line.isEmpty())
                return;

            String[] chunks = line.split(" ");
            if (chunks.length < 2 || !chunks[0].equals("GET"))
                return;

            String path = chunks[1].equals("/") ? "index.html" : chunks[1];

            if (path.contains("..") || path.contains("//")) {
                File error404 = new File("files/notFound.html");
                sendHttpResponse(output, error404, 404, "Not Found");
                return;
            }

            String filePath = "files" + path;

            File file = new File(filePath);
            if (file.exists()) {
                sendHttpResponse(output, file, 200, "OK");
            } else {
                File error404 = new File("files/notFound.html");
                sendHttpResponse(output, error404, 404, "Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendHttpResponse(OutputStream output, File file, int code, String status) throws IOException {
        String mimeType = mimeTypeDetector(file.getName());

        byte[] bytes = Files.readAllBytes(file.toPath());

        ZonedDateTime moment = ZonedDateTime.now(ZoneId.of("GMT"));
        String dateHTTP = DateTimeFormatter.RFC_1123_DATE_TIME.format(moment);

        FileTime lastFileModification = Files.getLastModifiedTime(file.toPath());
        ZonedDateTime lastModificationTime = lastFileModification.toInstant().atZone(ZoneId.of("GMT"));
        String lastModification = DateTimeFormatter.RFC_1123_DATE_TIME.format(lastModificationTime);

        PrintWriter writer = new PrintWriter(output, false);
        writer.println("HTTP/1.1 " + code + " " + status);
        writer.println("Server: ServidorJavaHenrique/1.0");
        writer.println("Date: " + dateHTTP);
        writer.println("Content-Type: " + mimeType);
        writer.println("Last-Modified: " + lastModification);
        writer.println("Content-Length: " + bytes.length);
        writer.println();
        writer.flush();

        output.write(bytes);
        output.flush();
    }

    private String mimeTypeDetector(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream";
        }
    }
}
