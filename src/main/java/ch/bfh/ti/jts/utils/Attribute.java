package ch.bfh.ti.jts.utils;

import java.io.Serializable;

/**
 * An element attribute like orientation/state...
 * 
 * @author ente
 * @param <T>
 *            the attribute type (must be cloneable)
 */
public class Attribute<T> implements Serializable {
    
    private static final long         serialVersionUID = 1L;
    private String                    name;                 // name of the
                                                             // attribute
    private AttributeValueWrappers<T> value;                // attribute values
    
    /**
     * Construct a new object.
     * 
     * @param setName
     *            the new attribute name
     * @param setValue
     *            the new attribute value
     */
    protected Attribute(final String setName, final T setValue) {
        setName(setName);
        setValue(setValue);
    }
    
    /**
     * Get value of this attribute.
     * 
     * @return the value of this attribute
     */
    protected T getValue() {
        return value.get();
    }
    
    /**
     * Set new attribute value.
     * 
     * @param setValue
     *            the new value of this attribute
     */
    @SuppressWarnings("unchecked")
    protected void setValue(final T setValue) {
        value = (AttributeValueWrappers<T>) AttributeValueWrappers.wrap(setValue); // wrap
                                                                                   // the
                                                                                   // value
        if (value == null) {
            Game.printerr("Attribute [" + name + "] is not wrapable!");
        }
    }
    
    /**
     * Get name of this attribute.
     * 
     * @return the name of this attribute
     */
    protected String getName() {
        return name;
    }
    
    /**
     * Set a new name of this attribute. Attention: Make sure that there is no
     * attribute conflict.
     * 
     * @param setName
     *            the new name of this attribute
     */
    private void setName(final String setName) {
        name = setName;
    }
}
