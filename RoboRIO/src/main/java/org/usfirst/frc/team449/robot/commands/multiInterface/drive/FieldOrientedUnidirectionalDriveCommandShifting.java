package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.AutoshiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.oi.fieldoriented.OIFieldOriented;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

import java.util.List;

/**
 * Unidirectional drive with field-oriented control and autoshifting.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDriveCommandShifting<T extends YamlSubsystem & DriveUnidirectional & SubsystemAHRS & DriveShiftable>
        extends FieldOrientedUnidirectionalDriveCommand {

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
     * Default constructor
     *
     * @param toleranceBuffer            How many consecutive loops have to be run while within tolerance to be
     *                                   considered on target. Multiply by loop period of ~20 milliseconds for time.
     *                                   Defaults to 0.
     * @param absoluteTolerance          The maximum number of degrees off from the target at which we can be considered
     *                                   within tolerance.
     * @param minimumOutput              The minimum output of the loop. Defaults to zero.
     * @param maximumOutput              The maximum output of the loop. Can be null, and if it is, no maximum output is
     *                                   used.
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
                                                           int toleranceBuffer,
                                                           double minimumOutput, @Nullable Double maximumOutput,
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
        super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, kP, kI, kD, subsystem, oi, snapPoints);
        this.subsystem = subsystem;
        this.autoshiftComponent = autoshiftComponent;
        this.highGearAngularCoefficient = highGearAngularCoefficient != null ? highGearAngularCoefficient : 1;
    }

    /**
     * Set PID setpoint to processed controller setpoint and autoshift.
     */
    @Override
    protected void execute() {
        if (!subsystem.getOverrideAutoshift()) {
            autoshiftComponent.autoshift(oi.getVelCached(), subsystem.getLeftVelCached(), subsystem.getRightVelCached(), gear -> subsystem.setGear(gear));
        }
        super.execute();
    }

    /**
     * Give the correct output to the motors based on the PID output and velocity input.
     *
     * @param output The output of the angular PID loop.
     */
    @Override
    protected void usePIDOutput(double output) {
        super.usePIDOutput(output * (subsystem.getGear() == Shiftable.gear.HIGH.getNumVal() ? highGearAngularCoefficient : 1));
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