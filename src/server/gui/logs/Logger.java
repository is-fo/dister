package server.gui.logs;

public interface Logger {
    void printLogs(String message);
    void printErrors(String message);
}
