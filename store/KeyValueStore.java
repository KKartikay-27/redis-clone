package store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.PriorityQueue;

public class KeyValueStore {

    private final ConcurrentHashMap<String, String> store;
    private final ConcurrentHashMap<String, Long> expiryMap;
    private final PriorityQueue<ExpiryEntry> pq;

    public KeyValueStore() {
        this.store = new ConcurrentHashMap<>();
        this.expiryMap = new ConcurrentHashMap<>();
        this.pq = new PriorityQueue<>();
    }

    public String set(String key, String value, Long ttlSeconds) {
        store.put(key, value);

        if (ttlSeconds != null) {
            if (ttlSeconds <= 0) {
                store.remove(key);
                expiryMap.remove(key);
                return "OK";
            }

            long expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
            expiryMap.put(key, expiryTime);

            synchronized (pq) {
                pq.offer(new ExpiryEntry(key, expiryTime));
            }
        } else {
            expiryMap.remove(key);
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

    public void cleanupExpiredKeys() {
        long now = System.currentTimeMillis();

        synchronized (pq) {
            while (!pq.isEmpty()) {
                ExpiryEntry entry = pq.peek();

                if (entry.expiryTime > now) {
                    break;
                }

                pq.poll();

                Long currentExpiry = expiryMap.get(entry.key);

                if (currentExpiry == null) {
                    continue;
                }

                if (currentExpiry != entry.expiryTime) {
                    continue; // stale entry
                }

                // valid expired key
                store.remove(entry.key);
                expiryMap.remove(entry.key);
            }
        }
    }
}