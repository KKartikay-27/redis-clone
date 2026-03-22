package store;

import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    private final ConcurrentHashMap<String, String> store;

    public KeyValueStore() {
        this.store = new ConcurrentHashMap<>();
    }

    public String set(String key, String value) {
        store.put(key, value);
        return "OK";
    }

    public String get(String key) {
        return store.getOrDefault(key, "NULL");
    }

    public String del(String key) {
        return store.remove(key) != null ? "1" : "0";
    }
}