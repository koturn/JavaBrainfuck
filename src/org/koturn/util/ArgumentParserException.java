package org.koturn.util;

/**
 * An exception throwed from {@link ArgumentParser#parse(String[])}
 */
public class ArgumentParserException extends Exception {
    /**
     * Null message exception
     */
    public ArgumentParserException() {
        super();
    }
    /**
     * Use specified message for exception message
     * @param msg  Exception message
     */
    public ArgumentParserException(String msg) {
        super(msg);
    }
    /**
     * Create message for exception for parsing short option
     * @param msg        Base message
     * @param shortName  Short option name
     */
    public ArgumentParserException(String msg, char shortName) {
        super(msg + ": -" + shortName);
    }
    /**
     * Create message for exception for parsing long option
     * @param msg       Base message
     * @param longName  Long option name
     */
    public ArgumentParserException(String msg, String longName) {
        super(msg + ": --" + longName);
    }
}
