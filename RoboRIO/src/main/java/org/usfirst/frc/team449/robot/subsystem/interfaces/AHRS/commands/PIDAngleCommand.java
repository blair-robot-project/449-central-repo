package org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

/**
 * A command that uses a AHRS to turn to a certain angle.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class PIDAngleCommand extends PIDCommand {

    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    protected final SubsystemAHRS subsystem;

    /**
     * The minimum the robot should be able to output, to overcome friction.
     */
    private final double minimumOutput;

    /**
     * The range in which output is turned off to prevent "dancing" around the setpoint.
     */
    private final double deadband;

    /**
     * Whether or not the loop is inverted.
     */
    private final boolean inverted;

    /**
     * A buffer timer for having the loop be on target before it stops running. Can be null for no buffer.
     */
    @Nullable
    private final BufferTimer onTargetBuffer;

    /**
     * Default constructor.
     *
     * @param absoluteTolerance The maximum number of degrees off from the target at which we can be considered within
     *                          tolerance.
     * @param onTargetBuffer    A buffer timer for having the loop be on target before it stops running. Can be null for
     *                          no buffer.
     * @param minimumOutput     The minimum output of the loop. Defaults to zero.
     * @param maximumOutput     The maximum output of the loop. Can be null, and if it is, no maximum output is used.
     * @param loopTimeMillis    The time, in milliseconds, between each loop iteration. Defaults to 20 ms.
     * @param deadband          The deadband around the setpoint, in degrees, within which no output is given to the
     *                          motors. Defaults to zero.
     * @param inverted          Whether the loop is inverted. Defaults to false.
     * @param kP                Proportional gain. Defaults to zero.
     * @param kI                Integral gain. Defaults to zero.
     * @param kD                Derivative gain. Defaults to zero.
     * @param subsystem         The subsystem to execute this command on.
     */
    @JsonCreator
    public PIDAngleCommand(@JsonProperty(required = true) double absoluteTolerance,
                           @Nullable BufferTimer onTargetBuffer,
                           double minimumOutput, @Nullable Double maximumOutput,
                           @Nullable Integer loopTimeMillis,
                           double deadband,
                           boolean inverted,
                           @NotNull @JsonProperty(required = true) SubsystemAHRS subsystem,
                           double kP,
                           double kI,
                           double kD) {
        //Set P, I and D. I and D will normally be 0 if you're using cascading control, like you should be.
        super(kP, kI, kD, loopTimeMillis != null ? loopTimeMillis / 1000. : 20. / 1000.);
        this.subsystem = subsystem;

        //Navx reads from -180 to 180.
        setInputRange(-180, 180);

        //It's a circle, so it's continuous
        this.getPIDController().setContinuous(true);

        //Set the absolute tolerance to be considered on target within.
        this.getPIDController().setAbsoluteTolerance(absoluteTolerance);

        //This is how long we have to be within the tolerance band. Multiply by loop period for time in ms.
        this.onTargetBuffer = onTargetBuffer;

        //Minimum output, the smallest output it's possible to give. One-tenth of your drive's top speed is about
        // right.
        this.minimumOutput = minimumOutput;

        //This caps the output we can give. One way to set up closed-loop is to make P large and then use this to
        // prevent overshoot.
        if (maximumOutput != null) {
            this.getPIDController().setOutputRange(-maximumOutput, maximumOutput);
        }

        //Set a deadband around the setpoint, in degrees, within which don't move, to avoid "dancing"
        this.deadband = deadband;

        //Set whether or not to invert the loop.
        this.inverted = inverted;
    }

    /**
     * Process the output of the PID loop to account for minimum output and inversion.
     *
     * @param output The output from the WPILib angular PID loop.
     * @return The processed output, ready to be subtracted from the left side of the drive output and added to the
     * right side.
     */
    protected double processPIDOutput(double output) {
        //Set the output to the minimum if it's too small.
        if (output > 0 && output < minimumOutput) {
            output = minimumOutput;
        } else if (output < 0 && output > -minimumOutput) {
            output = -minimumOutput;
        }
        if (inverted) {
            output *= -1;
        }

        return output;
    }

    /**
     * Deadband the output of the PID loop.
     *
     * @param output The output from the WPILib angular PID loop.
     * @return That output after being deadbanded with the map-given deadband.
     */
    protected double deadbandOutput(double output) {
        return Math.abs(this.getPIDController().getError()) > deadband ? output : 0;
    }

    /**
     * Returns the input for the pid loop. <p> It returns the input for the pid loop, so if this command was based off
     * of a gyro, then it should return the angle of the gyro </p> <p> All subclasses of {@link PIDCommand} must
     * override this method. </p> <p> This method will be called in a different thread then the {@link Scheduler}
     * thread. </p>
     *
     * @return the value the pid loop should use as input
     */
    @Override
    protected double returnPIDInput() {
        return subsystem.getHeadingCached();
    }

    /**
     * Whether or not the loop is on target. Use this instead of {@link edu.wpi.first.wpilibj.PIDController}'s
     * onTarget.
     *
     * @return True if on target, false otherwise.
     */
    protected boolean onTarget() {
        if (onTargetBuffer == null) {
            return this.getPIDController().onTarget();
        } else {
            return onTargetBuffer.get(this.getPIDController().onTarget());
        }
    }

    /**
     * Do nothing in the usePIDOutput thread to keep code clean.
     *
     * @param output The output of the PID loop.
     */
    @Override
    protected void usePIDOutput(double output) {
        //Do nothing
    }
}
