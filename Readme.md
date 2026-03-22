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

## Limitations (Current)
- Values are not whitespace-preserving (multiple spaces collapse)
- No persistence (data is lost on restart)
- No expiry (TTL not implemented yet)
- Simple string-based protocol (not binary-safe)

## Next Steps
- Add TTL (key expiration)
- Implement LRU eviction
- Add persistence to disk
- Improve protocol (length-based / RESP-like)