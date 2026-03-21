import server.KVServer;

public class Main {
    public static void main(String[] args) throws Exception {
        KVServer server = new KVServer(6379);
        server.start();
    }
}