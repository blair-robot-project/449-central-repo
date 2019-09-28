package org.usfirst.frc.team449.robot.commands.multiSubsystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands.PIDAngleCommand;
import org.usfirst.frc.team449.robot.subsystem.interfaces.conditional.SubsystemConditional;

/**
 * Drive straight using the NavX gyro stabilization until a {@link SubsystemConditional}'s condition is met.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveStraightUntilConditionMet<T extends Subsystem & DriveUnidirectional & SubsystemAHRS> extends PIDAngleCommand {

    /**
     * The drive subsystem this command controls.
     */
    @NotNull
    private final T drive;

    /**
     * The conditional subsystem this command uses.
     */
    @NotNull
    private final SubsystemConditional subsystemConditional;

    /**
     * The velocity for the drive to go at, on [-1, 1].
     */
    private final double driveVelocity;

    /**
     * The output of the PID loop. Field to avoid garbage collection.
     */
    private double output;

    /**
     * Default constructor.
     *
     * @param onTargetBuffer       A buffer timer for having the loop be on target before it stops running. Can be null
     *                             for no buffer.
     * @param absoluteTolerance    The maximum number of degrees off from the target at which we can be considered
     *                             within tolerance.
     * @param minimumOutput        The minimum output of the loop. Defaults to zero.
     * @param maximumOutput        The maximum output of the loop. Can be null, and if it is, no maximum output is
     *                             used.
     * @param loopTimeMillis       The time, in milliseconds, between each loop iteration. Defaults to 20 ms.
     * @param deadband             The deadband around the setpoint, in degrees, within which no output is given to the
     *                             motors. Defaults to zero.
     * @param inverted             Whether the loop is inverted. Defaults to false.
     * @param kP                   Proportional gain. Defaults to zero.
     * @param kI                   Integral gain. Defaults to zero.
     * @param kD                   Derivative gain. Defaults to zero.
     * @param drive                The drive subsystem this command controls.
     * @param subsystemConditional The conditional subsystem this command uses.
     * @param driveVelocity        The velocity for the drive to go at, on [-1, 1].
     */
    @JsonCreator
    public DriveStraightUntilConditionMet(@JsonProperty(required = true) double absoluteTolerance,
                                          @Nullable BufferTimer onTargetBuffer,
                                          double minimumOutput, @Nullable Double maximumOutput,
                                          @Nullable Integer loopTimeMillis,
                                          double deadband,
                                          boolean inverted,
                                          int kP,
                                          int kI,
                                          int kD,
                                          @NotNull @JsonProperty(required = true) T drive,
                                          @NotNull @JsonProperty(required = true) SubsystemConditional subsystemConditional,
                                          @JsonProperty(required = true) double driveVelocity) {
        super(absoluteTolerance, onTargetBuffer, minimumOutput, maximumOutput, loopTimeMillis, deadband, inverted,
                drive, kP, kI, kD);
        this.drive = drive;
        this.subsystemConditional = subsystemConditional;
        this.driveVelocity = driveVelocity;
        requires(this.drive);
    }

    /**
     * Log and set the setpoint of the angle PID to the current heading.
     */
    @Override
    protected void initialize() {
        Logger.addEvent("DriveStraightUntilConditionMet init", this.getClass());
        this.getPIDController().setSetpoint(this.returnPIDInput());
        this.getPIDController().enable();
    }

    /**
     * Give output to the drive based on the output of the PID loop.
     */
    @Override
    public void execute() {
        //Process the PID output with deadband, minimum output, etc.
        output = processPIDOutput(this.getPIDController().get());

        drive.setOutput(driveVelocity - output, driveVelocity + output);
    }

    /**
     * End when the condition is met.
     *
     * @return True if the condition is met, false otherwise.
     */
    @Override
    protected boolean isFinished() {
        return subsystemConditional.isConditionTrue();
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("DriveStraightUntilConditionMet end", this.getClass());
        this.getPIDController().disable();
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("DriveStraightUntilConditionMet interrupted!", this.getClass());
        this.getPIDController().disable();
    }
}
