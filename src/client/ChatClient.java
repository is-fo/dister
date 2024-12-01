// Main client class, connects to the server

package client;

import model.Message;
import model.User;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

public class ChatClient {

    private Socket socket;
    private ObjectOutputStream objectOut;
    private final User user;

    public ChatClient(String serverAddress, int serverPort, String username) {
        this.user = new User(username);

        boolean connected = false;
        while (!connected) {
            try {
                socket = new Socket(serverAddress, serverPort);
                System.out.println("Connected to the chat server as " + username);

                objectOut = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

                MessageReceiver receiver = new MessageReceiver(objectIn);
                new Thread(receiver).start();
                connected = true;
            } catch (Exception e) {
                System.err.println("Error connecting to the server: " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.err.println("Sleep interrupted: " + ex.getMessage());
                    System.exit(-1);
                }

            }
        }
    }

    public void sendMessage(String message) {
        if (objectOut != null) {
            try {
                objectOut.writeObject(new Message(user, message));
                objectOut.flush();
            } catch (IOException e) {
                System.err.println("Error writing to the server: " + e.getMessage());
            }
        }
    }

    public void start() {

        try (Scanner scanner = new Scanner(System.in)) {
            //noinspection InfiniteLoopStatement
            while (true) {
                String message = scanner.nextLine();
                sendMessage(message);
            }
        } finally {
            close();
        }
    }

    private void close() {
        try {
            if (objectOut != null) objectOut.close();
            if (socket != null) socket.close();
            System.out.println(user + " disconnected from server.");
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12345;
        String username = "User" + LocalTime.now().getSecond();

        ChatClient client = new ChatClient(serverAddress, serverPort, username);
        client.start();
    }
}
