package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.oi.unidirectional.tank.OITank;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands.PIDAngleCommand;

/**
 * Drives straight using the NavX gyro to keep a constant alignment.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class NavXDriveStraight<T extends Subsystem & DriveUnidirectional & SubsystemAHRS> extends PIDAngleCommand {

    /**
     * The drive subsystem to give output to.
     */
    @NotNull
    protected final T subsystem;

    /**
     * The tank OI to get input from.
     */
    @NotNull
    private final OITank oi;

    /**
     * Whether to use the left joystick to drive straight.
     */
    private final boolean useLeft;

    /**
     * The output of the PID loop. Field to avoid garbage collection.
     */
    private double output;

    /**
     * Default constructor.
     *
     * @param onTargetBuffer    A buffer timer for having the loop be on target before it stops running. Can be null for
     *                          no buffer.
     * @param absoluteTolerance The maximum number of degrees off from the target at which we can be considered within
     *                          tolerance.
     * @param minimumOutput     The minimum output of the loop. Defaults to zero.
     * @param maximumOutput     The maximum output of the loop. Can be null, and if it is, no maximum output is used.
     * @param loopTimeMillis    The time, in milliseconds, between each loop iteration. Defaults to 20 ms.
     * @param deadband          The deadband around the setpoint, in degrees, within which no output is given to the
     *                          motors. Defaults to zero.
     * @param inverted          Whether the loop is inverted. Defaults to false.
     * @param kP                Proportional gain. Defaults to zero.
     * @param kI                Integral gain. Defaults to zero.
     * @param kD                Derivative gain. Defaults to zero.
     * @param subsystem         The drive to execute this command on.
     * @param oi                The tank OI to take input from.
     * @param useLeft           Which joystick to use to get the forward component to drive straight. True for left,
     *                          false for right.
     */
    @JsonCreator
    public NavXDriveStraight(@JsonProperty(required = true) double absoluteTolerance,
                             @Nullable BufferTimer onTargetBuffer,
                             double minimumOutput, @Nullable Double maximumOutput,
                             @Nullable Integer loopTimeMillis,
                             double deadband,
                             boolean inverted,
                             int kP,
                             int kI,
                             int kD,
                             @NotNull @JsonProperty(required = true) T subsystem,
                             @NotNull @JsonProperty(required = true) OITank oi,
                             @JsonProperty(required = true) boolean useLeft) {
        super(absoluteTolerance, onTargetBuffer, minimumOutput, maximumOutput, loopTimeMillis, deadband, inverted, subsystem, kP, kI, kD);
        this.oi = oi;
        this.subsystem = subsystem;
        this.useLeft = useLeft;
        //This is likely to need to interrupt the DefaultCommand and therefore should require its subsystem.
        requires(subsystem);
    }

    /**
     * Set the setpoint of the angle PID.
     */
    @Override
    protected void initialize() {
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

        //Set throttle to the specified stick.
        if (useLeft) {
            subsystem.setOutput(oi.getLeftRightOutputCached()[0] - output, oi.getLeftRightOutputCached()[1] + output);
        } else {
            subsystem.setOutput(oi.getLeftRightOutputCached()[0] - output, oi.getLeftRightOutputCached()[1] + output);
        }
    }

    /**
     * Never finishes.
     *
     * @return false
     */
    @Override
    protected boolean isFinished() {
        return false;
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("NavXDriveStraight end", this.getClass());
        this.getPIDController().disable();
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("NavXDriveStraight interrupted!", this.getClass());
        this.getPIDController().disable();
    }
}
