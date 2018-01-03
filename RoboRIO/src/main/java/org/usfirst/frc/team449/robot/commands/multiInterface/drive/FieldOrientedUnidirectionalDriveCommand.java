package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.oi.fieldoriented.OIFieldOriented;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.commands.PIDAngleCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Unidirectional drive with field-oriented control
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDriveCommand<T extends YamlSubsystem & DriveUnidirectional & SubsystemAHRS> extends PIDAngleCommand {

    /**
     * The drive this command is controlling.
     */
    @NotNull
    protected final T subsystem;

    /**
     * The OI giving the input stick values.
     */
    @NotNull
    protected final OIFieldOriented oi;

    /**
     * The points to snap the PID controller input to.
     */
    @NotNull
    private final List<AngularSnapPoint> snapPoints;

    /**
     * The absolute angular setpoint for the robot to go to. Field to avoid garbage collection.
     */
    @Nullable
    private Double theta;

    /**
     * Default constructor
     *
     * @param toleranceBuffer   How many consecutive loops have to be run while within tolerance to be considered on
     *                          target. Multiply by loop period of ~20 milliseconds for time. Defaults to 0.
     * @param absoluteTolerance The maximum number of degrees off from the target at which we can be considered within
     *                          tolerance.
     * @param minimumOutput     The minimum output of the loop. Defaults to zero.
     * @param maximumOutput     The maximum output of the loop. Can be null, and if it is, no maximum output is used.
     * @param deadband          The deadband around the setpoint, in degrees, within which no output is given to the
     *                          motors. Defaults to zero.
     * @param inverted          Whether the loop is inverted. Defaults to false.
     * @param kP                Proportional gain. Defaults to zero.
     * @param kI                Integral gain. Defaults to zero.
     * @param kD                Derivative gain. Defaults to zero.
     * @param subsystem         The drive to execute this command on.
     * @param oi                The OI controlling the robot.
     * @param snapPoints        The points to snap the PID controller input to.
     */
    @JsonCreator
    public FieldOrientedUnidirectionalDriveCommand(@JsonProperty(required = true) double absoluteTolerance,
                                                   int toleranceBuffer,
                                                   double minimumOutput, @Nullable Double maximumOutput,
                                                   double deadband,
                                                   boolean inverted,
                                                   double kP,
                                                   double kI,
                                                   double kD,
                                                   @NotNull @JsonProperty(required = true) T subsystem,
                                                   @NotNull @JsonProperty(required = true) OIFieldOriented oi,
                                                   @Nullable List<AngularSnapPoint> snapPoints) {
        //Assign stuff
        super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, subsystem, kP, kI, kD);
        this.oi = oi;
        this.subsystem = subsystem;
        this.snapPoints = snapPoints != null ? snapPoints : new ArrayList<>();

        //Needs a requires because it's a default command.
        requires(this.subsystem);

        //Logging, but in Spanish.
        Logger.addEvent("Drive Robot bueno", this.getClass());
    }

    /**
     * Initialize PIDController and variables.
     */
    @Override
    protected void initialize() {
        //Reset all values of the PIDController and enable it.
        this.getPIDController().reset();
        this.getPIDController().enable();
        Logger.addEvent("FieldOrientedUnidirectionalDriveCommand init.", this.getClass());
    }

    /**
     * Set PID setpoint to processed controller setpoint.
     */
    @Override
    protected void execute() {
        theta = oi.getThetaCached();
        if (theta != null) {
            for (AngularSnapPoint snapPoint : snapPoints) {
                //See if we should snap
                if (snapPoint.getLowerBound() < theta && theta < snapPoint.getUpperBound()) {
                    theta = snapPoint.getSnapTo();
                    //Break to shorten runtime, we'll never snap twice.
                    break;
                }
            }
            this.getPIDController().setSetpoint(theta);
        }
    }

    /**
     * Run constantly because this is a defaultDrive
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
        Logger.addEvent("FieldOrientedUnidirectionalDriveCommand End.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("FieldOrientedUnidirectionalDriveCommand Interrupted!", this.getClass());
    }

    /**
     * Give the correct output to the motors based on the PID output and velocity input.
     *
     * @param output The output of the angular PID loop.
     */
    @Override
    protected void usePIDOutput(double output) {
        //Process or zero the input depending on whether the NavX is being overriden.
        output = subsystem.getOverrideGyro() ? 0 : processPIDOutput(output);

        //Adjust the heading according to the PID output, it'll be positive if we want to go right.
        subsystem.setOutput(oi.getVelCached() - output, oi.getVelCached() + output);
    }

    /**
     * A data-holding class representing an angular setpoint to "snap" the controller output to.
     */
    protected static class AngularSnapPoint {

        /**
         * The angle to snap the setpoint to, in degrees.
         */
        private final double snapTo;

        /**
         * The upper bound, below which all angles above snapTo are changed to snapTo. Measured in degrees.
         */
        private final double upperBound;

        /**
         * The lower bound, above which all angles below snapTo are changed to snapTo. Measured in degrees.
         */
        private final double lowerBound;

        /**
         * Default constructor.
         *
         * @param snapTo     The angle to snap the setpoint to, in degrees.
         * @param upperBound The upper bound, below which all angles above snapTo are changed to snapTo. Measured in
         *                   degrees.
         * @param lowerBound The lower bound, above which all angles below snapTo are changed to snapTo. Measured in
         *                   degrees.
         */
        @JsonCreator
        public AngularSnapPoint(@JsonProperty(required = true) double snapTo,
                                @JsonProperty(required = true) double upperBound,
                                @JsonProperty(required = true) double lowerBound) {
            this.snapTo = snapTo;
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
        }

        /**
         * @return The angle to snap the setpoint to, in degrees.
         */
        public double getSnapTo() {
            return snapTo;
        }

        /**
         * @return The upper bound, below which all angles above snapTo are changed to snapTo. Measured in degrees.
         */
        public double getUpperBound() {
            return upperBound;
        }

        /**
         * @return The lower bound, above which all angles below snapTo are changed to snapTo. Measured in degrees.
         */
        public double getLowerBound() {
            return lowerBound;
        }
    }
}