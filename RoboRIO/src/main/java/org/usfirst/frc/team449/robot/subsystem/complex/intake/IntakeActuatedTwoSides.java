package org.usfirst.frc.team449.robot.subsystem.complex.intake;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.IntakeTwoSidesSimple;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.SubsystemIntakeTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/** An intake that goes up and down with a piston. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeActuatedTwoSides extends IntakeTwoSidesSimple
    implements SubsystemSolenoid, SubsystemIntakeTwoSides {

  /** The piston for actuating the intake. */
  private final DoubleSolenoid piston;
  /** The current position of the piston */
  private DoubleSolenoid.Value currentPistonPos;

  /**
   * Default constructor.
   *
   * @param piston The piston for actuating the intake.
   * @param leftMotor The left motor that this subsystem controls.
   * @param rightMotor The left motor that this subsystem controls.
   * @param inSlowVel The velocity for the motor to go at for the IN_SLOW {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use IN_SLOW.
   * @param inFastVel The velocity for the motor to go at for the IN_FAST {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use IN_FAST.
   * @param outSlowVel The velocity for the motor to go at for the OUT_SLOW {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use OUT_SLOW.
   * @param outFastVel The velocity for the motor to go at for the OUT_FAST {@link IntakeMode}, on
   *     [-1, 1]. Can be null to indicate that this intake doesn't have/use OUT_FAST.
   */
  @JsonCreator
  public IntakeActuatedTwoSides(
      @NotNull @JsonProperty(required = true) final MappedDoubleSolenoid piston,
      @NotNull @JsonProperty(required = true) final SimpleMotor leftMotor,
      @NotNull @JsonProperty(required = true) final SimpleMotor rightMotor,
      @Nullable final Double inSlowVel,
      @Nullable final Double inFastVel,
      @Nullable final Double outSlowVel,
      @Nullable final Double outFastVel) {
    super(leftMotor, rightMotor, inSlowVel, inFastVel, outSlowVel, outFastVel);
    this.piston = piston;
  }

  /** @param value The position to set the solenoid to. */
  @Override
  public void setSolenoid(@NotNull final DoubleSolenoid.Value value) {
    this.currentPistonPos = value;
    this.piston.set(value);
  }

  /** @return the current position of the solenoid. */
  @Override
  @NotNull
  public DoubleSolenoid.Value getSolenoidPosition() {
    return this.currentPistonPos;
  }
}
