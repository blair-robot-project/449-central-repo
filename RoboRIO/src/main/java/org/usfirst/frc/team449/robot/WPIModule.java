package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.mixIn.CommandMixIn;
import org.usfirst.frc.team449.robot.mixIn.SubsystemMixIn;

/**
 * A Jackson {@link com.fasterxml.jackson.databind.Module} for adding mix-in annotations to some WPI classes.
 */
public class WPIModule extends SimpleModule {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2317468877178800440L;

	/**
     * Default constructor
     */
    public WPIModule() {
        super("WPIModule");
    }

    /**
     * Mixes in some mix-ins to the given context.
     *
     * @param context the context to set up
     */
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.setMixInAnnotations(Command.class, CommandMixIn.class);
        context.setMixInAnnotations(Subsystem.class, SubsystemMixIn.class);
    }
}
