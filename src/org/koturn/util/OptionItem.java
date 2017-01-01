package org.koturn.util;


/**
 * One option item
 */
public class OptionItem {
    /**
     * Indicates whether an option requires an argument or not.
     */
    public enum OptionType {
        /**
         * An option doesn't require an argument
         */
        NoArgument,
        /**
         * An option requires an argument
         */
        RequreidArgument,
        /**
         * An option is not absolutely requires argument (short option requires an argument)
         */
        OptionalArgument
    }

    /**
     * Return default converter of specified argument.
     * @param o An instance
     * @return  Wrapper class converter if o is premitive type, otherwise converter which calling o#toString()
     */
    private static IConverter<?> getDefaultConverter(Object o) {
        if (o instanceof Byte) {
            return Byte::parseByte;
        } else if (o instanceof Short) {
            return Short::parseShort;
        } else if (o instanceof Integer) {
            return Integer::parseInt;
        } else if (o instanceof Long) {
            return Long::parseLong;
        } else if (o instanceof Float) {
            return Float::parseFloat;
        } else if (o instanceof Double) {
            return Double::parseDouble;
        } else {
            return val -> val.toString();
        }
    }

    /**
     * Short option name
     */
    private char shortName;
    /**
     * Long option name
     */
    private String longName;
    /**
     * Option type
     */
    private OptionType optType;
    /**
     * Value of this option
     */
    private String value;
    /**
     * Description of this option
     */
    private String description;
    /**
     * Meta variable for this option argument.
     */
    private String metavar;
    /**
     * Option value converter.
     */
    private IConverter<?> converter;

    /**
     * Full constructor of {@link OptionItem}
     * @param <T>  Type of default value
     * @param shortName     Short name of this option
     * @param longName      Long name of this option
     * @param optType       This parameter indicates an option requires an argument or not
     * @param description   Description for this option
     * @param metavar       Name of meta variable used in usage
     * @param defaultValue  A default value of this option
     * @param converter     Option value converter
     */
    public <T> OptionItem(char shortName, String longName, OptionType optType, String description, T metavar, String defaultValue, IConverter<?> converter) {
        this.shortName = shortName;
        this.longName = longName;
        this.optType = optType;
        this.value = defaultValue;
        this.metavar = metavar.toString();
        this.description = description;
        this.converter = converter == null ? getDefaultConverter(defaultValue) : converter;
    }

    /**
     * Constructor for argument-required option.
     * Option value converter is automatically selected using {@link #getDefaultConverter(Object)} by the type of parameter, {@code defaultValue}.
     * @param <T>  Type of default value
     * @param shortName     Short name of this option
     * @param longName      Long name of this option
     * @param optType       This parameter indicates an option requires an argument or not
     * @param description   Description for this option
     * @param metavar       Name of meta variable used in usage
     * @param defaultValue  A default value of this option
     */
    public <T> OptionItem(char shortName, String longName, OptionType optType, String description, String metavar, T defaultValue) {
        this(shortName, longName, optType, description, metavar, defaultValue.toString(), getDefaultConverter(defaultValue));
    }

    /**
     * Constructor for argument-required option.
     * Option value converter is automatically selected using {@link #getDefaultConverter(Object)} by the type of parameter, {@code defaultValue}.
     * This constructor is for short name only options.
     * @param <T>  Type of default value
     * @param shortName     Short name of this option
     * @param optType       This parameter indicates an option requires an argument or not
     * @param description   Description for this option
     * @param metavar       Name of meta variable used in usage
     * @param defaultValue  A default value of this option
     * @see #OptionItem(char, String, OptionType, String, String, Object)
     */
    public <T> OptionItem(char shortName, OptionType optType, String description, String metavar, T defaultValue) {
        this(shortName, null, optType, description, metavar, defaultValue);
    }

    /**
     * Constructor for argument-required option.
     * Option value converter is automatically selected using {@link #getDefaultConverter(Object)} by the type of parameter, {@code defaultValue}.
     * This constructor is for long name only options.
     * @param <T>  Type of default value
     * @param longName      Long name of this option
     * @param optType       This parameter indicates an option requires an argument or not
     * @param description   Description for this option
     * @param metavar       Name of meta variable used in usage
     * @param defaultValue  A default value of this option
     * @see #OptionItem(char, String, OptionType, String, String, Object)
     */
    public <T> OptionItem(String longName, OptionType optType, String description, String metavar, T defaultValue) {
        this('\0', longName, optType, description, metavar, defaultValue);
    }


    public OptionItem(char shortName, String longName, OptionType optType, String description, String metavar, IConverter<?> converter) {
        this(shortName, longName, optType, description, metavar, null, converter);
    }

    /**
     * Constructor for argument-required option.
     * This constructor is for short name only options.
     * @param shortName     Short name of this option
     * @param optType       This parameter indicates an option requires an argument or not
     * @param description   Description for this option
     * @param metavar       Name of meta variable used in usage
     * @param converter     Option value converter
     * @see #OptionItem(char, String, OptionType, String, String, IConverter)
     */
    public OptionItem(char shortName, OptionType optType, String description, String metavar, IConverter<?> converter) {
        this(shortName, null, optType, description, metavar, converter);
    }

    /**
     * Constructor for argument-required option.
     * This constructor is for long name only options.
     * @param longName      Long name of this option
     * @param optType       This parameter indicates an option requires an argument or not
     * @param description   Description for this option
     * @param metavar       Name of meta variable used in usage
     * @param converter     Option value converter
     * @see #OptionItem(char, String, OptionType, String, String, IConverter)
     */
    public OptionItem(String longName, OptionType optType, String description, String metavar, IConverter<?> converter) {
        this('\0', longName, optType, description, metavar, converter);
    }

    /**
     * Constructor for binary option (option that doesn't require an argument).
     * @param shortName    Short name of option
     * @param longName     Long name of option
     * @param description  Description for this option
     * @see #OptionItem(char, String, OptionType, String, Object, String, IConverter)
     */
    public OptionItem(char shortName, String longName, String description) {
        this(shortName, longName, OptionType.NoArgument, description, "false", null, Boolean::parseBoolean);
    }

    /**
     * Constructor for binary option (option that doesn't require an argument).
     * This constructor is for short name only options.
     * @param shortName    Short name of option
     * @param description  Description for this option
     * @see #OptionItem(char, String, String)
     */
    public OptionItem(char shortName, String description) {
        this(shortName, null, description);
    }

    /**
     * Constructor for binary option (option that doesn't require an argument).
     * This constructor is for long name only options.
     * @param longName     Long name of option
     * @param description  Description for this option
     * @see #OptionItem(char, String, String)
     */
    public OptionItem(String longName, String description) {
        this('\0', longName, description);
    }

    /**
     * Getter of {@link #shortName}
     * @return Value of {@link #shortName}
     */
    public char getShortName() {
        return shortName;
    }
    /**
     * Setter of {@link #shortName}
     * @param shortName  A value for {@link #shortName}
     */
    public void setShortName(char shortName) {
        this.shortName = shortName;
    }
    /**
     * Getter of {@link #longName}
     * @return Value of {@link #longName}
     */
    public String getLongName() {
        return longName;
    }
    /**
     * Setter of {@link #longName}
     * @param longName  A value for {@link #longName}
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }
    /**
     * Getter of {@link #value}
     * @return Value of {@link #value}
     */
    public String getValue() {
        return value;
    }
    /**
     * Setter of {@link #value}
     * @param value  A value for {@link #value}
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * Getter of {@link #optType}
     * @return Value of {@link #optType}
     */
    public OptionType getOptType() {
        return optType;
    }
    /**
     * Setter of {@link #optType}
     * @param optType  A value for {@link #optType}
     */
    public void setOptType(OptionType optType) {
        this.optType = optType;
    }
    /**
     * Getter of {@link #description}
     * @return Value of {@link #description}
     */
    public String getDescription() {
        return description;
    }
    /**
     * Setter of {@link #description}
     * @param description  A value for {@link #description}
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Getter of {@link #metavar}
     * @return Value of {@link #metavar}
     */
    public String getMetavar() {
        return metavar;
    }
    /**
     * Setter of {@link #metavar}
     * @param metavar  A value for {@link #metavar}
     */
    public void setMetavar(String metavar) {
        this.metavar = metavar;
    }
    /**
     * Getter of {@link #converter}
     * @return Value of {@link #converter}
     */
    public IConverter<?> getConverter() {
        return converter;
    }
    /**
     * Setter of {@link #converter}
     * @param converter  A value for {@link #converter}
     */
    public void setConverter(IConverter<?> converter) {
        this.converter = converter;
    }

    /**
     * @param <T>  Type of option value
     * @return  Option value
     */
    @SuppressWarnings("unchecked")
    public <T> T getConvertedValue() {
       return (T) converter.convert(value);
    }

    /**
     * Return hash code
     * @return Hash code
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((longName == null) ? 0 : longName.hashCode());
        result = prime * result + ((metavar == null) ? 0 : metavar.hashCode());
        result = prime * result + ((optType == null) ? 0 : optType.hashCode());
        result = prime * result + shortName;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        OptionItem other = (OptionItem) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (longName == null) {
            if (other.longName != null)
                return false;
        } else if (!longName.equals(other.longName))
            return false;
        if (metavar == null) {
            if (other.metavar != null)
                return false;
        } else if (!metavar.equals(other.metavar))
            return false;
        if (optType != other.optType)
            return false;
        if (shortName != other.shortName)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    /**
     * Stringify this object
     */
    @Override
    public String toString() {
        return "OptionItem [shortName=" + shortName + ", longName=" + longName + ", optType=" + optType + ", value="
                + value + ", description=" + description + ", metavar=" + metavar + ", converter=" + converter + "]";
    }
}
