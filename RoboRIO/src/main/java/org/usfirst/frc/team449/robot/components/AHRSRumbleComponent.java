package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.rumbleable.Rumbleable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;
import org.usfirst.frc.team449.robot.other.Clock;

import java.util.List;

/** A component to rumble controllers based off the jerk measurements from an AHRS. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AHRSRumbleComponent implements Runnable {

  /** The NavX to get jerk measurements from. */
  @NotNull private final MappedAHRS ahrs;

  /** The things to rumble. */
  @NotNull private final List<Rumbleable> rumbleables;

  /** The minimum jerk that will trigger rumbling, in feet/second^3. */
  private final double minJerk;

  /**
   * The jerk, in feet/second^3, that's scaled to maximum rumble. All jerks of greater magnitude are
   * capped at 1.
   */
  private final double maxJerk;

  /** Whether the NavX Y-axis measures forwards-back jerk or left-right jerk. */
  private final boolean yIsFrontBack;

  /** Whether to invert the left-right jerk measurement. */
  private final boolean invertLeftRight;

  /**
   * Variables for the per-call rumble calculation representing the directional accelerations.
   * Fields to avoid garbage collection.
   */
  private double lastFrontBackAccel, lastLeftRightAccel;

  /**
   * Variables for the per-call rumble calculation representing the rumble outputs. Fields to avoid
   * garbage collection.
   */
  private double left, right;

  /** Variables for per-call acceleration calculation. Fields to avoid garbage collection. */
  private double frontBack, leftRight;

  /** The time at which the acceleration was last measured. */
  private long timeLastCalled;

  /**
   * Default constructor.
   *
   * @param ahrs The NavX to get jerk measurements from.
   * @param rumbleables The things to rumble.
   * @param minJerk The minimum jerk that will trigger rumbling, in feet/(sec^3).
   * @param maxJerk The jerk, in feet/(sec^3), that's scaled to maximum rumble. All jerks of greater
   *     magnitude are capped at 1.
   * @param yIsFrontBack Whether the NavX Y-axis measures forwards-back jerk or left-right jerk.
   *     Defaults to false.
   * @param invertLeftRight Whether to invert the left-right jerk measurement. Defaults to false.
   */
  @JsonCreator
  public AHRSRumbleComponent(
      @NotNull @JsonProperty(required = true) final MappedAHRS ahrs,
      @NotNull @JsonProperty(required = true) final List<Rumbleable> rumbleables,
      @JsonProperty(required = true) final double minJerk,
      @JsonProperty(required = true) final double maxJerk,
      final boolean yIsFrontBack,
      final boolean invertLeftRight) {
    this.ahrs = ahrs;
    this.rumbleables = rumbleables;
    this.minJerk = minJerk;
    this.maxJerk = maxJerk;
    this.yIsFrontBack = yIsFrontBack;
    this.invertLeftRight = invertLeftRight;
    this.timeLastCalled = 0;
    this.lastFrontBackAccel = 0;
    this.lastLeftRightAccel = 0;
  }

  /** Read the NavX jerk data and rumble the joysticks based off of it. */
  @Override
  public void run() {
    //TODO Both branches of this if statement are the exact same!
    if (this.yIsFrontBack) {
      // Put an abs() here because we can't differentiate front vs back when rumbling, so we only
      // care about
      // magnitude.
      this.frontBack = Math.abs(this.ahrs.getYAccel());
      this.leftRight = this.ahrs.getXAccel() * (this.invertLeftRight ? -1 : 1);
    } else {
      this.frontBack = Math.abs(this.ahrs.getYAccel());
      this.leftRight = this.ahrs.getXAccel() * (this.invertLeftRight ? -1 : 1);
    }

    // Left is negative jerk, so we subtract it from left so that when we're going left, left is
    // bigger and vice
    // versa
    this.left =
        ((this.frontBack - this.lastFrontBackAccel) - (this.leftRight - this.lastLeftRightAccel))
            / (Clock.currentTimeMillis() - this.timeLastCalled);
    this.right =
        ((this.frontBack - this.lastFrontBackAccel) + (this.leftRight - this.lastLeftRightAccel))
            / (Clock.currentTimeMillis() - this.timeLastCalled);

    if (this.left > this.minJerk) {
      this.left = (this.left - this.minJerk) / this.maxJerk;
    } else {
      this.left = 0;
    }

    if (this.right > this.minJerk) {
      this.right = (this.right - this.minJerk) / this.maxJerk;
    } else {
      this.right = 0;
    }

    for (final Rumbleable rumbleable : this.rumbleables) {
      rumbleable.rumble(this.left, this.right);
    }

    this.lastLeftRightAccel = this.leftRight;
    this.lastFrontBackAccel = this.frontBack;
    this.timeLastCalled = Clock.currentTimeMillis();
  }
}
