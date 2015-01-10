package ch.bfh.ti.jts.gui.console;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Basic console that manages text input.
 *
 * @author Enteee
 * @author winki
 */
public abstract class BasicConsole implements Console {
    
    private final static String PROMPT            = "jts>";
    private final static String CURSOR            = "█";
    /**
     * How many times per second the cursor blinks
     */
    private final static double CURSOR_BLINK_RATE = 1.0;
    private final static int    MAX_LINES         = 20;
    private final static int    LINE_HIEGHT       = 20;
    private final static int    POS_X             = 30;
    private final static int    POS_Y             = 40;
    private Font                font;
    private final Queue<String> lines             = new ConcurrentLinkedQueue<String>();
    private final StringBuffer  buffer            = new StringBuffer();
    
    public BasicConsole() {
        font = new Font("Courier New", Font.PLAIN, 14);
    }
    
    @Override
    public void executeCommand(final String line) {
        write(PROMPT + line);
        if (line.trim().length() > 0) {
            parseCommand(line);
        }
    }
    
    @Override
    public int getRenderLayer() {
        // doesn't matter at the moment
        return Integer.MAX_VALUE;
    }
    
    @Override
    public void keyTyped(final char character) {
        if (character >= 32 && character <= 127) {
            writeChar(character);
        }
        if (character == (char) KeyEvent.VK_BACK_SPACE) {
            removeChar();
        }
        if (character == (char) KeyEvent.VK_ENTER) {
            pressEnter();
        }
    }
    
    @Override
    public void stringTyped(String string) {
        writsString(string);
    }
    
    protected abstract void parseCommand(final String line);
    
    private void pressEnter() {
        final String line = buffer.toString();
        buffer.setLength(0);
        executeCommand(line);
    }
    
    private void removeChar() {
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(font);
        // render last 20 inputs
        int yoffset = 0;
        for (final String line : lines) {
            g.drawString(line, POS_X, POS_Y + yoffset);
            yoffset += LINE_HIEGHT;
        }
        String output = PROMPT + buffer.toString();
        if (System.currentTimeMillis() % (1000 / CURSOR_BLINK_RATE) > 1000 / CURSOR_BLINK_RATE * 0.5) {
            output += CURSOR;
        }
        g.drawString(output, POS_X, POS_Y + yoffset);
    }
    
    protected void setFont(final Font font) {
        this.font = font;
    }
    
    @Override
    public void write(final String text) {
        
        if (text.contains("\n")) {
            // multiple lines
            final String[] helpLines = text.split("\n");
            for (final String helpLine : helpLines) {
                write(helpLine);
            }
        } else {
            // single line
            lines.add(text);
            if (lines.size() > MAX_LINES) {
                lines.remove();
            }
        }
    }
    
    private void writeChar(final char character) {
        buffer.append(character);
    }
    
    private void writsString(final String string) {
        buffer.append(string);
    }
}
