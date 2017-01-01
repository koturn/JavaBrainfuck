package org.koturn.util;

/**
 * An exception throwed from {@link ArgumentParser#parse(String[])}.
 * This exception is throwed when the omitted option name can not be resolved uniquely.
 * For example, suppose that ArgumentParser can recognize long option {@code --foobarbuz} and {@code --foobazbar}.
 * A command line argument {@code --foobar} can resolve {@code --foobarbuz} uniquely but {@code --foobar} can resolve
 * {@code --foobarbuz} or {@code --foobazbar}.
 */
public class ArgumentParserAmbiguousOptionException extends ArgumentParserException {
    /**
     * Null message exception
     */
    public ArgumentParserAmbiguousOptionException() {
        super("Ambiguous option");
    }
    /**
     * Create message for exception for parsing long option
     * @param longName  Long option name
     */
    public ArgumentParserAmbiguousOptionException(String longName) {
        super("Ambiguous long option", longName);
    }
}
