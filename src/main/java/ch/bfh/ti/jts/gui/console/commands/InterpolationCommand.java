package ch.bfh.ti.jts.gui.console.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import ch.bfh.ti.jts.simulation.Simulation;

@Parameters(commandDescription = "Set animation interpolation setting")
public class InterpolationCommand extends Command {
    
    @Parameter(names = { "-off", "-value" }, description = "Disable interpolation")
    private boolean off = false;
    
    @Override
    public String execute(Object executor) {
        final Simulation simulation = (Simulation) executor;
        if (!off) {
            simulation.setInterpolateWallClockSimulationState(true);
            return "Interpolation on";
        } else {
            simulation.setInterpolateWallClockSimulationState(false);
            off = false; // reset value
            return "Interpolation off";
        }        
    }
    
    @Override
    public String getName() {
        return "interpolation";
    }
    
    @Override
    public Class<?> getTargetType() {
        return Simulation.class;
    }
}
