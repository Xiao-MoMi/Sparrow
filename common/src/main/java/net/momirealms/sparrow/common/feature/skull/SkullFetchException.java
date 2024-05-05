package net.momirealms.sparrow.common.feature.skull;

public class SkullFetchException extends RuntimeException {

    private final Reason reason;

    public SkullFetchException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public SkullFetchException(Reason reason, String message, Throwable t) {
        super(message, t);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    public enum Reason {

        THROTTLED,
        INVALID,
        INTERNAL,
        UNKNOWN,
    }
}
