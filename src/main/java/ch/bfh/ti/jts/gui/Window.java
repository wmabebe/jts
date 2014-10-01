package ch.bfh.ti.jts.gui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Window {
    
    private JFrame             frame;
    private JPanel             panel;
    private int                windoww      = 1000;
    private int                windowh      = 600;
    private final double       offsethdelta = 0.05;
    private double             offsetx      = 0;
    private double             offsety      = 0;
    private double             offseth      = 0.5;
    private final Set<Integer> keys         = new HashSet<Integer>();
    private final Renderable   renderable;
    
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
    
    @SuppressWarnings("serial")
    private void init() {
        // Create game window...
        frame = new JFrame();
        frame.setTitle("JavaTrafficSimulator");
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel() {
            
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            
            @Override
            public void paintComponent(final Graphics g) {
                Graphics2D g2d = null;
                try {
                    g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                    // transformation (scroll and zoom)
                    final AffineTransform t = new AffineTransform();
                    t.translate(windoww / 2, windowh / 2);
                    t.scale(1 / offseth, 1 / offseth);
                    t.translate(offsetx * offseth, offsety * offseth);
                    g2d.setTransform(t);
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
        final MouseAdapter adapter = new MouseAdapter() {
            
            boolean isDown = false;
            int     x      = 0;
            int     y      = 0;
            
            @Override
            public void mousePressed(final MouseEvent e) {
                x = e.getX();
                y = e.getY();
                isDown = true;
            }
            
            @Override
            public void mouseReleased(final MouseEvent e) {
                isDown = false;
            }
            
            @Override
            public void mouseDragged(final MouseEvent e) {
                if (isDown) {
                    offsetx += e.getX() - x;
                    offsety += e.getY() - y;
                    x = e.getX();
                    y = e.getY();
                }
            }
        };
        panel.addMouseListener(adapter);
        panel.addMouseMotionListener(adapter);
        panel.addMouseWheelListener(new MouseWheelListener() {
            
            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                final int rotation = e.getWheelRotation();
                if (rotation < 0 && offseth >= offsethdelta) {
                    offseth -= offsethdelta;
                } else if (rotation > 0) {
                    offseth += offsethdelta;
                }
            }
        });
        initMenu();
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
