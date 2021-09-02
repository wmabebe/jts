package ch.bfh.ti.jts.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.App;
import ch.bfh.ti.jts.Main;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.gui.console.Console;
import ch.bfh.ti.jts.gui.console.JtsConsole;
import ch.bfh.ti.jts.utils.Config;
import ch.bfh.ti.jts.utils.layers.Layers;

/**
 * Window for the application.
 *
 * @author Enteee
 * @author winki
 */
public class Window {

    private class FrameComponentAdapter extends ComponentAdapter {

        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            windoww = renderPanel.getWidth();
            windowh = renderPanel.getHeight();
        }
    }

    private class RenderPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        @Override
        public void paintComponent(final Graphics g) {
            final Graphics2D g2d = (Graphics2D) g;
            // simulate parts of the net
            final Net wallClockSimulationState = App.getInstance().getSimulation().getWallCLockSimulationState();
            try {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                t = new AffineTransform();
                final AffineTransform tConsole = new AffineTransform(t);
                // move to offset
                t.translate(offset.getX(), offset.getY());
                // transformation (scroll and zoom)
                t.translate(zoomCenter.getX(), zoomCenter.getY());
                // zoom
                t.scale(1 / zoom, 1 / zoom);
                // move to the zoom center
                t.translate(-zoomCenter.getX(), -zoomCenter.getY());
                if (Main.DEBUG) {
                    g2d.setColor(Color.GREEN);
                    g2d.drawLine(0, 0, (int) offset.getX(), (int) offset.getY());
                    g2d.drawLine((int) zoomCenter.getX() - 10, (int) zoomCenter.getY(), (int) zoomCenter.getX() + 10, (int) zoomCenter.getY());
                    g2d.drawLine((int) zoomCenter.getX(), (int) zoomCenter.getY() - 10, (int) zoomCenter.getX(), (int) zoomCenter.getY() + 10);
                }
                g2d.setTransform(t);
                if (Main.DEBUG) {
                    g2d.setColor(Color.RED);
                    g2d.drawLine(-20, 0, 20, 0);
                    g2d.drawLine(0, -20, 0, 20);
                    g2d.drawLine((int) zoomCenter.getX() - 10, (int) zoomCenter.getY(), (int) zoomCenter.getX() + 10, (int) zoomCenter.getY());
                    g2d.drawLine((int) zoomCenter.getX(), (int) zoomCenter.getY() - 10, (int) zoomCenter.getX(), (int) zoomCenter.getY() + 10);
                }
                // center on screen
                g2d.transform(AffineTransform.getTranslateInstance(windoww / 2, windowh / 2));

                try {
                    // save inverse transformation to get world coordinates from
                    // screen coordinates later
                    screenToWorldTransform = g2d.getTransform().createInverse();
                } catch (final NoninvertibleTransformException e) {
                    log.error("Can not invert world-->screen matrix.", e);
                }

                // render everything
                final Layers<Renderable> renderables = wallClockSimulationState.getRenderable();
                for (final int layer : renderables.getLayersIterator()) {
                    renderables.getLayerStream(layer).sequential().forEach(e -> {
                        e.render(g2d, App.getInstance().getSimulation().getSavedStates());
                    });
                }
                // render console
                g2d.setTransform(tConsole);
                console.render(g2d);

                // Let the OS have a little time...
                Thread.yield();

                // repaint after every step
                frame.repaint();
            } finally {
                if (g2d != null) {
                    g2d.dispose();
                }
            }
        }
    }

    private class RenderPanelKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            final int keyCode = keyEvent.getKeyCode();
            keys.add(keyCode);
        }

        @Override
        public void keyReleased(final KeyEvent keyEvent) {
            final int keyCode = keyEvent.getKeyCode();
            keys.remove(keyCode);
        }

        @Override
        public void keyTyped(final KeyEvent keyEvent) {
            // keyCode is undefinet in this event
            // so we use the character instead
            console.keyTyped(keyEvent.getKeyChar());
        }

    }

    private class RenderPanelMouseAdapter extends MouseAdapter {

        private boolean isDown            = false;
        private Point   mousePressedPoint = new Point();

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (isDown) {
                final double deltaX = e.getX() - mousePressedPoint.getX();
                final double deltaY = e.getY() - mousePressedPoint.getY();
                offset.setLocation(offset.getX() + deltaX, offset.getY() + deltaY);
                mousePressedPoint = e.getPoint();
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            mousePressedPoint = e.getPoint();
            isDown = true;
            // get world coordinates of mouse pointer
            final Point worldCoordinatesPoint = new Point();
            screenToWorldTransform.transform(mousePressedPoint, worldCoordinatesPoint);
            Optional<Class<?>> filterClass = Optional.empty();
            if (e.isControlDown()) {
                filterClass = Optional.of(Agent.class);
            } else if (e.isShiftDown()) {
                filterClass = Optional.of(Junction.class);
            } else if (e.isAltDown()) {
                filterClass = Optional.of(Edge.class);
            }
            filterClass.ifPresent(filter -> {
                App.getInstance().getSimulation().getWallCLockSimulationState().getElementByCoordinates(worldCoordinatesPoint, CLICK_RADIUS, filter).ifPresent(element -> {
                    final Console console = Window.getInstance().getConsole();
                    console.stringTyped(String.format("%d", element.getId()));
                });
            });
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            isDown = false;
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent mouseEvent) {
            final Point mousePoint = mouseEvent.getPoint();
            // set zoom center relative to no zoom
            final int rotation = mouseEvent.getWheelRotation();
            double zoomDelta = 0;
            if (rotation < 0 && zoom >= ZOOM_DELTA) {
                // zoom in
                zoomDelta = -ZOOM_DELTA;
            } else if (rotation > 0) {
                // zoom out
                zoomDelta = ZOOM_DELTA;
            }
            // change zoom
            zoom += zoomDelta;
            try {
                final Point mousePointInverse = new Point();
                t.inverseTransform(mousePoint, mousePointInverse);
                zoomCenter.setLocation(mousePointInverse.getX(), mousePointInverse.getY());
                offset.setLocation(mousePoint.getX() - mousePointInverse.getX(), mousePoint.getY() - mousePointInverse.getY());
            } catch (final NoninvertibleTransformException e) {
                log.error("Can not invert mouse drag vector", e);
            }
        }
    }

    /**
     * singleton
     *
     * @return instance
     */
    public static Window getInstance() {
        return INSTANCE;
    }

    private static final Logger log                    = LogManager.getLogger(Window.class);

    /**
     * Zoom delta. Determines how much to change the zoom when scrolling. Also
     * sets the minimum zoom
     */
    private static final double ZOOM_DELTA             = 0.05;

    /**
     * The click radius when selectimg elements;
     */
    private static final double CLICK_RADIUS           = Config.getInstance().getDouble("click.radius", 30.0, 0.0, 1000.0);

    private static final Window INSTANCE               = new Window();
    private final JFrame        frame;
    private final JPanel        renderPanel;
    private int                 windoww                = 1000;
    private int                 windowh                = 600;
    /**
     * Offset in x and y direction from (0/0)
     */
    private final Point2D       offset                 = new Point2D.Double();
    /**
     * Zoom factor
     */
    private double              zoom                   = 1;
    private AffineTransform     t                      = new AffineTransform();
    private AffineTransform     screenToWorldTransform = new AffineTransform();

    private final Point2D       zoomCenter             = new Point2D.Double();
    private final Set<Integer>  keys                   = new HashSet<Integer>();
    private final Console       console                = new JtsConsole();

    public Window() {
        frame = new JFrame();
        frame.setTitle("JavaTrafficSimulator");
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.addComponentListener(new FrameComponentAdapter());
        renderPanel = new Window.RenderPanel();
        final MouseAdapter renderPanelMouseAdaptor = new RenderPanelMouseAdapter();
        frame.addKeyListener(new RenderPanelKeyAdapter());
        renderPanel.addMouseListener(renderPanelMouseAdaptor);
        renderPanel.addMouseMotionListener(renderPanelMouseAdaptor);
        renderPanel.addMouseWheelListener(renderPanelMouseAdaptor);
        frame.setContentPane(renderPanel);
    }

    public Console getConsole() {
        return console;
    }

    public void setVisible(final boolean visible) {
        frame.setVisible(visible);
    }

}
