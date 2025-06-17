package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static String host;
    private static Integer port;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("IP do servidor (ex: localhost): ");
        String host = scanner.nextLine();
        System.out.print("Porta do servidor (ex: 6666): ");
        int port = scanner.nextInt();
        scanner.nextLine();

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Client.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
