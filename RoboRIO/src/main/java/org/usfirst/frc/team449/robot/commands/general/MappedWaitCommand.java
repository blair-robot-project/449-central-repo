package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.WaitCommand;

/** A jackson-compatible wrapper on {@link WaitCommand}. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedWaitCommand extends WaitCommand {

  /**
   * Instantiates a {@link WaitCommand} with the given timeout.
   *
   * @param timeout the time the command takes to run (seconds)
   */
  @JsonCreator
  public MappedWaitCommand(@JsonProperty(required = true) double timeout) {
    super(timeout);
  }
}
