package store;

public class ExpiryEntry implements Comparable<ExpiryEntry> {
    public String key;
    public long expiryTime;

    public ExpiryEntry(String key, long expiryTime) {
        this.key = key;
        this.expiryTime = expiryTime;
    }

    @Override
    public int compareTo(ExpiryEntry other) {
        return Long.compare(this.expiryTime, other.expiryTime);
    }
}