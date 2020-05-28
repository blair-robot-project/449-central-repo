package org.usfirst.frc.team449.robot.subsystem.complex.intake;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.DigitalInput;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.subsystem.interfaces.conditional.SubsystemConditional;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.IntakeTwoSidesSimple;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.SubsystemIntakeTwoSides;

public class IntakeSensoredTwoSides extends IntakeTwoSidesSimple
    implements SubsystemIntakeTwoSides, SubsystemConditional {

  /** The sensor for detecting if there's something in the intake. */
  private final DigitalInput sensor;

  /** The state of the condition when {@link IntakeSensored#update()} was called. */
  private boolean cachedCondition;

  /**
   * Default constructor.
   *
   * @param sensor The sensor for detecting if there's something in the intake.
   * @param leftMotor The left motor that this subsystem controls.
   * @param rightMotor The left motor that this subsystem controls.
   * @param fastSpeed The speed to run the motor at going fast.
   * @param slowSpeed The speed to run the motor at going slow.
   */
  @JsonCreator
  public IntakeSensoredTwoSides(
      @NotNull @JsonProperty(required = true) MappedDigitalInput sensor,
      @NotNull @JsonProperty(required = true) SimpleMotor leftMotor,
      @NotNull @JsonProperty(required = true) SimpleMotor rightMotor,
      @JsonProperty(required = true) double fastSpeed,
      @JsonProperty(required = true) double slowSpeed) {
    super(leftMotor, rightMotor, slowSpeed, fastSpeed, -slowSpeed, -fastSpeed);
    this.sensor = sensor;
  }

  /** @return true if the condition is met, false otherwise */
  @Override
  public boolean isConditionTrue() {
    return sensor.get();
  }

  /** @return true if the condition was met when cached, false otherwise */
  @Override
  public boolean isConditionTrueCached() {
    return cachedCondition;
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    cachedCondition = isConditionTrue();
  }
}
