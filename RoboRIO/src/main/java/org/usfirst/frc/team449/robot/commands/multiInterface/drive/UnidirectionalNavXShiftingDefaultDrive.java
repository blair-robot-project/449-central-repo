package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.AutoshiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
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
public class UnidirectionalNavXShiftingDefaultDrive<T extends YamlSubsystem & DriveUnidirectional & SubsystemAHRS & DriveShiftable> extends UnidirectionalNavXDefaultDrive {

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
     * Default constructor
     *
     * @param toleranceBuffer             How many consecutive loops have to be run while within tolerance to be
     *                                    considered on target. Multiply by loop period of ~20 milliseconds for time.
     *                                    Defaults to  0.
     * @param absoluteTolerance           The maximum number of degrees off from the target at which we can be
     *                                    considered within tolerance.
     * @param minimumOutput               The minimum output of the loop. Defaults to zero.
     * @param maximumOutput               The maximum output of the loop. Can be null, and if it is, no maximum output
     *                                    is used.
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
     * @param autoshiftComponent          The helper object for autoshifting.
     * @param highGearAngularCoefficient  The coefficient to multiply the loop output by in high gear. Defaults to 1.
     */
    @JsonCreator
    public UnidirectionalNavXShiftingDefaultDrive(@JsonProperty(required = true) double absoluteTolerance,
                                                  int toleranceBuffer,
                                                  double minimumOutput, @Nullable Double maximumOutput,
                                                  double deadband,
                                                  @Nullable Double maxAngularVelToEnterLoop,
                                                  boolean inverted,
                                                  double kP,
                                                  double kI,
                                                  double kD,
                                                  @NotNull @JsonProperty(required = true) BufferTimer driveStraightLoopEntryTimer,
                                                  @NotNull @JsonProperty(required = true) T subsystem,
                                                  @NotNull @JsonProperty(required = true) OIUnidirectional oi,
                                                  @NotNull @JsonProperty(required = true) AutoshiftComponent autoshiftComponent,
                                                  @Nullable Double highGearAngularCoefficient) {
        super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, maxAngularVelToEnterLoop,
                inverted, kP, kI, kD, driveStraightLoopEntryTimer, subsystem, oi);
        this.autoshiftComponent = autoshiftComponent;
        this.subsystem = subsystem;
        this.highGearAngularCoefficient = highGearAngularCoefficient != null ? highGearAngularCoefficient : 1;
    }

    /**
     * Autoshift and decide whether or not we should be in free drive or straight drive
     */
    @Override
    public void execute() {
        //Auto-shifting
        if (!subsystem.getOverrideAutoshift()) {
            autoshiftComponent.autoshift((oi.getLeftOutputCached() + oi.getRightOutputCached()) / 2., subsystem.getLeftVelCached(),
                    subsystem.getRightVelCached(), gear -> subsystem.setGear(gear));
        }
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

    /**
     * Give the correct output to the motors based on whether we're in free drive or drive straight.
     *
     * @param output The output of the angular PID loop.
     */
    @Override
    protected void usePIDOutput(double output) {
        super.usePIDOutput(output * (subsystem.getGear() == Shiftable.gear.HIGH.getNumVal() ? highGearAngularCoefficient : 1));
    }
}
