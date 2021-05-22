package org.usfirst.frc.team449.robot.oi.unidirectional.tank;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.oblarg.oblog.annotations.Log;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;

/** A tank-style dual joystick OI. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public abstract class OITank implements OIUnidirectional {

  /** Cached left and right output. */
  private double[] leftRightOutputCached;

  /** Cached forwards and rotational output. */
  private double[] fwdRotOutputCached;

  /**
   * Get the throttle for the left side of the drive.
   *
   * @return percent of max speed for left motor cluster from [-1.0, 1.0]
   */
  @Log
  public abstract double getLeftThrottle();

  /**
   * Get the throttle for the right side of the drive.
   *
   * @return percent of max speed for right motor cluster from [-1.0, 1.0]
   */
  @Log
  public abstract double getRightThrottle();

  /**
   * The output to be given to the left and right sides of the drive.
   *
   * @return An array of length 2, where the 1st element is the output for the left and the second
   *     for the right, both from [-1, 1].
   */
  @Override
  @Log
  public double[] getLeftRightOutput() {
    return new double[] {getLeftThrottle(), getRightThrottle()};
  }

  /**
   * The cached output to be given to the left and right sides of the drive.
   *
   * @return An array of length 2, where the 1st element is the output for the left and the second
   *     for the right, both from [-1, 1].
   */
  @Override
  @Log
  public double[] getLeftRightOutputCached() {
    return leftRightOutputCached;
  }

  /**
   * The forwards and rotational movement given to the drive.
   *
   * @return An array of length 2, where the first element is the forwards output and the second is
   *     the rotational, both from [-1, 1]
   */
  @Override
  @Log
  public double[] getFwdRotOutput() {
    return new double[] {
      (getLeftThrottle() + getRightThrottle()) / 2., (getLeftThrottle() - getRightThrottle()) / 2.
    };
  }

  /**
   * The cached forwards and rotational movement given to the drive.
   *
   * @return An array of length 2, where the first element is the forwards output and the second is
   *     the rotational, both from [-1, 1]
   */
  @Override
  @Log
  public double[] getFwdRotOutputCached() {
    return fwdRotOutputCached;
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    leftRightOutputCached = getLeftRightOutput();
    fwdRotOutputCached = getFwdRotOutput();
  }

}
