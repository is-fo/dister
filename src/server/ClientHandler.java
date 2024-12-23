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

    private ServerGUI serverGUI;

    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        serverGUI = ServerGUI.getInstance();
    }

    public void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            serverGUI.getServerlogPrinter().printErrors("Error disconnecting client: " + e.getMessage());
        }
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
                        serverGUI.getServerlogPrinter().printErrors("Message sender interrupted for client: " + clientSocket);
                    } catch (IOException e) {
                        serverGUI.getServerlogPrinter().printErrors("Message sender IOException for client: " + clientSocket);
                    }
                }).start();

                try {
                    Object message;
                    while ((message = in.readObject()) != null) {
                        if (message instanceof Message msg) {
                            server.publishMessage(msg);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    serverGUI.getServerlogPrinter().printErrors("Message class not found for client: " + clientSocket);
                }

        } catch (SocketException e) {
                serverGUI.getServerlogPrinter().printErrors("SocketException for client: " + clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
            serverGUI.getServerlogPrinter().printErrors("Exception when creating I/O connection.");
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
                serverGUI.getServerlogPrinter().printErrors("Error when closing streams or socket.");
            }
        }
    }
}
