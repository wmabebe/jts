package ch.bfh.ti.jts.gui.console.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.bfh.ti.jts.data.Element;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Print information to elemnet")
public class InfoCommand extends Command {

    @Parameter(required = true, description = "Information about the given elements")
    private final List<Integer> elementId = new ArrayList<>();

    @Override
    public Optional<String> execute(final Object executor) {
        final Element element = (Element) executor;
        Optional<String> information = Optional.empty();
        if (elementId.contains(element.getId())) {
            information = Optional.of(element.toString());
        }
        return information;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public Class<?> getTargetType() {
        return Element.class;
    }
}
