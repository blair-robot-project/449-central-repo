package org.usfirst.frc.team449.robot.generalInterfaces.limelight.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.limelight.Limelight;

/**
 * Sets the limelight pipeline to the index provided. Following are (as of 2020) the pipelines and
 * their uses.
 * 0: DriverCamera camera for use by the driver.
 * 1: CloseDistance setting for close-range vision targets (in the trench in front of ctrl panel).
 * 2: FarDistance same but with 2x zoom for being farther away.
 * 3: N/A 4: N/A 5: N/A 6: N/A 7: N/A 8: N/A 9: N/A
 */
public class SetPipeline extends InstantCommand {

  /**
   * Default constructor
   *
   * @param limelight The limelight to set.
   * @param index The index to set the pipeline to when this command is run.
   */
  @JsonCreator
  public SetPipeline(@NotNull @JsonProperty(required = true) Limelight limelight,
                     int index) {
    super(() -> limelight.setPipeline(index), limelight);
  }
}
