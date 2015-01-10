package ch.bfh.ti.jts.utils;

import static ch.bfh.ti.jts.utils.Helpers.convert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
    
    private static final Logger log = LogManager.getLogger(Config.class);
    
    /**
     * Singleton
     */
    private static Config       instance;
    
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
    
    private final Properties properties = new Properties();
    
    private Config() {
        // load
        try {
            properties.load(new FileInputStream("src/main/resources/properties.config"));
        } catch (IOException e) {
            log.fatal("Failed to load configuration", e);
        }
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
    public <O> O getValue(final String key, O defaultValue) {
        if (defaultValue == null)
            throw new ArgumentNullException("defaultValue");
        O output = defaultValue;
        try {
            String value = properties.getProperty(key);
            if (value != null) {
                output = (O) convert(value, defaultValue.getClass());
            }
        } catch (final Exception e) {
            log.warn("Conversion failed", e);
        }
        return output;
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
            String value = properties.getProperty(key);
            if (value != null) {
                output = (O) convert(value, outputClass);
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
}
