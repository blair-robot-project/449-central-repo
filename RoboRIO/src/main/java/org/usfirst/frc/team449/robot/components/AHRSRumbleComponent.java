package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.rumbleable.Rumbleable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedRunnable;
import org.usfirst.frc.team449.robot.other.Clock;

import java.util.List;

/**
 * A component to rumble controllers based off the jerk measurements from an AHRS.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AHRSRumbleComponent implements MappedRunnable {

    /**
     * The NavX to get jerk measurements from.
     */
    @NotNull
    private final MappedAHRS ahrs;

    /**
     * The things to rumble.
     */
    @NotNull
    private final List<Rumbleable> rumbleables;

    /**
     * The minimum jerk that will trigger rumbling, in feet/second^3.
     */
    private final double minJerk;

    /**
     * The jerk, in feet/second^3, that's scaled to maximum rumble. All jerks of greater magnitude are capped at 1.
     */
    private final double maxJerk;

    /**
     * Whether the NavX Y-axis measures forwards-back jerk or left-right jerk.
     */
    private final boolean yIsFrontBack;

    /**
     * Whether to invert the left-right jerk measurement.
     */
    private final boolean invertLeftRight;

    /**
     * Variables for the per-call rumble calculation representing the directional accelerations. Fields to avoid garbage
     * collection.
     */
    private double lastFrontBackAccel, lastLeftRightAccel;

    /**
     * Variables for the per-call rumble calculation representing the rumble outputs. Fields to avoid garbage
     * collection.
     */
    private double left, right;

    /**
     * Variables for per-call acceleration calculation. Fields to avoid garbage collection.
     */
    private double frontBack, leftRight;

    /**
     * The time at which the acceleration was last measured.
     */
    private long timeLastCalled;

    /**
     * Default constructor.
     *
     * @param ahrs            The NavX to get jerk measurements from.
     * @param rumbleables     The things to rumble.
     * @param minJerk         The minimum jerk that will trigger rumbling, in feet/(sec^3).
     * @param maxJerk         The jerk, in feet/(sec^3), that's scaled to maximum rumble. All jerks of greater magnitude
     *                        are capped at 1.
     * @param yIsFrontBack    Whether the NavX Y-axis measures forwards-back jerk or left-right jerk. Defaults to
     *                        false.
     * @param invertLeftRight Whether to invert the left-right jerk measurement. Defaults to false.
     */
    @JsonCreator
    public AHRSRumbleComponent(@NotNull @JsonProperty(required = true) MappedAHRS ahrs,
                               @NotNull @JsonProperty(required = true) List<Rumbleable> rumbleables,
                               @JsonProperty(required = true) double minJerk,
                               @JsonProperty(required = true) double maxJerk,
                               boolean yIsFrontBack,
                               boolean invertLeftRight) {
        this.ahrs = ahrs;
        this.rumbleables = rumbleables;
        this.minJerk = minJerk;
        this.maxJerk = maxJerk;
        this.yIsFrontBack = yIsFrontBack;
        this.invertLeftRight = invertLeftRight;
        timeLastCalled = 0;
        lastFrontBackAccel = 0;
        lastLeftRightAccel = 0;
    }

    /**
     * Read the NavX jerk data and rumble the joysticks based off of it.
     */
    @Override
    public void run() {
        if (yIsFrontBack) {
            //Put an abs() here because we can't differentiate front vs back when rumbling, so we only care about magnitude.
            frontBack = Math.abs(ahrs.getYAccel());
            leftRight = ahrs.getXAccel() * (invertLeftRight ? -1 : 1);
        } else {
            frontBack = Math.abs(ahrs.getYAccel());
            leftRight = ahrs.getXAccel() * (invertLeftRight ? -1 : 1);
        }

        //Left is negative jerk, so we subtract it from left so that when we're going left, left is bigger and vice versa
        left = ((frontBack - lastFrontBackAccel) - (leftRight - lastLeftRightAccel)) / (Clock.currentTimeMillis() - timeLastCalled);
        right = ((frontBack - lastFrontBackAccel) + (leftRight - lastLeftRightAccel)) / (Clock.currentTimeMillis() - timeLastCalled);

        if (left > minJerk) {
            left = (left - minJerk) / maxJerk;
        } else {
            left = 0;
        }

        if (right > minJerk) {
            right = (right - minJerk) / maxJerk;
        } else {
            right = 0;
        }

        for (Rumbleable rumbleable : rumbleables) {
            rumbleable.rumble(left, right);
        }

        lastLeftRightAccel = leftRight;
        lastFrontBackAccel = frontBack;
        timeLastCalled = Clock.currentTimeMillis();
    }

}
