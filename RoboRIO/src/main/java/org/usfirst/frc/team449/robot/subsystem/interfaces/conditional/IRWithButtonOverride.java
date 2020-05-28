package org.usfirst.frc.team449.robot.subsystem.interfaces.conditional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.oi.buttons.SimpleButton;

/**
 * A subsystem which returns true when either the IR sensor returns true or a manual override button
 * is pressed.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IRWithButtonOverride implements SubsystemConditional {

  /** The IR sensor this subsystem uses. */
  @NotNull private final MappedDigitalInput infraredSensor;

  /** Manual override button this subsystem uses. */
  @NotNull private final SimpleButton button;

  /** Cached value for whether the condition is true. */
  private boolean isConditionTrueCached;

  /**
   * Default constructor
   *
   * @param infraredSensor The IR sensor this subsystem uses.
   * @param button Manual override button this subsystem uses.
   */
  @JsonCreator
  public IRWithButtonOverride(
      @JsonProperty(required = true) @NotNull MappedDigitalInput infraredSensor,
      @JsonProperty(required = true) @NotNull SimpleButton button) {
    this.infraredSensor = infraredSensor;
    this.button = button;
  }

  /** @return true if the condition is met, false otherwise */
  @Override
  public boolean isConditionTrue() {
    return infraredSensor.get() || button.get();
  }

  /** @return true if the condition was met when cached, false otherwise */
  @Override
  public boolean isConditionTrueCached() {
    return isConditionTrueCached;
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    isConditionTrueCached = isConditionTrue();
  }
}
