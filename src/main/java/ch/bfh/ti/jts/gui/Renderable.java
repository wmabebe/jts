package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;

@FunctionalInterface
public interface Renderable {
    
    void render(Graphics2D g);
}
