// Main server class, listens for clients and broadcasts messages

package server;

import model.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatServer {

    private Map<ClientHandler, BlockingQueue<Message>> clientQueues = new ConcurrentHashMap<>();

    public void addClient(ClientHandler client) {
        clientQueues.put(client, new LinkedBlockingQueue<>());
    }

    public void removeClient(ClientHandler client) {
        clientQueues.remove(client);
    }

    public void publishMessage(Message message) {
        for (BlockingQueue<Message> queue : clientQueues.values()) {
            queue.offer(message);
        }
    }

    public BlockingQueue<Message> getMessageQueue(ClientHandler clientHandler) {
        return clientQueues.get(clientHandler);
    }

    private static final int PORT = 12345;

    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket, server);

                server.addClient(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}