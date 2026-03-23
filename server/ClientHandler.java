package server;

import store.KeyValueStore;
import protocol.CommandParser;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final KeyValueStore store;

    public ClientHandler(Socket socket, KeyValueStore store) {
        this.socket = socket;
        this.store = store;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true)
        ) {
            String line;

            while ((line = in.readLine()) != null) {
                String[] tokens = CommandParser.parse(line);
                if (tokens == null) {
                    continue;
                }

                String command = tokens[0].toUpperCase();

                switch (command) {
                    case "SET":
                        if (tokens.length < 3) {
                            out.println("ERROR");
                        } else {
                            String key = tokens[1];

                            Long ttl = null;
                            int valueEndIndex = tokens.length;

                            // Check if EX exists
                            if (tokens.length >= 5 && tokens[tokens.length - 2].equalsIgnoreCase("EX")) {
                                try {
                                    ttl = Long.parseLong(tokens[tokens.length - 1]);
                                    valueEndIndex = tokens.length - 2;
                                } catch (NumberFormatException e) {
                                    out.println("ERROR");
                                    break;
                                }
                            }

                            // Build value
                            StringBuilder valueBuilder = new StringBuilder();
                            for (int i = 2; i < valueEndIndex; i++) {
                                valueBuilder.append(tokens[i]);
                                if (i != valueEndIndex - 1) {
                                    valueBuilder.append(" ");
                                }
                            }

                            String value = valueBuilder.toString();
                            out.println(store.set(key, value, ttl));
                        }
                        break;

                    case "GET":
                        if (tokens.length != 2) {
                            out.println("ERROR");
                        } else {
                            out.println(store.get(tokens[1]));
                        }
                        break;

                    case "DEL":
                        if (tokens.length != 2) {
                            out.println("ERROR");
                        } else {
                            out.println(store.del(tokens[1]));
                        }
                        break;

                    default:
                        out.println("UNKNOWN_COMMAND");
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        }
    }
}