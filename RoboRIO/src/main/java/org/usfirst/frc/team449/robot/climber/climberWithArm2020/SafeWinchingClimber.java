package org.usfirst.frc.team449.robot.climber.climberWithArm2020;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.subsystem.binaryMotor.SubsystemBinaryMotor;
import org.usfirst.frc.team449.robot.subsystem.solenoid.SubsystemSolenoid;

import static org.usfirst.frc.team449.robot.other.Util.getLogPrefix;

/**
 * Like {@link ClimberWinchingWithArm} with safety features (stuff needs to be enabled to move)
 *
 * @author Nathan
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SafeWinchingClimber extends SubsystemBase
    implements SubsystemClimberWithArm,
        SubsystemBinaryMotor,
        SubsystemSolenoid,
        Updatable,
        Loggable {
  private final ClimberCurrentLimited motorSubsystem;
  private final SubsystemSolenoid solenoidSubsystem;

  private final long extensionTimeMillis;
  @Log private boolean armIsExtending = false;
  @Log private long extensionStartTime = 0L;
  @Log private boolean enableArm = true;
  @Log private boolean reallySure = false;

  @JsonCreator
  public SafeWinchingClimber(
      @NotNull @JsonProperty(required = true) final ClimberCurrentLimited motorSubsystem,
      @NotNull @JsonProperty(required = true) final SubsystemSolenoid solenoidSubsystem,
      final long extensionTimeMillis) {
    this.motorSubsystem = motorSubsystem;
    this.solenoidSubsystem = solenoidSubsystem;
    this.extensionTimeMillis = extensionTimeMillis;
  }

  /** Raise arm only if it is enabled */
  @Override
  public void raise() {
    System.out.println(getLogPrefix(this) + "raise");

    if (this.enableArm) {
      this.setSolenoid(DoubleSolenoid.Value.kForward);
      this.extensionStartTime = Clock.currentTimeMillis();
    }
  }

  /** Lower arm, but only if it is enabled */
  @Override
  public void lower() {
    System.out.println(getLogPrefix(this) + "lower");

    if (this.enableArm) {
      this.setSolenoid(DoubleSolenoid.Value.kReverse);
    }
  }

  @Override
  public void off() {
    System.out.println(getLogPrefix(this) + "off");

    this.setSolenoid(DoubleSolenoid.Value.kOff);
    this.turnMotorOff();
  }

  @Override
  public void setSolenoid(@NotNull final DoubleSolenoid.Value value) {
    this.solenoidSubsystem.setSolenoid(value);
    if (value == DoubleSolenoid.Value.kForward) {
      this.armIsExtending = true;
    } else if (value == DoubleSolenoid.Value.kReverse) {
      this.armIsExtending = false;
    }

    this.reallySure = false;
  }

  @Override
  public @NotNull DoubleSolenoid.Value getSolenoidPosition() {
    return this.solenoidSubsystem.getSolenoidPosition();
  }

  /**
   * Move the winch if the arm is up. Has to be called twice (double button press) for it to work (I
   * think?)
   */
  @Override
  public void turnMotorOn() {
//    if (this.armIsUp()) {
//      if (!this.reallySure) {
//        this.reallySure = true;
//      } else {
//        this.enableArm = false;
//      }
//    }
    this.setSolenoid(DoubleSolenoid.Value.kReverse);
    Timer.delay(3.);
    this.motorSubsystem.turnMotorOn();
  }

  @Log
  private boolean armIsUp() {
    if (!this.armIsExtending) {
      return false;
    }
    return Clock.currentTimeMillis() >= this.extensionStartTime + this.extensionTimeMillis;
  }

  /** Turn off the winch */
  @Override
  public void turnMotorOff() {
    this.motorSubsystem.turnMotorOff();
    this.reallySure = false;
  }

  @Override
  @Log
  public boolean isMotorOn() {
    return this.motorSubsystem.isMotorOn();
  }

  @Override
  public void update() {
    if (this.motorSubsystem.isConditionTrueCached()) {
      this.motorSubsystem.turnMotorOff();
    }
  }

  //    /**
  //     * @return true if the condition is met, false otherwise
  //     */
  //    @Override
  //    public boolean isConditionTrue() {
  //        return this.motorSubsystem.isConditionTrue();
  //    }
  //
  //    /**
  //     * @return true if the condition was met when cached, false otherwise
  //     */
  //    @Override
  //    public boolean isConditionTrueCached() {
  //        return this.motorSubsystem.isConditionTrueCached();
  //    }
  //
  //    /**
  //     * Updates all cached values with current ones.
  //     */
  //    @Override
  //    public void update() {
  //        this.motorSubsystem.update();
  //    }
}
