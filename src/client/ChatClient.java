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
    private User user;

    public ChatClient(String serverAddress, int serverPort, String username) {
        this.user = new User(username);

        try {
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the chat server as " + username);

            objectOut = new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

            MessageReceiver receiver = new MessageReceiver(objectIn);
            new Thread(receiver).start();

        } catch (Exception e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
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
            System.out.println("Type a message and press Enter to send.");
            while (true) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("quit")) {
                    System.out.println("Exiting chat...");
                    break;
                }
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
            System.out.println("Disconnected from chat server.");
        } catch (Exception e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12345;
        String username = "User" + LocalTime.now().getSecond(); // Can be prompted or dynamically set

        ChatClient client = new ChatClient(serverAddress, serverPort, username);
        client.start();
    }
}