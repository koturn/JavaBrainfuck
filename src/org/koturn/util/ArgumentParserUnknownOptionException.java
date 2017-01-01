package org.koturn.util;

/**
 * An exception throwed from {@link ArgumentParser#parse(String[])}.
 * This exception is throwed when detected unknown option.
 */
public class ArgumentParserUnknownOptionException extends ArgumentParserException {
    /**
     * Null message exception
     */
    public ArgumentParserUnknownOptionException() {
        super();
    }
    /**
     * Create message for exception for parsing short option
     * @param shortName  Short option name
     */
    public ArgumentParserUnknownOptionException(char shortName) {
        super("Unknown short option", shortName);
    }
    /**
     * Create message for exception for parsing long option
     * @param longName  Long option name
     */
    public ArgumentParserUnknownOptionException(String longName) {
        super("Unknown long option", longName);
    }
}
