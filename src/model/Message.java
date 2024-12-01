// Message class to represent chat messages

package model;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {

    private final User sender;
    private final String message;
    private final LocalTime timestamp;
    private transient final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public Message(User sender, String message) {
        this.sender = sender != null ? sender : new User("NULLUSERSPECIAL");
        this.message = message;
        this.timestamp = LocalTime.now();
    }

    @Override
    public String toString() {
        return "[" + this.getFormattedTimestamp() + "] " + sender + ": " + message;
    }

    public User getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(formatter);
    }

    public static void main(String[] args) {
        System.out.println(new Message(null, "hi").getTimestamp());
        System.out.println(new Message(null, "hi").getFormattedTimestamp());
        System.out.println(new Message(null, "hi"));
    }
}
