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
                    out.println("ERROR");
                    continue;
                }

                String command = tokens[0].toUpperCase();

                switch (command) {
                    case "SET":
                        if (tokens.length != 3) {
                            out.println("ERROR");
                        } else {
                            out.println(store.set(tokens[1], tokens[2]));
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
            e.printStackTrace();
        }
    }
}