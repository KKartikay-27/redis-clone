package store;

import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    private final ConcurrentHashMap<String, String> store;
    private final ConcurrentHashMap<String, Long> expiryMap;

    public KeyValueStore() {
        this.store = new ConcurrentHashMap<>();
        this.expiryMap = new ConcurrentHashMap<>();
    }

    public String set(String key, String value, Long ttlSeconds) {
        store.put(key, value);

        if (ttlSeconds != null) {
            if (ttlSeconds != null && ttlSeconds <= 0) {
                store.remove(key);
                expiryMap.remove(key);
                return "OK";
            }
            long expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
            expiryMap.put(key, expiryTime);
        } else {
            expiryMap.remove(key); // remove old TTL if exists
        }

        return "OK";
    }

    public String get(String key) {
        // Check if key exists
        if (!store.containsKey(key)) {
            return "NULL";
        }

        // Check expiry
        Long expiryTime = expiryMap.get(key);
        if (expiryTime != null && System.currentTimeMillis() > expiryTime) {
            // expired → delete
            store.remove(key);
            expiryMap.remove(key);
            return "NULL";
        }

        return store.get(key);
    }

    public String del(String key) {
        boolean existed = store.remove(key) != null;
        expiryMap.remove(key); // keep consistent
        return existed ? "1" : "0";
    }
}