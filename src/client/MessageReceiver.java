// Background thread to receive messages from server

package client;

import model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageReceiver implements Runnable {

    private final ObjectInputStream in;

    public MessageReceiver(ObjectInputStream in) {
        this.in = in;
        if (in == null) {
            throw new RuntimeException("in is null");
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Message receiver started for client.");
            Object message;
            while ((message = in.readObject()) != null) {
                if (message instanceof Message m) {
                    System.out.println(new Message(m.getSender(), m.getMessage()));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading from server." + e.getMessage());
            System.exit(1);
        }
    }
}
