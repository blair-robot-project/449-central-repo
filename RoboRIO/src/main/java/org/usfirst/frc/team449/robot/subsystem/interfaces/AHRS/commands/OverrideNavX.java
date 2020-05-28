package org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

/** Set whether or not to override the AHRS. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OverrideNavX extends InstantCommand {

  /** Whether or not to override the AHRS. */
  private final boolean override;

  /** The subsystem to execute this command on. */
  @NotNull @Log.Exclude private final SubsystemAHRS subsystem;

  /**
   * Default constructor.
   *
   * @param subsystem The subsystem to execute this command on
   * @param override Whether or not to override the AHRS.
   */
  @JsonCreator
  public OverrideNavX(
      @NotNull @JsonProperty(required = true) SubsystemAHRS subsystem,
      @JsonProperty(required = true) boolean override) {
    this.override = override;
    this.subsystem = subsystem;
  }

  /** Log when this command is initialized */
  @Override
  public void initialize() {
    Shuffleboard.addEventMarker(
        "OverrideNavX init", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("OverrideNavX init", this.getClass());
  }

  /** Set whether or not we're overriding the AHRS */
  @Override
  public void execute() {
    subsystem.setOverrideGyro(override);
  }

  /** Log when this command ends */
  @Override
  public void end(boolean interrupt) {
    if (interrupt) {
      Shuffleboard.addEventMarker(
          "OverrideNavX Interrupted!", this.getClass().getSimpleName(), EventImportance.kNormal);
    }
    Shuffleboard.addEventMarker(
        "OverrideNavX end", this.getClass().getSimpleName(), EventImportance.kNormal);
  }
}
