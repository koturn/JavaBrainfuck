package org.koturn.util;

/**
 * An exception throwed from {@link ArgumentParser#parse(String[])}.
 * This exception is throwed when an argument required option cannot find an argument.
 */
public class ArgumentParserMissingArgumentException extends ArgumentParserException {
    /**
     * Null message exception
     */
    public ArgumentParserMissingArgumentException() {
        super();
    }
    /**
     * Create message for exception for parsing short option
     * @param shortName  Short option name
     */
    public ArgumentParserMissingArgumentException(char shortName) {
        super("Missing argument of short option", shortName);
    }
    /**
     * Create message for exception for parsing long option
     * @param longName  Long option name
     */
    public ArgumentParserMissingArgumentException(String longName) {
        super("Missing argument of long option", longName);
    }
}
