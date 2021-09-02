package ch.bfh.ti.jts.utils;

import static ch.bfh.ti.jts.utils.Helpers.clamp;
import static ch.bfh.ti.jts.utils.Helpers.convert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.exceptions.ArgumentNullException;

/**
 * Config class handles application properties config.
 *
 * @author Enteee
 * @author winki
 */
public class Config {

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private static final Logger log        = LogManager.getLogger(Config.class);

    /**
     * Singleton
     */
    private static Config       instance;

    private final Properties    properties = new Properties();

    private Config() {
        // load
        try {
            properties.load(new FileInputStream("src/main/resources/properties.config"));
        } catch (final IOException e) {
            log.fatal("Failed to load configuration", e);
        }
    }

    /**
     * Returns a config value from type boolean.
     *
     * @param key
     *            property key
     * @param defaultValue
     *            default value
     * @return boolean value
     */
    public boolean getBool(final String key, final boolean defaultValue) {
        boolean output = defaultValue;
        final String value = properties.getProperty(key);
        if (value != null) {
            try {
                output = Boolean.parseBoolean(value);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }
        return output;
    }

    /**
     * Returns an config value from type double.
     *
     * @param key
     *            property key
     * @param defaultValue
     *            default value
     * @param minValue
     *            minimum value
     * @param maxValue
     *            maximumn value
     * @return double value
     */
    public double getDouble(final String key, final double defaultValue, final double minValue, final double maxValue) {
        double output = defaultValue;
        final String value = properties.getProperty(key);
        if (value != null) {
            try {
                output = Double.parseDouble(value);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }
        return clamp(output, minValue, maxValue);
    }

    /**
     * @see Config#getDouble(String, double, double, double)
     */
    public double getDouble(final String key, final int defaultValue) {
        return getDouble(key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Returns a config value out of an enumeration of possible values. Default
     * is the first value in the option list.
     *
     * @param key
     *            property key
     * @param options
     *            possible values
     * @return enum value
     */
    public String getEnum(final String key, final String[] options) {
        if (options == null) {
            throw new ArgumentNullException("options");
        }
        if (options.length == 0) {
            throw new IllegalArgumentException("no options");
        }
        final String value = properties.getProperty(key);
        if (value != null) {
            final Set<String> lookup = new HashSet<String>(Arrays.asList(options));
            if (lookup.contains(value)) {
                return value;
            }
        }
        return options[0];
    }

    /**
     * @see Config#getInt(String, int, int, int)
     */
    public int getInt(final String key, final int defaultValue) {
        return getInt(key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns an config value from type integer.
     *
     * @param key
     *            property key
     * @param defaultValue
     *            default value
     * @param minValue
     *            minimum value
     * @param maxValue
     *            maximumn value
     * @return integer value
     */
    public int getInt(final String key, final int defaultValue, final int minValue, final int maxValue) {
        int output = defaultValue;
        final String value = properties.getProperty(key);
        if (value != null) {
            try {
                output = Integer.parseInt(value);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }
        return clamp(output, minValue, maxValue);
    }

    /**
     * Reads a property value.
     *
     * @param key
     *            property key
     * @param defaultValue
     *            default value if property is not found
     * @return property value
     */
    @SuppressWarnings("unchecked")
    public <O> O getValue(final String key, final O defaultValue) {
        if (defaultValue == null) {
            throw new ArgumentNullException("defaultValue");
        }
        O output = defaultValue;
        try {
            final String value = properties.getProperty(key);
            if (value != null) {
                output = (O) convert(value, defaultValue.getClass());
            }
        } catch (final Exception e) {
            log.warn("Conversion failed", e);
        }
        return output;
    }

    /**
     * @see Config#getValueNullable(String, Class<O>)
     */
    public String getValueNullable(final String key) {
        return getValueNullable(key, String.class);
    }

    /**
     * Reads a property value.
     *
     * @param key
     *            property key
     * @param defaultValue
     *            default value if property is not found
     * @param outputClass
     *            target type of the value
     * @return property value
     */
    public <O> O getValueNullable(final String key, final Class<O> outputClass) {
        O output = null;
        try {
            final String value = properties.getProperty(key);
            if (value != null) {
                output = convert(value, outputClass);
            }
        } catch (final Exception e) {
            log.warn("Conversion failed", e);
        }
        return output;
    }
}
