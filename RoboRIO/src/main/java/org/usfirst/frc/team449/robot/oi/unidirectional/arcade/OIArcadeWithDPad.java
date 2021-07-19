package org.usfirst.frc.team449.robot.oi.unidirectional.arcade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.Polynomial;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/** An arcade OI with an option to use the D-pad for turning. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OIArcadeWithDPad extends OIArcade {

  /**
   * How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the
   * turning stick that much of the way
   */
  private final double dPadShift;

  /** The throttle wrapper for the stick controlling turning velocity */
  @NotNull private final Throttle rotThrottle;

  /** The throttle wrapper for the stick controlling linear velocity */
  @NotNull private final Throttle fwdThrottle;

  /** The controller with the D-pad. Can be null if not using D-pad. */
  @Nullable private final Joystick gamepad;

  /**
   * The polynomial to scale the forwards throttle output by before using it to scale the rotational
   * throttle. Can be null, and if it is, rotational throttle is not scaled by forwards throttle.
   */
  @Nullable private final Polynomial scaleRotByFwdPoly;

  /** The scalar that scales the rotational throttle while turning in place. */
  private final double turnInPlaceRotScale;

  /** Forwards output. Field to avoid garbage collection. */
  private double fwd;

  /**
   * Default constructor
   *
   * @param gamepad The gamepad containing the joysticks and buttons. Can be null if not using the
   *     D-pad.
   * @param rotThrottle The throttle for rotating the robot.
   * @param fwdThrottle The throttle for driving the robot straight.
   * @param invertDPad Whether or not to invert the D-pad. Defaults to false.
   * @param dPadShift How fast the dPad should turn the robot, on [0, 1]. Defaults to 0.
   * @param scaleRotByFwdPoly The polynomial to scale the forwards throttle output by before using
   *     it to scale the rotational throttle. Can be null, and if it is, rotational throttle is not
   *     scaled by forwards throttle.
   * @param turnInPlaceRotScale The scalar that scales the rotational throttle while turning in
   *     place.
   * @param rescaleOutputs Whether or not to scale the left and right outputs so the max output is
   *     1. Defaults to false.
   */
  @JsonCreator
  public OIArcadeWithDPad(
      @NotNull @JsonProperty(required = true) Throttle rotThrottle,
      @NotNull @JsonProperty(required = true) Throttle fwdThrottle,
      double dPadShift,
      boolean invertDPad,
      @Nullable MappedJoystick gamepad,
      @Nullable Polynomial scaleRotByFwdPoly,
      @JsonProperty(required = true) double turnInPlaceRotScale,
      boolean rescaleOutputs) {
    super(rescaleOutputs);
    this.dPadShift = (invertDPad ? -1 : 1) * dPadShift;
    this.rotThrottle = rotThrottle;
    this.fwdThrottle = fwdThrottle;
    this.gamepad = gamepad;
    this.scaleRotByFwdPoly = scaleRotByFwdPoly;
    this.turnInPlaceRotScale = turnInPlaceRotScale;
  }

  /**
   * The forwards and rotational movement given to the drive.
   *
   * @return An array of length 2, where the first element is the forwards output and the second is
   *     the rotational, both from [-1, 1]
   */
  @Override
  public double[] getFwdRotOutput() {
    fwd = fwdThrottle.getValue();

    // If the gamepad is being pushed to the left or right
    if (gamepad != null && !(gamepad.getPOV() == -1 || gamepad.getPOV() % 180 == 0)) {
      // Output the shift value
      return new double[] {fwd, gamepad.getPOV() < 180 ? dPadShift : -dPadShift};
    } else if (fwd == 0) { // Turning in place
      return new double[] {fwd, rotThrottle.getValue() * turnInPlaceRotScale};
    } else if (scaleRotByFwdPoly != null) { // If we're using Cheezy Drive
      return new double[] {
        fwd, rotThrottle.getValue() * scaleRotByFwdPoly.applyAsDouble(Math.abs(fwd))
      };
    } else { // Plain and simple
      return new double[] {fwd, rotThrottle.getValue()};
    }
  }
}
