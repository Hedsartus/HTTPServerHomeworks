package server;

import Handler.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int POOL_SIZE = 64;
    private ExecutorService threadPool;
    private int serverPort = 5050;
    private ServerSocket serverSocket = null;
    private final ConcurrentHashMap<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen(int port) {
        this.serverPort = port;
        this.threadPool = Executors.newFixedThreadPool(POOL_SIZE);
        startServer();
    }

    public ConcurrentHashMap<String, Map<String, Handler>> getHandlers() {
        return this.handlers;
    }

    public void startServer() {
        System.out.println("Старт сервера...");
        openServerSocket();

        while (!this.serverSocket.isClosed()) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                addConnection(new ClientHandler(clientSocket, this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.threadPool.shutdown();
        System.out.println("Остановка сервера!");
    }

    private void addConnection(ClientHandler clientHandler) {
        this.threadPool.execute(clientHandler);
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new HashMap<>());
        }
        handlers.get(method).put(path, handler);
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
    }
}
