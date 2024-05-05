package net.momirealms.sparrow.common.feature.skull;

public class FailedToGetSkullException extends RuntimeException {
    public FailedToGetSkullException(String playerName) {
        super("Failed to get skull of the given player name " + playerName + "!");
    }

    public FailedToGetSkullException(String playerName, Throwable cause) {
        super("Failed to get skull of the given player name " + playerName + "!", cause);
    }
}
