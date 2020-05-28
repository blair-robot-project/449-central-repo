package org.usfirst.frc.team449.robot.commands.limelight;

import com.fasterxml.jackson.annotation.JsonCreator;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.usfirst.frc.team449.robot.components.limelight.LimelightComponent;

/**
 * Sets the limelight pipeline to the index provided Following are (as of 2020) the pipelines and
 * their uses 0: DriverCamera camera for use by the driver 1: CloseDistance setting for close-range
 * vision targets (in the trench in front of ctrl panel) 2: FarDistance same but with 2x zoom for
 * being farther away 3: N/A 4: N/A 5: N/A 6: N/A 7: N/A 8: N/A 9: N/A
 */
public class SetPipeline extends InstantCommand {

  /**
   * Default constructor
   *
   * @param index the index to set the pipeline to when this command is run
   */
  @JsonCreator
  public SetPipeline(int index) {
    super(() -> LimelightComponent.setPipeline(index), null);
  }
}
