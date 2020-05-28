package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;

/**
 * A ConditionalCommand that uses a {@link edu.wpi.first.wpilibj.DigitalInput} for determining which
 * command to run.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ConditionalCommandDigitalInputBased extends ConditionalCommand {

  /**
   * Default constructor
   *
   * @param onTrue The Command to execute if the digital input returns true.
   * @param onFalse The Command to execute if the digital input returns false. Can be null to not
   *     execute a command if the supplier is false.
   * @param digitalInput A digital input for determining which command to run.
   */
  @JsonCreator
  public ConditionalCommandDigitalInputBased(
      @NotNull @JsonProperty(required = true) Command onTrue,
      @Nullable Command onFalse,
      @NotNull @JsonProperty(required = true) MappedDigitalInput digitalInput) {
    super(onTrue, onFalse, digitalInput::get);
  }
}
