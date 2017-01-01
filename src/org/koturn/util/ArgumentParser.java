package org.koturn.util;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * An argument parser class.
 * This class analyze options in argument like getopt() function in C-language.
 */
public class ArgumentParser {
    /**
     * Return new line code character and indent string.
     * This method is intended to used for an option description
     * @return  A combined string: new line character code and indent string
     */
    public static String newline() {
        return System.getProperty("line.separator") + "    ";
    }

    /**
     * Split string at the first position of {@code ch}
     * @param str  Target string
     * @param ch   Separater character
     * @return  Separated string array (the number of elements are two)
     */
    private static String[] split(String str, char ch) {
        int pos;
        if ((pos = str.indexOf('=')) == -1) {
            return new String[] {str, null};
        } else {
            return new String[] {str.substring(0, pos), str.substring(pos + 1)};
        }
    }

    /**
     * A name of this program
     */
    String progName;
    /**
     * Arguments unreleated to options
     */
    private ArrayList<String> remnantArguments;
    /**
     * Option array
     */
    private ArrayList<OptionItem> options;
    /**
     * HashMap between short option name and option instance
     */
    private HashMap<Character, OptionItem> shortOptionMap;
    /**
     * HashMap between long option name and option instance
     */
    private HashMap<String, OptionItem> longOptionMap;

    /**
     * Create ArgumentParser and set program name automaticaly.
     */
    public ArgumentParser() {
        this(Thread.currentThread().getStackTrace()[1].getClassName());
    }

    /**
     * Create ArgumentParser and use specified program name.
     * @param progName  A program name
     */
    public ArgumentParser(String progName) {
        setProgName(progName);
        remnantArguments = new ArrayList<>();
        options = new ArrayList<>();
        shortOptionMap = new HashMap<>();
        longOptionMap = new HashMap<>();
    }

    /**
     * Show usage of the program (including option descriptions)
     */
    public void showUsage() {
        String nl = System.getProperty("line.separator");
        System.out.println(
                "[Usage]" + nl
                + "java " + progName + " [Options ...] [Arguments ...]" + nl + nl
                + "[Options]");
        for (OptionItem item : options) {
            System.out.print("  ");
            if (item.getLongName() == null) {
                showShortOptionDescription(item);
            } else if (item.getShortName() == '\0') {
                showLongOptionDescription(item);
            } else {
                showShortOptionDescription(item);
                System.out.print(", ");
                showLongOptionDescription(item);
            }
            System.out.println(nl + "    " + item.getDescription());
        }
    }

    /**
     * Parse arguments
     * @param args  Command-line arguments
     * @throws ArgumentParserAmbiguousOptionException     Throw if omitted option name can not be resolved uniquely
     * @throws ArgumentParserMissingArgumentException     Throw if option argument is not found
     * @throws ArgumentParserDoesntTakeArgumentException  Throw if an argument is given to non-argument option
     * @throws ArgumentParserUnknownOptionException       Throw if unknown option is found
     */
    public void parse(String[] args)
            throws ArgumentParserAmbiguousOptionException,
            ArgumentParserMissingArgumentException,
            ArgumentParserDoesntTakeArgumentException,
            ArgumentParserUnknownOptionException {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (args[i].length() == 2) {
                    for (i++; i  < args.length; i++) {
                        remnantArguments.add(args[i]);
                    }
                    return;
                }
                i = parseLongOption(args, i);
            } else if (args[i].startsWith("-")) {
                i = parseShortOption(args, i);
            } else {
                remnantArguments.add(args[i]);
            }
        }
    }

    /**
     * Set one option to this parser
     * @param item  One option item
     */
    public void setOption(OptionItem item) {
        options.add(item);
        shortOptionMap.put(item.getShortName(), item);
        longOptionMap.put(item.getLongName(), item);
    }

    /**
     * Get option value with {@link OptionItem#shortName}
     * @param <T>  Type of option value
     * @param shortName  Short name (without "-" prefix) of an option
     * @return Option value
     */
    public <T> T getValue(char shortName) {
        return shortOptionMap.get(shortName).<T>getConvertedValue();
    }

    /**
     * Get option value with {@link OptionItem#longName}
     * @param <T>  Type of option value
     * @param longName  Short name (without "--" prefix) of an option
     * @return Option value
     */
    public <T> T getValue(String longName) {
        return longOptionMap.get(longName).<T>getConvertedValue();
    }

    /**
     * Use specified program name
     * @param progName  Program name of this program
     */
    public void setProgName(String progName) {
        this.progName = progName;
    }

    /**
     * Return program name
     * @return Name of this program
     */
    public String getProgName() {
        return progName;
    }

    /**
     * Get remnant arguments
     * @return Remnant arguments
     */
    public ArrayList<String> getRemnantArguments() {
        return remnantArguments;
    }


    /**
     * Parse long option such as {@code --opt, --opt=val or --opt val}
     * @param args  Command-line arguments
     * @param idx   Index of this long option
     * @return Index where parsing is completed
     * @throws ArgumentParserAmbiguousOptionException     Throw if omitted option name can not be resolved uniquely
     * @throws ArgumentParserMissingArgumentException     Throw if option argument is not found
     * @throws ArgumentParserDoesntTakeArgumentException  Throw if an argument is given to non-argument option
     * @throws ArgumentParserUnknownOptionException       Throw if unknown option is found
     */
    private int parseLongOption(String[] args, int idx)
            throws ArgumentParserAmbiguousOptionException,
            ArgumentParserMissingArgumentException,
            ArgumentParserDoesntTakeArgumentException,
            ArgumentParserUnknownOptionException {
        String[] keyval = split(args[idx].substring(2), '=');
        String longName = keyval[0];
        String value = keyval[1];
        OptionItem[] items = longOptionMap.values().stream()
                .filter(item -> item.getLongName().startsWith(longName))
                .toArray(OptionItem[]::new);
        if (items.length == 0) {
            throw new ArgumentParserUnknownOptionException(longName);
        } else if (items.length > 1) {
            throw new ArgumentParserAmbiguousOptionException(longName);
        }
        OptionItem item = items[0];
        switch (item.getOptType()) {
            case NoArgument:
                if (value != null) {
                    throw new ArgumentParserDoesntTakeArgumentException(longName, value);
                }
                item.setValue("true");
                return idx;
            case OptionalArgument:
                item.setValue(value == null ? "true" : value);
                return idx;
            case RequreidArgument:
                if (keyval.length == 1) {
                    if (idx + 1 >= args.length) {
                        throw new ArgumentParserMissingArgumentException(longName);
                    }
                    item.setValue(args[idx + 1]);
                    return idx + 1;
                } else {
                    item.setValue(value);
                    return idx;
                }
            default:
                return -1;
        }
    }

    /**
     * Parse short option such as {@code -o, -o val, -oval}
     * @param args  Command-line arguments
     * @param idx   Index of this short option
     * @return Index where parsing is completed
     * @throws ArgumentParserMissingArgumentException     Throw if option argument is not found
     * @throws ArgumentParserDoesntTakeArgumentException  Throw if an argument is given to non-argument option
     * @throws ArgumentParserUnknownOptionException       Throw if unknown option is found
     */
    private int parseShortOption(String[] args, int idx)
            throws ArgumentParserMissingArgumentException,
            ArgumentParserDoesntTakeArgumentException,
            ArgumentParserUnknownOptionException {
        String substr = args[idx].substring(1);
        char shortName = substr.charAt(0);
        OptionItem item = shortOptionMap.get(shortName);
        if (item == null) {
            throw new ArgumentParserUnknownOptionException(shortName);
        }
        if (substr.length() == 1) {
            if (item.getOptType() == OptionItem.OptionType.NoArgument) {
                item.setValue("true");
                return idx;
            }
            if (idx + 1 >= args.length) {
                throw new ArgumentParserMissingArgumentException(shortName);
            }
            item.setValue(args[idx + 1]);
            return idx + 1;
        } else {
            if (item.getOptType() == OptionItem.OptionType.NoArgument) {
                throw new ArgumentParserDoesntTakeArgumentException(shortName, substr.substring(1));
            }
            item.setValue(substr.substring(1));
            return idx;
        }
    }


    /**
     * Emit short option description
     * @param item  Option instance for emitting
     */
    private void showShortOptionDescription(OptionItem item) {
        System.out.print("-" + item.getShortName());
        if (item.getOptType() == OptionItem.OptionType.RequreidArgument
                || item.getOptType() == OptionItem.OptionType.OptionalArgument) {
            System.out.print(" " + item.getMetavar());
        }
    }

    /**
     * Emit long option description
     * @param item  Option instance for emitting
     */
    private void showLongOptionDescription(OptionItem item) {
        System.out.print("--" + item.getLongName());
        switch (item.getOptType()) {
            case NoArgument:
                break;
            case OptionalArgument:
                System.out.print("[=" + item.getMetavar() + "]");
                break;
            case RequreidArgument:
                System.out.print("=" + item.getMetavar());
                break;
        }
    }

    @Override
    public String toString() {
        return "ArgumentParser [progName=" + progName + ", remnantArguments=" + remnantArguments + ", options="
                + options + ", shortOptionMap=" + shortOptionMap + ", longOptionMap=" + longOptionMap + "]";
    }

    /**
     * Return hash code
     * @return Hash code
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((longOptionMap == null) ? 0 : longOptionMap.hashCode());
        result = prime * result + ((options == null) ? 0 : options.hashCode());
        result = prime * result + ((progName == null) ? 0 : progName.hashCode());
        result = prime * result + ((remnantArguments == null) ? 0 : remnantArguments.hashCode());
        result = prime * result + ((shortOptionMap == null) ? 0 : shortOptionMap.hashCode());
        return result;
    }

    /**
     * Identify object is equals to the other object
     * @param obj  The other object
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArgumentParser other = (ArgumentParser) obj;
        if (longOptionMap == null) {
            if (other.longOptionMap != null)
                return false;
        } else if (!longOptionMap.equals(other.longOptionMap))
            return false;
        if (options == null) {
            if (other.options != null)
                return false;
        } else if (!options.equals(other.options))
            return false;
        if (progName == null) {
            if (other.progName != null)
                return false;
        } else if (!progName.equals(other.progName))
            return false;
        if (remnantArguments == null) {
            if (other.remnantArguments != null)
                return false;
        } else if (!remnantArguments.equals(other.remnantArguments))
            return false;
        if (shortOptionMap == null) {
            if (other.shortOptionMap != null)
                return false;
        } else if (!shortOptionMap.equals(other.shortOptionMap))
            return false;
        return true;
    }
}
