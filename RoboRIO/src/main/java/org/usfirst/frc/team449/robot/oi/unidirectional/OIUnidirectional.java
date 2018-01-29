package org.usfirst.frc.team449.robot.oi.unidirectional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.oi.OI;

/**
 * An OI to control a robot with a unidirectional drive that has a left and right side (e.g. not meccanum, swerve, or
 * holonomic)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface OIUnidirectional extends OI {

    /**
     * The output to be given to the left and right sides of the drive.
     *
     * @return An array of length 2, where the 1st element is the output for the left and the second for the right, both
     * from [-1, 1].
     */
    double[] getLeftRightOutput();

    /**
     * The cached output to be given to the left and right sides of the drive.
     *
     * @return An array of length 2, where the 1st element is the output for the left and the second for the right, both
     * from [-1, 1].
     */
    double[] getLeftRightOutputCached();

    /**
     * The forwards and rotational movement given to the drive.
     *
     * @return An array of length 2, where the first element is the forwards output and the second is the rotational,
     * both from [-1, 1]
     */
    double[] getFwdRotOutput();

    /**
     * The cached forwards and rotational movement given to the drive.
     *
     * @return An array of length 2, where the first element is the forwards output and the second is the rotational,
     * both from [-1, 1]
     */
    double[] getFwdRotOutputCached();

    /**
     * Whether the driver is trying to drive straight.
     *
     * @return True if the driver is trying to drive straight, false otherwise.
     */
    boolean commandingStraight();
}
