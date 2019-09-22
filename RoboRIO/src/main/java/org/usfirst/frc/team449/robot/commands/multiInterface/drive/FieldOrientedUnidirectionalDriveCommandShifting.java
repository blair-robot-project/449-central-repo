package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.AutoshiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.oi.fieldoriented.OIFieldOriented;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

import java.util.List;

/**
 * Unidirectional drive with field-oriented control and autoshifting.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDriveCommandShifting<T extends Subsystem & DriveUnidirectional & SubsystemAHRS & DriveShiftable>
        extends FieldOrientedUnidirectionalDriveCommand<T> {

    /**
     * The drive to execute this command on.
     */
    @NotNull
    protected final T subsystem;

    /**
     * The helper object for autoshifting.
     */
    @NotNull
    private final AutoshiftComponent autoshiftComponent;

    /**
     * The coefficient to multiply the loop output by in high gear. Defaults to 1.
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
     * @param onTargetBuffer             A buffer timer for having the loop be on target before it stops running. Can be
     *                                   null for no buffer.
     * @param absoluteTolerance          The maximum number of degrees off from the target at which we can be considered
     *                                   within tolerance.
     * @param minimumOutput              The minimum output of the loop. Defaults to zero.
     * @param maximumOutput              The maximum output of the loop. Can be null, and if it is, no maximum output is
     *                                   used.
     * @param loopTimeMillis             The time, in milliseconds, between each loop iteration. Defaults to 20 ms.
     * @param deadband                   The deadband around the setpoint, in degrees, within which no output is given
     *                                   to the motors. Defaults to zero.
     * @param inverted                   Whether the loop is inverted. Defaults to false.
     * @param kP                         Proportional gain. Defaults to zero.
     * @param kI                         Integral gain. Defaults to zero.
     * @param kD                         Derivative gain. Defaults to zero.
     * @param subsystem                  The drive to execute this command on.
     * @param oi                         The OI controlling the robot.
     * @param snapPoints                 The points to snap the PID controller input to.
     * @param autoshiftComponent         The helper object for autoshifting.
     * @param highGearAngularCoefficient The coefficient to multiply the loop output by in high gear. Defaults to 1.
     */
    @JsonCreator
    public FieldOrientedUnidirectionalDriveCommandShifting(@JsonProperty(required = true) double absoluteTolerance,
                                                           @Nullable BufferTimer onTargetBuffer,
                                                           double minimumOutput, @Nullable Double maximumOutput,
                                                           @Nullable Integer loopTimeMillis,
                                                           double deadband,
                                                           boolean inverted,
                                                           double kP,
                                                           double kI,
                                                           double kD,
                                                           @NotNull @JsonProperty(required = true) T subsystem,
                                                           @NotNull @JsonProperty(required = true) OIFieldOriented oi,
                                                           @Nullable List<AngularSnapPoint> snapPoints,
                                                           @NotNull @JsonProperty(required = true) AutoshiftComponent autoshiftComponent,
                                                           @Nullable Double highGearAngularCoefficient) {
        //Assign stuff
        super(absoluteTolerance, onTargetBuffer, minimumOutput, maximumOutput, loopTimeMillis, deadband, inverted, kP
                , kI, kD, subsystem, oi, snapPoints);
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.subsystem = subsystem;
        this.autoshiftComponent = autoshiftComponent;
        this.highGearAngularCoefficient = highGearAngularCoefficient != null ? highGearAngularCoefficient : 1;
        this.lastGear = this.subsystem.getGear();
    }

    /**
     * Set PID setpoint to processed controller setpoint and autoshift.
     */
    @Override
    protected void execute() {
        if (!subsystem.getOverrideAutoshift()) {
            autoshiftComponent.autoshift(oi.getVelCached(), subsystem.getLeftVelCached(),
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

        super.execute();
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("FieldOrientedUnidirectionalDriveCommandShifting End.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("FieldOrientedUnidirectionalDriveCommandShifting Interrupted!", this.getClass());
    }

}