package ch.bfh.ti.jts.gui.console.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.bfh.ti.jts.data.Element;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Remove elements")
public class RemoveCommand extends Command {

    @Parameter(description = "Ids of the elements to remove", required = true)
    private final List<Integer> elementIds = new ArrayList<>();

    @Override
    public Optional<String> execute(final Object executor) {
        final Element element = (Element) executor;
        Optional<String> message = Optional.empty();
        if (elementIds.contains(element.getId())) {
            element.remove();
            message = Optional.of(String.format("%s removed", element));
        }
        return message;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public Class<?> getTargetType() {
        return Element.class;
    }
}
