# In-Memory Key-Value Store (Java)

A minimal in-memory key-value store built from scratch with TCP networking and concurrency.

## Features

### Phase 1
- Single-threaded TCP key-value store
- Commands: SET, GET, DEL
- Simple string-based protocol

### Phase 2
- Multi-client support using thread pool
- Thread-safe storage with ConcurrentHashMap
- Multi-word value handling
- Fixed command parsing edge cases

### Phase 3
- TTL support using `SET key value EX <seconds>`
- Lazy expiration (checked during GET)
- Timestamp-based expiry tracking

### Phase 4
- Efficient expiration using Priority Queue (min-heap)
- Background cleanup thread for expired keys
- Avoids full scan (O(N)) using time-ordered eviction
- Handles stale entries safely via expiryMap validation
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

## Design Decisions

### Expiration Strategy

The system uses a hybrid expiration model:

- **expiryMap (source of truth)**  
  Stores the latest expiration timestamp for each key.

- **Priority Queue (min-heap)**  
  Tracks keys ordered by expiration time for efficient cleanup.

### Why not remove old entries from PQ?

Removing arbitrary elements from a PriorityQueue is O(N).  
Instead, stale entries are allowed and safely ignored during cleanup by validating against expiryMap.

### Cleanup Strategy

- Background thread periodically checks the earliest expiring keys.
- Expired keys are removed only if the PQ entry matches the current expiryMap value.
- Ensures correctness while maintaining efficient writes.

### Tradeoffs

- Allows stale entries in PQ → slightly higher memory usage
- Avoids expensive removal operations → faster writes
- Cleanup cost proportional to expired keys, not total keys

## Limitations

- Uses a simple string-based protocol (not binary-safe)
- No persistence (in-memory only)
- PriorityQueue may contain stale entries (handled during cleanup)
- No eviction policy when memory is full