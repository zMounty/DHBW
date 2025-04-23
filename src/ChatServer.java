import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Chat-Server läuft...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Neuer Client verbunden: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            output.println("Willkommen im Chat! Schreib 'exit' zum Verlassen.");

            String message;
            while ((message = input.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) break;
                System.out.println("Client sagt: " + message);
                ChatServer.broadcast("Client: " + message, this);
            }
        } catch (IOException e) {
            System.out.println("Verbindung verloren: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}
            ChatServer.removeClient(this);
        }
    }

    void sendMessage(String message) {
        output.println(message);
    }
}
