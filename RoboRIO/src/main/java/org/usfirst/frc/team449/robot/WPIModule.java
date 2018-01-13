package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.mixIn.CommandMixIn;

public class WPIModule extends SimpleModule {

    public WPIModule() {
        super("WPIModule");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(Command.class, CommandMixIn.class);
    }
}
