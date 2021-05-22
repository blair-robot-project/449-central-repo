package org.usfirst.frc.team449.robot.subsystem.solenoid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;

/** A simple SubsystemSolenoid.java. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SolenoidSimple extends SubsystemBase implements SubsystemSolenoid, Loggable {

  /** Piston for pushing gears */
  @NotNull private final DoubleSolenoid piston;

  /** The piston's current position */
  @NotNull private DoubleSolenoid.Value pistonPos = DoubleSolenoid.Value.kOff;

  /**
   * Default constructor
   *
   * @param piston The piston that comprises this subsystem.
   */
  @JsonCreator
  public SolenoidSimple(@NotNull @JsonProperty(required = true) final DoubleSolenoid piston) {
    this.piston = piston;
  }

  /** @param value The position to set the solenoid to. */
  @Override
  public void setSolenoid(@NotNull final DoubleSolenoid.Value value) {
    this.piston.set(value);
    this.pistonPos = value;
  }

  /** @return the current position of the solenoid. */
  @NotNull
  @Override
  @Log.ToString
  public DoubleSolenoid.Value getSolenoidPosition() {
    return this.pistonPos;
  }
}
