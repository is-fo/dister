// Manages individual client connections on the server

package server;

import model.Message;
import server.gui.ServerGUI;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final ChatServer server;

    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

            try {
             in = new ObjectInputStream(clientSocket.getInputStream());
             out = new ObjectOutputStream(clientSocket.getOutputStream());

                BlockingQueue<Message> queue = server.getMessageQueue(this);
                new Thread(() -> {
                    try {
                        while (true) {
                            Object msg = queue.take();
                            out.writeObject(msg);
                        }
                    } catch (InterruptedException e) {
                        System.err.println("Message sender interrupted for client: " + clientSocket);
                    } catch (IOException e) {
                        System.err.println("Message sender IOException for client: " + clientSocket);
                    }
                }).start();

                try {
                    Object message;
                    while ((message = in.readObject()) != null) {
                        if (message instanceof Message msg) {
                            server.publishMessage(msg);
                            System.out.println(new Message(msg.getSender(), msg.getMessage()));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Message class not found for client: " + clientSocket);
                }

        } catch (SocketException e) {
            System.err.println("Client didn't disconnect properly.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception when creating I/O connection.");
        } finally {
            try {
                in.close();
                out.close();
                if (clientSocket != null) {
                    clientSocket.close();
                }
                server.removeClient(this);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error when closing streams or socket.");
            }
        }
    }
}
