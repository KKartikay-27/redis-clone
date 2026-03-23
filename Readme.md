# Redis Clone (Java)

A minimal in-memory key-value store built from scratch with TCP networking and concurrency.

## Features

### Phase 1
- Single-threaded TCP server
- Basic commands: SET, GET, DEL
- In-memory storage using HashMap

### Phase 2
- Concurrent client handling using thread pool (ExecutorService)
- Thread-safe storage using ConcurrentHashMap
- Support for multi-word values in SET
- Graceful handling of empty input
- Fixed switch-case fall-through bug

### Phase 3
- TTL (Time-To-Live) support using `SET key value EX <seconds>`
- Lazy expiration: keys expire on access
- Expiry tracking using timestamp-based approach
- Correct handling of TTL overwrite

---

## How It Works

- Server listens on port `6379`
- Each client connection is handled by a thread from the pool
- Commands are parsed as whitespace-separated tokens
- Values in `SET` can contain spaces (joined internally)

---

## Run

```bash
javac Main.java server/*.java store/*.java protocol/*.java
java Main
```

## Usage

Connect using telnet:

```bash
telnet localhost 6379
```

Example:

```bash
SET name kartikay
GET name
DEL name
```

Multi-word values:

```bash
SET bio I am building a redis clone
GET bio
```

TTL support:
```bash
SET session user123 EX 5
GET session
```

## Limitations (Current)
- Values are not whitespace-preserving (multiple spaces collapse)
- No persistence (data is lost on restart)
- TTL is implemented, but expiration occurs after a delay (lazy expiration).
- Simple string-based protocol (not binary-safe)

## Next Steps
- Add optimized TTL (key expiration)
- Implement LRU eviction
- Add persistence to disk
- Improve protocol (length-based / RESP-like)