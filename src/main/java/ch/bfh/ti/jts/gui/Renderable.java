package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;

public interface Renderable {
    
    /**
     * The rendering layer of the object. 0: Background 1: First layer
     *
     * @return the layer
     */
    int getRenderLayer();
    
    /**
     * Render the implementing object.
     *
     * @param g
     *            the object to render with.
     */
    void render(Graphics2D g);
}
