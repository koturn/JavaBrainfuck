package org.koturn.util;

/**
 * FUnctional interface for {@code String -> T} (desired type) converter.
 * @param <T> Desired type
 */
@FunctionalInterface
public interface IConverter<T> {
    /**
     * Convert function.
     * @param value  String value
     * @return A value converted to desired type, {@code T}
     */
    public T convert(String value);
}
