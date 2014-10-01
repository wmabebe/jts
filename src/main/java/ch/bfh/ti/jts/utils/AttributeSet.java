package ch.bfh.ti.jts.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A class which holds and tracks attributes. There are three different method
 * types: - get: Get an attribute. - set: Set an attribute to a provided value
 * and add the attribute to the map of changed attributes. - update: Set an
 * attribute to a provide value without adding the attribute to the map of
 * changed attributes.
 * 
 * @author ente
 */
public class AttributeSet implements Serializable {
    
    private static final long                       serialVersionUID  = 1L;
    /**
     * All attributes in the set.
     */
    private final HashMap<String, Attribute<?>>     attributes        = new HashMap<String, Attribute<?>>();
    /**
     * Pointers to the changed attributes in attributes.
     */
    private final HashMap<String, Attribute<?>>     changedAttributes = new HashMap<String, Attribute<?>>();
    /**
     * If freecedCount > 0 : the attribute set is freezed. Freezing an attribute
     * set means that all changes to attributes won't take effect until the
     * attribute set is unfreezed.
     */
    private transient int                           freezeCount       = 0;
    /**
     * The freezed attributes.
     */
    private transient HashMap<String, Attribute<?>> freezedAttributes = new HashMap<String, Attribute<?>>();
    
    /**
     * Construct a new empty object.
     */
    protected AttributeSet() {
        // empty
    }
    
    /**
     * Construct a new initialized object.
     * 
     * @param initAttributes
     *            the attributes for initialization
     */
    protected AttributeSet(final HashMap<String, Attribute<?>> initAttributes) {
        // add all attributes
        final Iterator<Attribute<?>> i = initAttributes.values().iterator();
        while (i.hasNext()) {
            createAttribute(i.next());
        }
    }
    
    /**
     * Write object override.
     * 
     * @param stream
     *            the stream to write to
     * @throws IOException
     *             an exception thrown by the underlying output stream.
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        // freeze the attribute set before writing
        freeze();
        stream.defaultWriteObject();
        unfreeze();
    }
    
    /**
     * Read object override.
     * 
     * @param stream
     *            the stream to read from
     * @throws ClassNotFoundException
     *             underlying stream exception
     * @throws IOException
     *             underlying stream exception
     */
    private void readObject(final ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        // initialize transient
        freezeCount = 0;
        freezedAttributes = new HashMap<String, Attribute<?>>();
    }
    
    /**
     * Remove a certain attribute from a provided hashmap.
     * 
     * @param name
     *            the attributes name
     * @param map
     *            the map in which the attribute should be
     * @return @{code true} on success, @{code false} on failure
     */
    private synchronized boolean removeFromMap(final String name, final HashMap<String, Attribute<?>> map) {
        return map.remove(name) != null;
    }
    
    /**
     * Check if attribute exist.
     * 
     * @param name
     *            the attributes name
     * @return true if it exists, false if not
     */
    private synchronized boolean hasAttribute(final String name) {
        return attributes.get(name) != null;
    }
    
    /**
     * Has the attribute changed.
     * 
     * @param name
     *            the attributes name
     * @return true if it has changed, false if not
     */
    private synchronized boolean hasAttributeChanged(final String name) {
        return changedAttributes.get(name) != null;
    }
    
    /**
     * Create new attribute providing a name and a value.
     * 
     * @param name
     *            the name of the attribute
     * @param value
     *            the value of the attribute
     * @return true if creation was successful, false if not
     */
    public synchronized <T> boolean createAttribute(final String name, final T value) {
        if (!createAttribute(new Attribute<T>(name, value))) {
            return false;
        }
        return true;
    }
    
    /**
     * Create new attribute providing an attribute object.
     * 
     * @param createAttribute
     *            the attribute to create
     * @return true if attribute could be created, false if not
     */
    @SuppressWarnings("unchecked")
    private synchronized <T> boolean createAttribute(final Attribute<T> createAttribute) {
        Attribute<T> attribute;
        if (freezeCount <= 0) {
            // not in freeze mode
            attribute = (Attribute<T>) attributes.get(createAttribute.getName());
        } else {
            // in freeze mode
            attribute = (Attribute<T>) freezedAttributes.get(createAttribute.getName());
        }
        // do we already have the attribute?
        if (attribute != null) { // does the attribute already exist?
            // something went wrong here
            if (Game.DEBUG) {
                Game.printerr("Tried to add attribute (" + createAttribute.getName() + ") twice");
            }
            return false;
        }
        // add the attribute
        if (freezeCount <= 0) {
            attributes.put(createAttribute.getName(), createAttribute);
        } else {
            freezedAttributes.put(createAttribute.getName(), createAttribute);
        }
        return true;
    }
    
    /**
     * Get an attribute.
     * 
     * @param name
     *            the attributes name
     * @return the attribute object
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T getAttribute(final String name) {
        final Attribute<T> attribute = (Attribute<T>) attributes.get(name); // search
                                                                            // for
                                                                            // attribute
        if (attribute != null) {
            return attribute.getValue();
        }
        Game.printwarn("Attribute: " + name + " does not exist!");
        return null;
    }
    
    /**
     * Change an attribute providing an object.
     * 
     * @param setAttribute
     *            the attribute object
     * @return true if change was successful, false if not
     */
    private synchronized <T> boolean setAttribute(final Attribute<T> setAttribute) {
        return setAttribute(setAttribute.getName(), setAttribute.getValue());
    }
    
    /**
     * Change an attribute providing a name/value.
     * 
     * @param name
     *            the attributes name to set/change
     * @param value
     *            the new value
     * @return true if change was successful, false if not
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> boolean setAttribute(final String name, final T value) {
        final Attribute<T> attribute = (Attribute<T>) attributes.get(name); // search
                                                                            // attribute
        // do we already have such an attribute
        if (attribute != null) {
            if (freezeCount <= 0) { // is attribute set freezed?
                // nope -> change it directly
                attribute.setValue(value); // set new value
                attributeChanged(attribute); // attribute has changed
            } else {
                // set is freezed -> change freeze attribute
                Attribute<T> freezeAttribute = (Attribute<T>) freezedAttributes.get(name); // search
                                                                                           // attribute
                                                                                           // in
                                                                                           // freezed
                                                                                           // map
                if (freezeAttribute != null) {
                    // freeze attribute does exist
                    freezeAttribute.setValue(value);
                } else {
                    freezeAttribute = new Attribute<T>(name, value);
                    freezedAttributes.put(freezeAttribute.getName(), freezeAttribute);
                }
                // attribute has not changed yet.. (still freezed)
            }
        } else {
            if (!createAttribute(name, value)) { // create the attribute
                Game.printerr("Attribute (" + name + ") setting failed!");
                return false;
            }
        }
        return true;
    }
    
    /**
     * Update the attribute upAttribute.
     * 
     * @param updateAttribute
     *            the attribute to update
     * @return true on success, false if not
     */
    private synchronized <T> boolean updateAttribute(final Attribute<T> updateAttribute) {
        return updateAttribute(updateAttribute.getName(), updateAttribute.getValue());
    }
    
    /**
     * Update an attribute, which means set it and remove it from changed map.
     * 
     * @param name
     *            of the attribute to update
     * @param value
     *            of the attribute to update
     * @return true on success, false if not
     */
    public synchronized <T> boolean updateAttribute(final String name, final T value) {
        if (!setAttribute(name, value) // set the new attribute value
                || !removeFromMap(name, changedAttributes)) { // This is an
                                                              // update: remove
                                                              // the attribute
                                                              // from the
                                                              // changed map..
            Game.printerr("Attribute (" + name + ") updating failed!");
            return false;
        }
        return true;
    }
    
    /**
     * Flag the provided attribute as changed.
     * 
     * @param attributeChanged
     *            the attribute which has changed
     */
    private synchronized void attributeChanged(final Attribute<?> attributeChanged) {
        if (!hasAttributeChanged(attributeChanged.getName())) {
            changedAttributes.put(attributeChanged.getName(), attributeChanged);
        }
    }
    
    /**
     * Export all the changed attributes.
     * 
     * @return all the changed attributes
     */
    public synchronized AttributeSet exportChangedAttributes() {
        return new AttributeSet(changedAttributes); // get new attribute set
                                                    // with changed attributes
                                                    // in
    }
    
    /**
     * Import an attributeSet of updated attributes -> update all of them.
     * 
     * @param importUpdateSet
     *            the attribute set to be imported and updated
     */
    public synchronized void importAttributesUpdate(final AttributeSet importUpdateSet) {
        final Iterator<Attribute<?>> i = importUpdateSet.attributes.values().iterator();
        while (i.hasNext()) { // import all the changed attributes
            updateAttribute(i.next()); // update the attribute
        }
    }
    
    /**
     * Freeze the attribute set.
     */
    public synchronized void freeze() {
        // freeze the attribute set
        freezeCount++;
    }
    
    /**
     * Unfreeze the attribute set. Apply all changes buffered during freeze
     */
    public synchronized void unfreeze() {
        if (freezeCount <= 0) {
            if (Game.DEBUG) {
                Game.printerr("Unfreeze / Freeze unbalanced");
            }
        } else {
            // unfreeze, Important: do this before re-setting the attributes.
            // Oterwise they'll not get added to the changed attributes list.
            freezeCount--;
        }
        // completely unfrozen?
        if (freezeCount <= 0) {
            // apply changes
            final Iterator<Attribute<?>> freezedAttributesIterator = freezedAttributes.values().iterator();
            while (freezedAttributesIterator.hasNext()) {
                setAttribute(freezedAttributesIterator.next());
                freezedAttributesIterator.remove();
            }
        }
    }
}
