package ch.bfh.ti.jts.console;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.bfh.ti.jts.data.Net;

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
    private final Queue<String> lines             = new LinkedList<String>();
    private StringBuffer        buffer            = new StringBuffer();
    private Net                 net;
    
    public BasicConsole() {
        this.font = new Font("Courier New", Font.PLAIN, 14);
    }
    
    public Net getNet() {
        return net;
    }
    
    protected void setFont(final Font font) {
        this.font = font;
    }
    
    @Override
    public int getRenderLayer() {
        // doesn't matter at the moment
        return Integer.MAX_VALUE;
    }
    
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(font);
        // render last 20 inputs
        int yoffset = 0;
        for (String line : lines) {
            g.drawString(line, POS_X, POS_Y + yoffset);
            yoffset += LINE_HIEGHT;
        }
        String output = PROMPT + buffer.toString();
        if (System.currentTimeMillis() % (1000 / CURSOR_BLINK_RATE) > (1000 / CURSOR_BLINK_RATE * 0.5)) {
            output += CURSOR;
        }
        g.drawString(output, POS_X, POS_Y + yoffset);
    }
    
    @Override
    public void setNet(Net net) {
        this.net = net;
    }
    
    @Override
    public void keyTyped(final char character) {
        if ((int) character >= 32 && (int) character <= 127) {
            writeChar(character);
        }
        if (character == (char) KeyEvent.VK_BACK_SPACE) {
            removeChar();
        }
        if (character == (char) KeyEvent.VK_ENTER) {
            pressEnter();
        }
    }
    
    private void writeChar(final char character) {
        buffer.append(character);
    }
    
    private void removeChar() {
        buffer.deleteCharAt(buffer.length() - 1);
    }
    
    private void pressEnter() {
        String line = buffer.toString();
        buffer.setLength(0);
        executeCommand(line);
    }
    
    @Override
    public void writeLine(final String line) {
        if (line.contains("\n")) {
            // multiple lines
            String[] helpLines = line.split("\n");
            for (String helpLine : helpLines) {
                writeLine(helpLine);
            }
        } else {
            // single line
            lines.add(line);
            if (lines.size() > MAX_LINES) {
                lines.remove();
            }
        }
    }
    
    @Override
    public void executeCommand(final String line) {
        writeLine(PROMPT + line);
        if (line.trim().length() > 0) {
            Logger.getGlobal().log(Level.INFO, "console input: " + line);
            parseCommand(line);
            writeLine("");
        }
    }
    
    protected abstract void parseCommand(final String line);
}
