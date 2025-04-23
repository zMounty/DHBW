import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        String serverIP = "127.0.0.1"; // Oder echte IP im Netzwerk
        int port = 12345;

        try (Socket socket = new Socket(serverIP, port)) {
            System.out.println("Mit dem Chat verbunden.");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Thread für eingehende Nachrichten
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = input.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Verbindung zum Server verloren.");
                }
            }).start();

            // Hauptthread für Eingabe
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                output.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
