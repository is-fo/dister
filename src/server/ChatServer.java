// Main server class, listens for clients and broadcasts messages

package server;

import model.Message;
import server.gui.ServerGUI;
import server.gui.logs.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatServer {

    private final Logger logger;

    private static final int PORT = 12345;

    private Map<ClientHandler, BlockingQueue<Message>> clientQueues = new ConcurrentHashMap<>();

    public void addClient(ClientHandler client) {
        clientQueues.put(client, new LinkedBlockingQueue<>());
    }

    public void removeClient(ClientHandler client) {
        clientQueues.remove(client);
    }

    public void publishMessage(Message message) {

        logger.printLogs(message.getSender().getUsername() + " " + message.getMessage());

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
        logger.printLogs("Disconnecting clients...");

        for (ClientHandler client : clientQueues.keySet()) {
            client.close();
            removeClient(client);
        }
        logger.printLogs("Shutting down server... (TODO)");
        System.exit(Integer.MAX_VALUE);
    }

    private void start() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.printLogs("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.printLogs("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket, this, logger);

                this.addClient(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            logger.printErrors("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ChatServer() {
        logger = ServerGUI.getInstance().getServerlogPrinter();
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
