package org.koturn.util;

/**
 * An exception throwed from {@link ArgumentParser#parse(String[])}.
 * This exception is throwed when a non-argument required option recieved a
 */
public class ArgumentParserDoesntTakeArgumentException extends ArgumentParserException {
    /**
     * Null message exception
     */
    public ArgumentParserDoesntTakeArgumentException() {
        super();
    }
    /**
     * Create message for exception for parsing long option
     * @param shortName  Short option name
     * @param arg       Argument for a short option
     */
    public ArgumentParserDoesntTakeArgumentException(char shortName, String arg) {
        super("An argument is given to non-argument required short option: -" + shortName + ", argument = " + arg);
    }
    /**
     * Create message for exception for parsing long option
     * @param longName  Long option name
     * @param arg       Argument for a long option
     */
    public ArgumentParserDoesntTakeArgumentException(String longName, String arg) {
        super("An argument is given to non-argument required long option: --" + longName + ", argument = " + arg);
    }
}
