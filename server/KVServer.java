package server;

import store.KeyValueStore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KVServer {

    private final int port;
    private final KeyValueStore store;
    private final ExecutorService threadPool;

    public KVServer(int port) {
        this.port = port;
        this.store = new KeyValueStore();
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

            // ✅ THIS is the key change
            threadPool.submit(new ClientHandler(clientSocket, store));
        }
    }
}