package com.iemr.flw.service.sync;

/**
 * The tablet's opaque sync position. Two kinds:
 *
 *   d.&lt;serverId&gt;.&lt;seq&gt;.&lt;issuedEpochSeconds&gt;.&lt;typesMask&gt;
 *       changes after change-log line seq on this hub;
 *   s.&lt;serverId&gt;.&lt;snapshotSeq&gt;.&lt;typeIndex&gt;.&lt;afterKey&gt;.&lt;typesMask&gt;
 *       part-way through a first download: the next page starts at type typeIndex, after afterKey,
 *       and when it finishes the tablet continues with changes after snapshotSeq.
 *
 * serverId is the hub's MySQL server_uuid, so a token from another hub is never trusted. The issue
 * time lets the hub tell when the log lines a token points after may have been cleaned up.
 * typesMask is the set of record types the token was issued for (bit = StopTBSyncType ordinal): a
 * token only covers those types, so a request for a different set starts a new snapshot.
 *
 * @author Piramal Swasthya
 */
public final class SyncToken {

    public enum Kind { DELTA, SNAPSHOT }

    private final Kind kind;
    private final String serverId;
    private final long seq;
    private final long issuedAt;
    private final int typeIndex;
    private final long afterKey;
    private final long typesMask;

    private SyncToken(Kind kind, String serverId, long seq, long issuedAt, int typeIndex, long afterKey,
                      long typesMask) {
        this.kind = kind;
        this.serverId = serverId;
        this.seq = seq;
        this.issuedAt = issuedAt;
        this.typeIndex = typeIndex;
        this.afterKey = afterKey;
        this.typesMask = typesMask;
    }

    public static SyncToken delta(String serverId, long seq, long issuedAt, long typesMask) {
        return new SyncToken(Kind.DELTA, serverId, seq, issuedAt, 0, 0, typesMask);
    }

    public static SyncToken snapshot(String serverId, long snapshotSeq, int typeIndex, long afterKey, long typesMask) {
        return new SyncToken(Kind.SNAPSHOT, serverId, snapshotSeq, 0, typeIndex, afterKey, typesMask);
    }

    /** null for a missing or unreadable token; the caller then starts a snapshot. */
    public static SyncToken parse(String token) {
        if (token == null || token.isBlank()) return null;
        String[] p = token.trim().split("\\.");
        try {
            if (p.length == 5 && "d".equals(p[0]) && !p[1].isEmpty()) {
                return delta(p[1], Long.parseLong(p[2]), Long.parseLong(p[3]), Long.parseLong(p[4]));
            }
            if (p.length == 6 && "s".equals(p[0]) && !p[1].isEmpty()) {
                return snapshot(p[1], Long.parseLong(p[2]), Integer.parseInt(p[3]), Long.parseLong(p[4]),
                        Long.parseLong(p[5]));
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public String encode() {
        return kind == Kind.DELTA
                ? "d." + serverId + "." + seq + "." + issuedAt + "." + typesMask
                : "s." + serverId + "." + seq + "." + typeIndex + "." + afterKey + "." + typesMask;
    }

    public Kind kind() {
        return kind;
    }

    public String serverId() {
        return serverId;
    }

    /** DELTA: last change-log line applied. SNAPSHOT: the line changes continue from afterwards. */
    public long seq() {
        return seq;
    }

    public long issuedAt() {
        return issuedAt;
    }

    public int typeIndex() {
        return typeIndex;
    }

    public long afterKey() {
        return afterKey;
    }

    public long typesMask() {
        return typesMask;
    }

    @Override
    public String toString() {
        return encode();
    }
}
