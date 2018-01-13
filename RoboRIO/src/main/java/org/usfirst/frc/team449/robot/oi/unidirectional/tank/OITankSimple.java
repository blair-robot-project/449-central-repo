package org.usfirst.frc.team449.robot.oi.unidirectional.tank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/**
 * A simple tank drive, where each joystick controls a side of the robot.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OITankSimple extends OITank {

    /**
     * The left throttle
     */
    @NotNull
    private final Throttle leftThrottle;

    /**
     * The right throttle
     */
    @NotNull
    private final Throttle rightThrottle;

    /**
     * The difference between left and right input within which the driver is considered to be trying to drive
     * straight.
     */
    private final double commandingStraightTolerance;

    /**
     * Default constructor
     *
     * @param leftThrottle                The throttle for controlling the velocity of the left side of the drive.
     * @param rightThrottle               The throttle for controlling the velocity of the right side of the drive.
     * @param commandingStraightTolerance The difference between left and right input within which the driver is
     *                                    considered to be trying to drive straight. Defaults to 0.
     */
    @JsonCreator
    public OITankSimple(@NotNull @JsonProperty(required = true) Throttle leftThrottle,
                        @NotNull @JsonProperty(required = true) Throttle rightThrottle,
                        double commandingStraightTolerance) {
        this.leftThrottle = leftThrottle;
        this.rightThrottle = rightThrottle;
        this.commandingStraightTolerance = commandingStraightTolerance;
    }

    /**
     * Get the throttle for the left side of the drive.
     *
     * @return percent of max speed for left motor cluster from [-1.0, 1.0]
     */
    @Override
    public double getLeftThrottle() {
        //If the driver is trying to drive straight, use the average of the two sticks.
        if (commandingStraight()) {
            return (leftThrottle.getValue() + rightThrottle.getValue()) / 2.;
        }
        return leftThrottle.getValue();
    }

    /**
     * Get the throttle for the right side of the drive.
     *
     * @return percent of max speed for right motor cluster from [-1.0, 1.0]
     */
    @Override
    public double getRightThrottle() {
        //If the driver is trying to drive straight, use the average of the two sticks.
        if (commandingStraight()) {
            return (leftThrottle.getValue() + rightThrottle.getValue()) / 2.;
        }
        return rightThrottle.getValue();
    }

    /**
     * Whether the driver is trying to drive straight.
     *
     * @return True if the driver is trying to drive straight, false otherwise.
     */
    @Override
    public boolean commandingStraight() {
        return Math.abs(getLeftOutputCached() - getRightOutputCached()) <= commandingStraightTolerance;
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "OI";
    }
}