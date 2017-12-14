package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Clock;

import java.util.function.Consumer;

/**
 * A component class for autoshifting.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AutoshiftComponent {

    /**
     * The speed setpoint at the upshift break
     */
    private final double upshiftSpeed;

    /**
     * The speed setpoint at the downshift break
     */
    private final double downshiftSpeed;

    /**
     * The robot isn't eligible to shift again for this many milliseconds after upshifting.
     */
    private final long cooldownAfterUpshift;

    /**
     * The robot isn't eligible to shift again for this many milliseconds after downshifting.
     */
    private final long cooldownAfterDownshift;

    /**
     * BufferTimers for shifting that make it so all the other conditions to shift must be met for some amount of time
     * before shifting actually happens.
     */
    @Nullable
    private final BufferTimer upshiftDebouncer, downshiftDebouncer;

    /**
     * The forward velocity setpoint (on a 0-1 scale) below which we stay in low gear
     */
    private final double upshiftFwdThresh;

    /**
     * The time we last upshifted (milliseconds)
     */
    private long timeLastUpshifted;

    /**
     * The time we last downshifted (milliseconds)
     */
    private long timeLastDownshifted;

    /**
     * Whether it's okay to up or down shift. Fields to avoid garbage collection.
     */
    private boolean okayToUpshift, okayToDownshift;

    /**
     * Default constructor
     *
     * @param upshiftSpeed           The minimum speed both sides the drive must be going at to shift to high gear.
     * @param downshiftSpeed         The maximum speed both sides must be going at to shift to low gear.
     * @param upshiftDebouncer       Buffer timer for upshifting.
     * @param downshiftDebouncer     Buffer timer for downshifting.
     * @param cooldownAfterDownshift The minimum time, in seconds, between downshifting and then upshifting again.
     *                               Defaults to 0.
     * @param cooldownAfterUpshift   The minimum time, in seconds, between upshifting and then downshifting again.
     *                               Defaults to 0.
     * @param upshiftFwdThresh       The minimum amount the forward joystick must be pushed forward in order to upshift,
     *                               on [0, 1]. Defaults to 0.
     */
    @JsonCreator
    public AutoshiftComponent(@JsonProperty(required = true) double upshiftSpeed,
                              @JsonProperty(required = true) double downshiftSpeed,
                              @Nullable BufferTimer upshiftDebouncer,
                              @Nullable BufferTimer downshiftDebouncer,
                              double upshiftFwdThresh,
                              double cooldownAfterUpshift,
                              double cooldownAfterDownshift) {
        this.upshiftSpeed = upshiftSpeed;
        this.downshiftSpeed = downshiftSpeed;
        this.upshiftFwdThresh = upshiftFwdThresh;
        this.cooldownAfterUpshift = (long) (cooldownAfterUpshift * 1000.);
        this.cooldownAfterDownshift = (long) (cooldownAfterDownshift * 1000.);
        this.upshiftDebouncer = upshiftDebouncer;
        this.downshiftDebouncer = downshiftDebouncer;
    }

    /**
     * Determine whether the robot should downshift.
     *
     * @param forwardThrottle The forwards throttle, on [-1, 1].
     * @param leftVel         The velocity of the left side of the drive.
     * @param rightVel        The velocity of the right side of the drive.
     * @return True if the drive should downshift, false otherwise.
     */
    private boolean shouldDownshift(double forwardThrottle, double leftVel, double rightVel) {
        //We should shift if we're going slower than the downshift speed
        okayToDownshift = Math.max(Math.abs(leftVel), Math.abs(rightVel)) < downshiftSpeed;
        //Or if we're just turning in place.
        okayToDownshift = okayToDownshift || (forwardThrottle == 0);
        //Or commanding a low speed.
        okayToDownshift = okayToDownshift || (Math.abs(forwardThrottle) < upshiftFwdThresh);
        //But we can only shift if we're out of the cooldown period.
        okayToDownshift = okayToDownshift && Clock.currentTimeMillis() - timeLastUpshifted > cooldownAfterUpshift;

        if (downshiftDebouncer != null) {
            //We use a BufferTimer so we only shift if the conditions are met for a specific continuous interval.
            // This avoids brief blips causing shifting.
            okayToDownshift = downshiftDebouncer.get(okayToDownshift);
        }

        //Record the time if we do decide to shift.
        if (okayToDownshift) {
            timeLastDownshifted = Clock.currentTimeMillis();
        }
        return okayToDownshift;
    }

    /**
     * Determine whether the robot should upshift.
     *
     * @param forwardThrottle The forwards throttle, on [-1, 1].
     * @param leftVel         The velocity of the left side of the drive.
     * @param rightVel        The velocity of the right side of the drive.
     * @return True if the drive should upshift, false otherwise.
     */
    private boolean shouldUpshift(double forwardThrottle, double leftVel, double rightVel) {
        //We should shift if we're going faster than the upshift speed...
        okayToUpshift = Math.min(Math.abs(leftVel), Math.abs(rightVel)) > upshiftSpeed;
        //AND the driver's trying to go forward fast.
        okayToUpshift = okayToUpshift && Math.abs(forwardThrottle) > upshiftFwdThresh;
        //But we can only shift if we're out of the cooldown period.
        okayToUpshift = okayToUpshift && Clock.currentTimeMillis() - timeLastDownshifted > cooldownAfterDownshift;

        if (upshiftDebouncer != null) {
            //We use a BufferTimer so we only shift if the conditions are met for a specific continuous interval.
            // This avoids brief blips causing shifting.
            okayToUpshift = upshiftDebouncer.get(okayToUpshift);
        }

        if (okayToUpshift) {
            timeLastUpshifted = Clock.currentTimeMillis();
        }
        return okayToUpshift;
    }

    /**
     * Determine if the subsystem should shift, and if yes, do the shifting.
     *
     * @param forwardThrottle The forwards throttle, on [-1, 1].
     * @param leftVel         The velocity of the left side of the drive.
     * @param rightVel        The velocity of the right side of the drive.
     * @param shift           The function to actually shift gears.
     */
    public void autoshift(double forwardThrottle, double leftVel, double rightVel, Consumer<Integer> shift) {
        if (shouldDownshift(forwardThrottle, leftVel, rightVel)) {
            shift.accept(Shiftable.gear.LOW.getNumVal());
        } else if (shouldUpshift(forwardThrottle, leftVel, rightVel)) {
            shift.accept(Shiftable.gear.HIGH.getNumVal());
        }
    }
}
