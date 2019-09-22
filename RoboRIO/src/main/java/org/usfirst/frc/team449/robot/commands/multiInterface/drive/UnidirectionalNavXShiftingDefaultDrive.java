package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.AutoshiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.RampComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

/**
 * Drive with arcade drive setup, autoshift, and when the driver isn't turning, use a NavX to stabilize the robot's
 * alignment.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnidirectionalNavXShiftingDefaultDrive<T extends Subsystem & DriveUnidirectional & SubsystemAHRS & DriveShiftable> extends UnidirectionalNavXDefaultDrive<T> {

    /**
     * The drive to execute this command on.
     */
    @NotNull
    protected final T subsystem;

    /**
     * The helper object for autoshifting.
     */
    @NotNull
    protected final AutoshiftComponent autoshiftComponent;

    /**
     * The coefficient to multiply the loop output by in high gear.
     */
    private final double highGearAngularCoefficient;

    /**
     * PID loop coefficients.
     */
    private final double kP, kI, kD;

    /**
     * The gear the subsystem was in the last time execute() ran.
     */
    private int lastGear;

    /**
     * Default constructor
     *
     * @param onTargetBuffer              A buffer timer for having the loop be on target before it stops running. Can
     *                                    be null for no buffer.
     * @param absoluteTolerance           The maximum number of degrees off from the target at which we can be
     *                                    considered within tolerance.
     * @param minimumOutput               The minimum output of the loop. Defaults to zero.
     * @param maximumOutput               The maximum output of the loop. Can be null, and if it is, no maximum output
     *                                    is used.
     * @param loopTimeMillis              The time, in milliseconds, between each loop iteration. Defaults to 20 ms.
     * @param deadband                    The deadband around the setpoint, in degrees, within which no output is given
     *                                    to the motors. Defaults to zero.
     * @param maxAngularVelToEnterLoop    The maximum angular velocity, in degrees/sec, at which the loop will be
     *                                    entered. Defaults to 180.
     * @param inverted                    Whether the loop is inverted. Defaults to false.
     * @param kP                          Proportional gain. Defaults to zero.
     * @param kI                          Integral gain. Defaults to zero.
     * @param kD                          Derivative gain. Defaults to zero.
     * @param driveStraightLoopEntryTimer The buffer timer for starting to drive straight.
     * @param subsystem                   The drive to execute this command on.
     * @param oi                          The OI controlling the robot.
     * @param rampComponent               The acceleration-limiting ramp for the output to the drive. Defaults to no
     *                                    ramp.
     * @param autoshiftComponent          The helper object for autoshifting.
     * @param highGearAngularCoefficient  The coefficient to multiply the loop output by in high gear. Defaults to 1.
     */
    @JsonCreator
    public UnidirectionalNavXShiftingDefaultDrive(@JsonProperty(required = true) double absoluteTolerance,
                                                  @Nullable BufferTimer onTargetBuffer,
                                                  double minimumOutput, @Nullable Double maximumOutput,
                                                  @Nullable Integer loopTimeMillis,
                                                  double deadband,
                                                  @Nullable Double maxAngularVelToEnterLoop,
                                                  boolean inverted,
                                                  double kP,
                                                  double kI,
                                                  double kD,
                                                  @NotNull @JsonProperty(required = true) BufferTimer driveStraightLoopEntryTimer,
                                                  @NotNull @JsonProperty(required = true) T subsystem,
                                                  @NotNull @JsonProperty(required = true) OIUnidirectional oi,
                                                  @Nullable RampComponent rampComponent,
                                                  @NotNull @JsonProperty(required = true) AutoshiftComponent autoshiftComponent,
                                                  @Nullable Double highGearAngularCoefficient) {
        super(absoluteTolerance, onTargetBuffer, minimumOutput, maximumOutput, loopTimeMillis, deadband,
                maxAngularVelToEnterLoop,
                inverted, kP, kI, kD, driveStraightLoopEntryTimer, subsystem, oi, rampComponent);
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.autoshiftComponent = autoshiftComponent;
        this.subsystem = subsystem;
        this.highGearAngularCoefficient = highGearAngularCoefficient != null ? highGearAngularCoefficient : 1;
        this.lastGear = this.subsystem.getGear();
    }

    /**
     * Autoshift and decide whether or not we should be in free drive or straight drive
     */
    @Override
    public void execute() {
        //Auto-shifting
        if (!subsystem.getOverrideAutoshift()) {
            autoshiftComponent.autoshift(oi.getFwdRotOutputCached()[0], subsystem.getLeftVelCached(),
                    subsystem.getRightVelCached(), gear -> subsystem.setGear(gear));
        }

        //Gain schedule the loop if we shifted
        if (lastGear != subsystem.getGear()) {
            if (subsystem.getGear() == Shiftable.gear.LOW.getNumVal()) {
                this.getPIDController().setP(kP);
                this.getPIDController().setI(kI);
                this.getPIDController().setD(kD);
            } else {
                this.getPIDController().setP(kP * highGearAngularCoefficient);
                this.getPIDController().setI(kI * highGearAngularCoefficient);
                this.getPIDController().setD(kD * highGearAngularCoefficient);
            }
            lastGear = subsystem.getGear();
        }

        //Actually do stuff
        super.execute();
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("ShiftingUnidirectionalNavXArcadeDrive End.", this.getClass());
    }

    /**
     * Stop the motors and log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("ShiftingUnidirectionalNavXArcadeDrive Interrupted! Stopping the robot.", this.getClass());
        subsystem.fullStop();
    }
}
