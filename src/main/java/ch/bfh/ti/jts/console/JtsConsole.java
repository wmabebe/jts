package ch.bfh.ti.jts.console;

public class JtsConsole extends BasicConsole {
    
    @Override
    protected void parseCommand(String line) {
        if (line.equals("test")) {
            writeLine("yeah.. do it!");
        } else {
            writeLine("error: invalid command \"" + line + "\"");
        }
    }
}
