package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.Button;
import org.usfirst.frc.team449.robot.mixIn.*;

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

    context.setMixInAnnotations(DoubleSolenoid.class, DoubleSolenoidMixIn.class);

    context.setMixInAnnotations(Command.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);
    context.setMixInAnnotations(CommandBase.class, CommandBaseMixIn.class);

    // TODO Verify how this actually works (probably it's because Jackson ignores the constructor's
    //   name and only looks at the signature).
    context.setMixInAnnotations(SequentialCommandGroup.class, CommandGroupMixIn.class);
    context.setMixInAnnotations(ParallelCommandGroup.class, CommandGroupMixIn.class);

    context.setMixInAnnotations(WaitCommand.class, WaitCommandMixIn.class);
    context.setMixInAnnotations(WaitUntilCommand.class, WaitUntilCommandMixIn.class);
    context.setMixInAnnotations(PrintCommand.class, PrintCommandMixIn.class);
    context.setMixInAnnotations(ConditionalCommand.class, ConditionalCommandMixIn.class);
    context.setMixInAnnotations(PerpetualCommand.class, PerpetualCommandMixIn.class);

    context.setMixInAnnotations(Button.class, UseCLASSIncludeWRAPPER_OBJECTMixIn.class);
  }
}
