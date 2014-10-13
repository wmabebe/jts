package ch.bfh.ti.jts.gui;

import java.awt.Color;
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
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Window {
    
    /**
     * Zoom delta. Determines how much to change the zoom when scrolling. Also
     * sets the minimum zoom
     */
    private static final double ZOOM_DELTA = 0.05;
    private JFrame              frame;
    private JPanel              panel;
    private int                 windoww    = 1000;
    private int                 windowh    = 600;
    /**
     * Offset in x and y direction from (0/0)
     */
    private final Point2D       offset     = new Point2D.Double();
    /**
     * Zoom factor
     */
    private double              zoom       = 1;
    private AffineTransform     t          = new AffineTransform();
    private final Point2D       zoomCenter = new Point2D.Double();
    private final Set<Integer>  keys       = new HashSet<Integer>();
    private final Renderable    renderable;
    
    public Window(final Renderable renderable) {
        if (renderable == null) {
            throw new IllegalArgumentException("renderable is null");
        }
        this.renderable = renderable;
        frame = new JFrame();
        init();
    }
    
    public void setVisible(final boolean visible) {
        frame.setVisible(visible);
    }
    
    public void render() {
        frame.repaint();
    }
    
    private void init() {
        // Create game window...
        frame = new JFrame();
        frame.setTitle("JavaTrafficSimulator");
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void paintComponent(final Graphics g) {
                Graphics2D g2d = null;
                try {
                    g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                    t = new AffineTransform();
                    // move to offset
                    t.translate(offset.getX(), offset.getY());
                    // transformation (scroll and zoom)
                    t.translate(zoomCenter.getX(), zoomCenter.getY());
                    // zoom
                    t.scale(zoom, zoom);
                    // move to the zoom center
                    t.translate(-zoomCenter.getX(), -zoomCenter.getY());
                    if (App.DEBUG) {
                        g2d.setColor(Color.GREEN);
                        g2d.drawLine(0, 0, (int) offset.getX(), (int) offset.getY());
                        g2d.drawOval((int) zoomCenter.getX() - 10, (int) zoomCenter.getY() - 10, 20, 20);
                    }
                    g2d.setTransform(t);
                    if (App.DEBUG) {
                        g2d.setColor(Color.RED);
                        g2d.drawLine(-20, 0, 20, 0);
                        g2d.drawLine(0, -20, 0, 20);
                        g2d.drawOval((int) zoomCenter.getX() - 10, (int) zoomCenter.getY() - 10, 20, 20);
                    }
                    // center on screen
                    g2d.transform(AffineTransform.getTranslateInstance(windoww / 2, windowh / 2));
                    /*
                     * Hotfix: affine transformation y = -y. We've to do this
                     * because the coordinates imported expect a origin in the
                     * left bottom corner. But java does stuff different.
                     * Therefore the origin is in the left upper corner. As a
                     * result all the agents are driving on the wrong side.
                     * TODO: Change importer so that the y coordinates get
                     * transformed.
                     */
                    g2d.transform(AffineTransform.getScaleInstance(1, -1));
                    renderable.render(g2d);
                    // Let the OS have a little time...
                    Thread.yield();
                } finally {
                    if (g2d != null) {
                        g2d.dispose();
                    }
                }
            }
        };
        final MouseAdapter adapter = new MouseAdapter() {
            
            private boolean isDown            = false;
            private Point   mousePressedPoint = new Point();
            
            @Override
            public void mousePressed(final MouseEvent e) {
                mousePressedPoint = e.getPoint();
                isDown = true;
            }
            
            @Override
            public void mouseReleased(final MouseEvent e) {
                isDown = false;
            }
            
            @Override
            public void mouseDragged(final MouseEvent e) {
                if (isDown) {
                    double deltaX = e.getX() - mousePressedPoint.getX();
                    double deltaY = e.getY() - mousePressedPoint.getY();
                    offset.setLocation(offset.getX() + deltaX, offset.getY() + deltaY);
                    mousePressedPoint = e.getPoint();
                }
            }
        };
        frame.setContentPane(panel);
        // Add canvas to game window...
        // frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        // app.setLocationRelativeTo(null);
        frame.addComponentListener(new ComponentAdapter() {
            
            @Override
            public void componentResized(final ComponentEvent e) {
                windoww = frame.getWidth();
                windowh = frame.getHeight();
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyReleased(final KeyEvent e) {
                final int keyCode = e.getKeyCode();
                keys.remove(keyCode);
            }
            
            @Override
            public void keyPressed(final KeyEvent e) {
                final int keyCode = e.getKeyCode();
                keys.add(keyCode);
            }
        });
        panel.addMouseListener(adapter);
        panel.addMouseMotionListener(adapter);
        panel.addMouseWheelListener(new MouseWheelListener() {
            
            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                final Point mousePoint = e.getPoint();
                // set zoom center relative to no zoom
                final int rotation = e.getWheelRotation();
                double zoomDelta = 0;
                if (rotation < 0) {
                    // zoom in
                    zoomDelta = ZOOM_DELTA;
                } else if (rotation > 0 && zoom >= ZOOM_DELTA) {
                    // zoom out
                    zoomDelta = -ZOOM_DELTA;
                }
                // change zoom
                zoom += zoomDelta;
                try {
                    final Point mousePointInverse = new Point();
                    t.inverseTransform(mousePoint, mousePointInverse);
                    zoomCenter.setLocation(mousePointInverse.getX(), mousePointInverse.getY());
                    offset.setLocation(mousePoint.getX() - mousePointInverse.getX(), mousePoint.getY() - mousePointInverse.getY());
                } catch (NoninvertibleTransformException e1) {
                }
            }
        });
        /*
         * TODO: don't uncomment this because if you do so the coordinate origin
         * will be strange!
         */
        // initMenu();
    }
    
    private void initMenu() {
        final JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(x -> {
            System.exit(0);
        });
        menu.add(menuItem);
        menu = new JMenu("Simulation");
        menu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(menu);
        menuItem = new JMenuItem("Start");
        menuItem.addActionListener(x -> {
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Pause");
        menuItem.addActionListener(x -> {
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Stop");
        menuItem.addActionListener(x -> {
        });
        menu.add(menuItem);
        frame.setJMenuBar(menuBar);
    }
}
