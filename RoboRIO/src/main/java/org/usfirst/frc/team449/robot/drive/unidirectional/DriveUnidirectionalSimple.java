package org.usfirst.frc.team449.robot.drive.unidirectional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A simple unidirectional drive with no encoders.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveUnidirectionalSimple extends YamlSubsystem implements DriveUnidirectional {

    /**
     * The motor for the left side of the drive.
     */
    @NotNull
    private final SimpleMotor leftMotor;

    /**
     * The motor for the right side of the drive.
     */
    @NotNull
    private final SimpleMotor rightMotor;

    /**
     * Default constructor
     *
     * @param leftMotor  The motor for the left side of the drive.
     * @param rightMotor The motor for the right side of the drive.
     */
    @JsonCreator
    public DriveUnidirectionalSimple(@NotNull @JsonProperty(required = true) SimpleMotor leftMotor,
                                     @NotNull @JsonProperty(required = true) SimpleMotor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
    }

    /**
     * Initialize the default command for a subsystem. By default subsystems have no default command, but if they do,
     * the default command is set with this method. It is called on all Subsystems by CommandBase in the users program
     * after all the Subsystems are created.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }

    /**
     * Set the output of each side of the drive.
     *
     * @param left  The output for the left side of the drive, from [-1, 1]
     * @param right the output for the right side of the drive, from [-1, 1]
     */
    @Override
    public void setOutput(double left, double right) {
        leftMotor.setVelocity(left);
        rightMotor.setVelocity(right);
    }

    /**
     * Get the velocity of the left side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getLeftVel() {
        return null;
    }

    /**
     * Get the velocity of the right side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getRightVel() {
        return null;
    }

    /**
     * Get the position of the left side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getLeftPos() {
        return null;
    }

    /**
     * Get the position of the right side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getRightPos() {
        return null;
    }

    /**
     * Get the cached velocity of the left side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getLeftVelCached() {
        return null;
    }

    /**
     * Get the cached velocity of the right side of the drive.
     *
     * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getRightVelCached() {
        return null;
    }

    /**
     * Get the cached position of the left side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getLeftPosCached() {
        return null;
    }

    /**
     * Get the cached position of the right side of the drive.
     *
     * @return The signed position in feet, or null if the drive doesn't have encoders.
     */
    @Nullable
    @Override
    public Double getRightPosCached() {
        return null;
    }

    /**
     * Completely stop the robot by setting the voltage to each side to be 0.
     */
    @Override
    public void fullStop() {
        leftMotor.setVelocity(0);
        rightMotor.setVelocity(0);
    }

    /**
     * If this drive uses motors that can be disabled, enable them.
     */
    @Override
    public void enableMotors() {
        leftMotor.enable();
        rightMotor.enable();
    }

    /**
     * Reset the position of the drive if it has encoders.
     */
    @Override
    public void resetPosition() {
        //No encoders, do nothing
    }

    /**
     * Updates all cached values with current ones.
     */
    @Override
    public void update() {
        //Do nothing
    }
}
