package org.usfirst.frc.team449.robot.subsystem.solenoid.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.solenoid.SolenoidSimple;

/** Sets the state of a provided solenoid to the correct state. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetSolenoidPose extends InstantCommand {

  /**
   * Default constructor
   *
   * @param subsystem The subsystem subsystem to run this command on.
   * @param value The value to set the subsystem to.
   */
  @JsonCreator
  public SetSolenoidPose(@NotNull @JsonProperty(required = true) final SolenoidSimple subsystem,
                         @NotNull @JsonProperty(required = true) final DoubleSolenoid.Value value) {
    super(() -> subsystem.setSolenoid(value), subsystem);
  }
}
