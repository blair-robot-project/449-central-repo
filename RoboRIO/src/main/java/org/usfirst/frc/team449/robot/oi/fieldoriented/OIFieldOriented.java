package org.usfirst.frc.team449.robot.oi.fieldoriented;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.oi.OI;

/**
 * An OI that gives an absolute heading, relative to the field, and a velocity.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class OIFieldOriented implements OI {

    /**
     * The cached linear velocity.
     */
    private double cachedVel;

    /**
     * The cached angular setpoint.
     */
    @Nullable
    private Double cachedTheta;

    /**
     * Get the absolute angle for the robot to move towards.
     *
     * @return An angular setpoint for the robot in degrees, where 0 is pointing at the other alliance's driver station
     * and 90 is pointing at the left wall when looking out from the driver station. Returns null if vel is 0.
     */
    @Nullable
    public abstract Double getTheta();

    /**
     * Get the velocity for the robot to go at.
     *
     * @return A velocity from [-1, 1].
     */
    public abstract double getVel();

    /**
     * Get the cached absolute angle for the robot to move towards.
     *
     * @return An angular setpoint for the robot in degrees, where 0 is pointing at the other alliance's driver station
     * and 90 is pointing at the left wall when looking out from the driver station. Returns null if vel is 0.
     */
    @Nullable
    public Double getThetaCached() {
        return cachedTheta;
    }

    /**
     * Get the cached velocity for the robot to go at.
     *
     * @return A velocity from [-1, 1].
     */
    public double getVelCached() {
        return cachedVel;
    }

    /**
     * Updates all cached values with current ones.
     */
    @Override
    public void update() {
        cachedVel = getVel();
        cachedTheta = getTheta();
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
                "theta",
                "vel"
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
                getThetaCached(),
                getVelCached()
        };
    }

}
