// Main server class, listens for clients and broadcasts messages

package server;

import model.Message;
import server.gui.ServerGUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatServer {

    private ServerGUI serverGUI;

    private static final int PORT = 12345;

    private Map<ClientHandler, BlockingQueue<Message>> clientQueues = new ConcurrentHashMap<>();

    public void addClient(ClientHandler client) {
        clientQueues.put(client, new LinkedBlockingQueue<>());
    }

    public void removeClient(ClientHandler client) {
        clientQueues.remove(client);
    }

    public void publishMessage(Message message) {

        if (serverGUI != null) {
            serverGUI.getServerlogPrinter().printLogs(message.getSender().getUsername() + " " + message.getMessage());
        } else {
            serverGUI = ServerGUI.getInstance();
            serverGUI.getServerlogPrinter().printLogs(message.getSender().getUsername() + " " + message.getMessage());
        }

        for (BlockingQueue<Message> queue : clientQueues.values()) {
            queue.offer(message);
        }
    }

    public BlockingQueue<Message> getMessageQueue(ClientHandler clientHandler) {
        return clientQueues.get(clientHandler);
    }

    public void restart() {
        close();
        start();
    }

    public void close() {
        //TODO exit logik
        serverGUI.getServerlogPrinter().printLogs("Disconnecting clients...");

        for (ClientHandler client : clientQueues.keySet()) {
            client.close();
            removeClient(client);
        }
        serverGUI.getServerlogPrinter().printLogs("Shutting down server... (TODO)");
        System.exit(Integer.MAX_VALUE);
    }

    private void start() {
        ChatServer server = new ChatServer();
        serverGUI = ServerGUI.getInstance();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverGUI.getServerlogPrinter().printLogs("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                serverGUI.getServerlogPrinter().printLogs("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket, server);

                server.addClient(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            serverGUI.getServerlogPrinter().printErrors("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
