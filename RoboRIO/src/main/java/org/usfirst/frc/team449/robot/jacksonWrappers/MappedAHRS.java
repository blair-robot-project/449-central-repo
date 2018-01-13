package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

/**
 * A Jackson-compatible, invertible wrapper for the NavX.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedAHRS implements Loggable, Updatable {

    /**
     * The AHRS this class is a wrapper on.
     */
    protected final AHRS ahrs;

    /**
     * A multiplier for the yaw angle. -1 to invert, 1 to not.
     */
    protected final int invertYaw;

    /**
     * The 9-axis heading value to return. Field to avoid garbage collection.
     */
    private double toRet;

    /**
     * Cached values.
     */
    private double cachedHeading, cachedAngularDisplacement, cachedAngularVel, cachedXAccel, cachedYAccel;

    /**
     * Default constructor.
     *
     * @param port      The port the NavX is plugged into. It seems like only kMXP (the port on the RIO) works.
     * @param invertYaw Whether or not to invert the yaw axis. Defaults to true.
     */
    @JsonCreator
    public MappedAHRS(@JsonProperty(required = true) SPI.Port port,
                      Boolean invertYaw) {
        this.ahrs = new AHRS(port);
        ahrs.reset();
        if (invertYaw == null || invertYaw) {
            this.invertYaw = -1;
        } else {
            this.invertYaw = 1;
        }
    }

    /**
     * Convert from gs (acceleration due to gravity) to feet/(second^2).
     *
     * @param accelGs An acceleration in gs.
     * @return That acceleration in feet/(sec^2)
     */
    @Contract(pure = true)
    protected static double gsToFeetPerSecondSquared(double accelGs) {
        return accelGs * 32.17; //Wolfram alpha said so
    }

    /**
     * Get the current yaw value.
     *
     * @return The heading, in degrees from [-180, 180]
     */
    public double getHeading() {
        toRet = ahrs.getFusedHeading();
        if (toRet > 180) {
            toRet -= 360;
        }
        return toRet * invertYaw;
    }

    /**
     * Set the current yaw value.
     *
     * @param headingDegrees An angle in degrees, from [-180, 180], to set the heading to.
     */
    public void setHeading(double headingDegrees) {
        ahrs.setAngleAdjustment(ahrs.getYaw() + invertYaw * headingDegrees);
        cachedHeading = headingDegrees;
    }

    /**
     * Get the current total angular displacement. Differs from getHeading because it doesn't limit angle.
     *
     * @return The angular displacement, in degrees.
     */
    public double getAngularDisplacement() {
        return ahrs.getAngle() * invertYaw;
    }

    /**
     * Get the current angular yaw velocity.
     *
     * @return The angular yaw velocity, in degrees/sec.
     */
    public double getAngularVelocity() {
        return ahrs.getRate() * invertYaw;
    }

    /**
     * Get the absolute X acceleration of the robot, relative to the field.
     *
     * @return Linear X acceleration, in feet/(sec^2)
     */
    public double getXAccel() {
        return gsToFeetPerSecondSquared(ahrs.getWorldLinearAccelX());
    }

    /**
     * Get the absolute Y acceleration of the robot, relative to the field.
     *
     * @return Linear Y acceleration, in feet/(sec^2)
     */
    public double getYAccel() {
        return gsToFeetPerSecondSquared(ahrs.getWorldLinearAccelY());
    }

    /**
     * Get the cached yaw value.
     *
     * @return The heading, in degrees from [-180, 180]
     */
    public double getCachedHeading() {
        toRet = ahrs.getFusedHeading();
        if (toRet > 180) {
            toRet -= 360;
        }
        return toRet * invertYaw;
    }

    /**
     * Get the cached total angular displacement. Differs from getHeading because it doesn't limit angle.
     *
     * @return The angular displacement, in degrees.
     */
    public double getCachedAngularDisplacement() {
        return cachedAngularDisplacement;
    }

    /**
     * Get the cached angular yaw velocity.
     *
     * @return The angular yaw velocity, in degrees/sec.
     */
    public double getCachedAngularVelocity() {
        return cachedAngularVel;
    }

    /**
     * Get the cached absolute X acceleration of the robot, relative to the field.
     *
     * @return Linear X acceleration, in feet/(sec^2)
     */
    public double getCachedXAccel() {
        return cachedXAccel;
    }

    /**
     * Get the cached absolute Y acceleration of the robot, relative to the field.
     *
     * @return Linear Y acceleration, in feet/(sec^2)
     */
    public double getCachedYAccel() {
        return cachedYAccel;
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @NotNull
    @Override
    public String[] getHeader() {
        return new String[]{
                "heading",
                "angular_displacement",
                "angular_vel",
                "x_accel",
                "y_accel"
        };
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @NotNull
    @Override
    public Object[] getData() {
        return new Object[]{
                getHeading(),
                getAngularDisplacement(),
                getAngularVelocity(),
                getXAccel(),
                getYAccel()
        };
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "AHRS";
    }

    /**
     * Updates all cached values with current ones.
     */
    @Override
    public void update() {
        cachedHeading = getHeading();
        cachedAngularDisplacement = getAngularDisplacement();
        cachedAngularVel = getAngularVelocity();
        cachedXAccel = getXAccel();
        cachedYAccel = getYAccel();
    }
}
