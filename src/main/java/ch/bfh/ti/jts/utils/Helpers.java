package ch.bfh.ti.jts.utils;

public class Helpers {
    
    /**
     * Make sure a value is in between the specified bounds.
     *
     * @param value
     *            the value
     * @param min
     *            the minimal possible value (inclusive)
     * @param max
     *            the maximal possible value (inclusive)
     * @return the clamped value
     */
    public static double clamp(double value, final double min, final double max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }
}
