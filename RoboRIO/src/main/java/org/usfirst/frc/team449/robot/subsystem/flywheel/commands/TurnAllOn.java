package org.usfirst.frc.team449.robot.subsystem.flywheel.commands; // package
                                                                   // org.usfirst.frc.team449.robot.subsystems.flywheels.commands;
//
// import com.fasterxml.jackson.annotation.JsonCreator;
// import com.fasterxml.jackson.annotation.JsonIdentityInfo;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.fasterxml.jackson.annotation.ObjectIdGenerators;
// import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
// import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import io.github.oblarg.oblog.annotations.Log;
// import org.jetbrains.annotations.NotNull;
// import org.usfirst.frc.team449.robot.subsystems.flywheels.SubsystemFlywheel;
//
/// ** Turn on the flywheel and the feeder. */
// @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
// public class TurnAllOn extends InstantCommand {
//
//  /** The subsystem to execute this command on. */
//  @NotNull @Log.Exclude private final SubsystemFlywheel subsystem;
//
//  /**
//   * Default constructor
//   *
//   * @param subsystem The subsystem to execute this command on.
//   */
//  @JsonCreator
//  public TurnAllOn(@NotNull @JsonProperty(required = true) final SubsystemFlywheel subsystem) {
//    this.subsystem = subsystem;
//  }
//
//  /** Log when this command is initialized */
//  @Override
//  public void initialize() {
//    Shuffleboard.addEventMarker(
//        "TurnAllOn init.", this.getClass().getSimpleName(), EventImportance.kNormal);
//    // Logger.addEvent("TurnAllOn init.", this.getClass());
//  }
//
//  /** Turn on the flywheel and feeder. */
//  @Override
//  public void execute() {
//    subsystem.turnFlywheelOn();
//  }
//
//  /** Log when this command ends */
//  @Override
//  public void end(final boolean interrupted) {
//    if (interrupted) {
//      Shuffleboard.addEventMarker(
//          "TurnAllOn Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
//    }
//    Shuffleboard.addEventMarker(
//        "TurnAllOn end.", this.getClass().getSimpleName(), EventImportance.kNormal);
//  }
// }
