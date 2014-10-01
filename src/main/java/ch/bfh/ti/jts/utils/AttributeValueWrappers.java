package ch.bfh.ti.jts.utils;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Wrapper classes for attribute values. We use them for making object
 * immutable.
 * 
 * @author ente
 * @param <T>
 *            The type of the object to wrap
 */
abstract public class AttributeValueWrappers<T> implements Cloneable, Serializable {
    
    private static final long   serialVersionUID    = 1L;
    /**
     * Name of the public static method which is used for identifying the right
     * wrapper class.
     */
    private final static String CAN_WRAP_CHECK_NAME = "canWrap";
    /**
     * The wrapped object.
     */
    private T                   wrapObject;
    
    /**
     * Can the class handle objects of the given type.
     * 
     * @param wrapClass
     *            the class which can be wrapped
     * @param object
     *            the object for wrapping
     * @return true if the class can handle the object, false if not
     */
    protected static boolean canWrap(Class<?> wrapClass, Object object) {
        if (wrapClass.isInstance(object)) {
            return true;
        }
        return false;
    }
    
    /**
     * Clone the given object.
     * 
     * @param objectToClone
     *            the object for cloning
     * @return a cloned version of the object
     */
    abstract protected T clone(T objectToClone);
    
    /**
     * Get a copy of the wrapped value. NOTICE: You will always get a copy of
     * the object
     * 
     * @return the value wrapped
     */
    public T get() {
        return clone(wrapObject);
    }
    
    /**
     * Wrap a copy of the provided value. NOTICE: It will always save a copy of
     * the object
     * 
     * @param object
     *            provide an object to wrap
     */
    public void set(T object) {
        wrapObject = clone(object);
    }
    
    /**
     * Return an attribute value wrapper which wraps the object given.
     * 
     * @param object
     *            the object to wrap
     * @return an attribute value wrapper, null if wrapping failed
     */
    @SuppressWarnings("unchecked")
    public static AttributeValueWrappers<?> wrap(Object object) {
        Class<?> parameters[] = { Object.class }; // parameters for wrapcheck
                                                  // method
        Class<?>[] wrappers = AttributeValueWrappers.class.getClasses();
        for (int i = 0; i < wrappers.length; i++) { // try all the wrappers
            Class<?> wrapper = wrappers[i]; // next wrapper
            try { // do some reflection work here..
                Method canWrapChecker = wrapper.getDeclaredMethod(CAN_WRAP_CHECK_NAME, parameters); // try
                                                                                                    // to
                                                                                                    // get
                                                                                                    // the
                                                                                                    // handler
                                                                                                    // checker
                if ((Boolean) canWrapChecker.invoke(null, object)) { // can this
                                                                     // object
                                                                     // be
                                                                     // wrapped?
                    AttributeValueWrappers<Object> newWrap = (AttributeValueWrappers<Object>) wrapper.newInstance(); // instantiate
                                                                                                                     // the
                                                                                                                     // new
                                                                                                                     // wrapper
                    newWrap.set(object); // start wrapping the object..
                    return newWrap; // return the wrapped object
                }
            } catch (NoSuchMethodException e) {
                if (Game.DEBUG) {
                    Game.printerr("Method [" + CAN_WRAP_CHECK_NAME + "] in " + wrapper.getName() + " not found");
                    e.printStackTrace();
                }
            } catch (SecurityException e) {
                if (Game.DEBUG) {
                    Game.printerr("Security violation");
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (Game.DEBUG) {
                    Game.printerr("Illegal access");
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                if (Game.DEBUG) {
                    Game.printerr("Illegal arguments");
                    e.printStackTrace();
                }
            } catch (InvocationTargetException e) {
                if (Game.DEBUG) {
                    Game.printerr("Invocation errro..");
                    e.printStackTrace();
                }
            } catch (InstantiationException e) {
                if (Game.DEBUG) {
                    Game.printerr("Cannot instatiate");
                    e.printStackTrace();
                }
            }
        }
        if (Game.DEBUG) {
            Game.printerr("No wrapper found for :" + object.getClass().getName());
        }
        return null;
    }
    /**
     * Wrapper for Integers.
     * 
     * @author ente
     */
    public static class wrapInteger extends AttributeValueWrappers<Integer> {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return true if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(Integer.class, object);
        }
        
        @Override
        protected Integer clone(Integer objectToClone) {
            return new Integer(objectToClone);
        }
    }
    /**
     * Wrapper for Longs.
     * 
     * @author ente
     */
    public static class wrapLong extends AttributeValueWrappers<Long> {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return true if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(Long.class, object);
        }
        
        @Override
        protected Long clone(Long objectToClone) {
            return new Long(objectToClone);
        }
    }
    /**
     * Wrapper for Floats.
     * 
     * @author ente
     */
    public static class wrapFloat extends AttributeValueWrappers<Float> {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return true if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(Float.class, object);
        }
        
        @Override
        protected Float clone(Float objectToClone) {
            return new Float(objectToClone);
        }
    }
    /**
     * Wrapper for Doubles.
     * 
     * @author ente
     */
    public static class wrapDouble extends AttributeValueWrappers<Double> {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return true if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(Double.class, object);
        }
        
        @Override
        protected Double clone(final Double objectToClone) {
            return new Double(objectToClone);
        }
    }
    /**
     * Wrapper for LinkedLists.
     * 
     * @author ente
     */
    public static class wrapLinkedList extends AttributeValueWrappers<LinkedList<?>> {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return true if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(LinkedList.class, object);
        }
        
        @Override
        protected LinkedList<?> clone(LinkedList<?> objectToClone) {
            return (LinkedList<?>) objectToClone.clone();
        }
    }
    /**
     * Wrapper for LinkedHashSet.
     * 
     * @author ente
     */
    public static class wrapLinkedHashSet extends AttributeValueWrappers<LinkedHashSet<?>> {
        
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return @{code true} if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(LinkedHashSet.class, object);
        }
        
        @Override
        protected LinkedHashSet<?> clone(LinkedHashSet<?> objectToClone) {
            return (LinkedHashSet<?>) objectToClone.clone();
        }
    }
    /**
     * Wrapper for HashSet.
     * 
     * @author ente
     */
    public static class wrapHashSet extends AttributeValueWrappers<HashSet<?>> {
        
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return @{code true} if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(HashSet.class, object);
        }
        
        @Override
        protected HashSet<?> clone(HashSet<?> objectToClone) {
            return (HashSet<?>) objectToClone.clone();
        }
    }
    /**
     * Wrapper for Vector2D.
     * 
     * @author ente
     */
    public static class wrapVector2D extends AttributeValueWrappers<Vector2D> {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return true if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(Vector2D.class, object);
        }
        
        @Override
        protected Vector2D clone(Vector2D objectToClone) {
            return objectToClone.clone();
        }
    }
    /**
     * Wrapper for Colors.
     * 
     * @author ente
     */
    public static class wrapColor extends AttributeValueWrappers<Color> {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * Check if an object can be wrapped by this class.
         * 
         * @param object
         *            the object to wrap
         * @return true if object can be wrapped
         */
        public static boolean canWrap(final Object object) {
            return canWrap(Color.class, object);
        }
        
        @Override
        protected Color clone(Color objectToClone) {
            return new Color(objectToClone.getRed(), objectToClone.getGreen(), objectToClone.getBlue(), objectToClone.getAlpha());
        }
    }
}
