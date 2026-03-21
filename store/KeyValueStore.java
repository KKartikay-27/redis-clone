package store;

import java.util.HashMap;
import java.util.Map;

public class KeyValueStore {

    private final Map<String, String> store;

    public KeyValueStore() {
        this.store = new HashMap<>();
    }

    public synchronized String set(String key, String value) {
        store.put(key, value);
        return "OK";
    }

    public synchronized String get(String key) {
        return store.getOrDefault(key, "NULL");
    }

    public synchronized String del(String key) {
        return store.remove(key) != null ? "1" : "0";
    }
}