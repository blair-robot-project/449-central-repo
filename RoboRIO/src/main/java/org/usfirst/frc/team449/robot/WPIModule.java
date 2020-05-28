package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.button.Button;
import org.usfirst.frc.team449.robot.mixIn.CommandGroupMixIn;
import org.usfirst.frc.team449.robot.mixIn.ConditionalCommandMixIn;
import org.usfirst.frc.team449.robot.mixIn.PrintCommandMixIn;
import org.usfirst.frc.team449.robot.mixIn.SubsystemMixIn;
import org.usfirst.frc.team449.robot.mixIn.UseCLASSIncludeWRAPPER_OBJECTMixIn;
import org.usfirst.frc.team449.robot.mixIn.WaitCommandMixIn;
import org.usfirst.frc.team449.robot.mixIn.WaitUntilCommandMixIn;

/**
 * A Jackson {@link com.fasterxml.jackson.databind.Module} for adding mix-in annotations to classes.
 */
public class WPIModule extends SimpleModule {

  /** Default constructor */
  public WPIModule() {
    super("WPIModule");
  }

  /**
   * Mixes in some mix-ins to the given context.
   *
   * @param context the context to set up
   */
  @Override
  public void setupModule(final SetupContext context) {
    super.setupModule(context);

    context.setMixInAnnotations(Subsystem.class, SubsystemMixIn.class);

    context.setMixInAnnotations(Command.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);

    context.setMixInAnnotations(WaitCommand.class, WaitCommandMixIn.class);
    context.setMixInAnnotations(WaitUntilCommand.class, WaitUntilCommandMixIn.class);
    context.setMixInAnnotations(PrintCommand.class, PrintCommandMixIn.class);
    context.setMixInAnnotations(ConditionalCommand.class, ConditionalCommandMixIn.class);

    // TODO Verify how this actually works (probably it's because Jackson ignores the constructor's
    // name and only looks at the signature).
    context.setMixInAnnotations(SequentialCommandGroup.class, CommandGroupMixIn.class);
    context.setMixInAnnotations(ParallelCommandGroup.class, CommandGroupMixIn.class);

    context.setMixInAnnotations(Button.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);
  }
}
