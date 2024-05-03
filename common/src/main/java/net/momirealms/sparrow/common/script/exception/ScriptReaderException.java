package net.momirealms.sparrow.common.script.exception;

public class ScriptReaderException extends RuntimeException {

    public ScriptReaderException(String message) {
        super(message);
    }

    public ScriptReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
