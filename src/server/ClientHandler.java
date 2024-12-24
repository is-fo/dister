// Manages individual client connections on the server

package server;

import model.Message;
import server.gui.logs.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final ChatServer server;
    private final Logger logger;

    private volatile boolean running = true;

    public ClientHandler(Socket clientSocket, ChatServer server, Logger logger) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.logger = logger;
    }

    public void close() {
        running = false;
        server.removeClient(this);
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
            logger.printErrors("Error disconnecting client: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            new Thread(() -> {
                try {
                    while (running) {
                        Object msg = server.getMessageQueue(this).take();
                        out.writeObject(msg);
                    }
                } catch (InterruptedException e) {
                    logger.printErrors("Message sender interrupted for client: " + clientSocket);
                } catch (IOException e) {
                    logger.printErrors("Message sender IOException for client: " + clientSocket);
                } finally {
                    close();
                }
            }).start();

            new Thread(() -> {
                try {
                    Object message;
                    while ((message = in.readObject()) != null) {
                        if (message instanceof Message msg) {
                            server.publishMessage(msg);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    logger.printErrors("Message class not found for client: " + clientSocket);
                } catch (IOException e) {
                    logger.printErrors("Error receiving message from client: " + clientSocket + ": " + e.getMessage());
                } finally {
                    close();
                }
            }).start();

        } catch (SocketException e) {
            logger.printErrors("SocketException for client: " + clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
            logger.printErrors("Exception when creating I/O connection.");
        }
    }
}
