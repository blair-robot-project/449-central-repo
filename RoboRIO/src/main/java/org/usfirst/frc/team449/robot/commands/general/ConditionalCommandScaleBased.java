package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import org.jetbrains.annotations.NotNull;

/**
 * A ConditionalCommand that chooses which command to run based off of if the scale is on the left
 * or the right.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandScaleBased extends ConditionalCommand {

  /**
   * Default constructor.
   *
   * @param leftScale The Command to execute if the left side of the scale is our alliance color.
   * @param rightScale The Command to execute if the right side of the scale is our alliance color.
   */
  @JsonCreator
  public ConditionalCommandScaleBased(
      @NotNull @JsonProperty(required = true) Command leftScale,
      @NotNull @JsonProperty(required = true) Command rightScale) {
    // The BooleanSupplier returns true if m_onTrue should be run or false if m_onFalse should be
    // run.
    super(
        leftScale,
        rightScale,
        () -> DriverStation.getInstance().getGameSpecificMessage().charAt(1) == 'L');
  }
}
