package org.usfirst.frc.team449.robot.subsystem.interfaces.position;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;

/**
 * A simple SubsystemPosition that uses a {@link FPSTalon}.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SubsystemPositionSimple extends Subsystem implements SubsystemPosition {

    /**
     * Motor that controls the subsystem
     */
    @NotNull
    private final FPSTalon motor;

    /**
     * How close the motor has to be to the setpoint to be considered on target, in feet.
     */
    private final double onTargetTolerance;

    /**
     * Default constructor.
     *
     * @param motor The motor changing the position
     * @param onTargetTolerance How close the motor has to be to the setpoint to be considered on target, in feet.
     */
    @JsonCreator
    public SubsystemPositionSimple(@NotNull @JsonProperty(required = true) FPSTalon motor,
                                   @JsonProperty(required = true) double onTargetTolerance) {
        this.motor = motor;
        this.onTargetTolerance = onTargetTolerance;
    }

    /**
     * Initialize the default command to do nothing.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }

    /**
     * Set the position setpoint
     *
     * @param feet Setpoint in feet from the limit switch used to zero
     */
    @Override
    public void setPositionSetpoint(double feet) {
        motor.setPositionSetpoint(feet);
    }

    /**
     * Set a % output setpoint for the motor.
     *
     * @param output The speed for the motor to run at, on [-1, 1]
     */
    @Override
    public void setMotorOutput(double output) {
        motor.setVelocity(output);
    }

    /**
     * Get the state of the reverse limit switch.
     *
     * @return True if the reverse limit switch is triggered, false otherwise.
     */
    @Override
    public boolean getReverseLimit() {
        return motor.getRevLimitSwitch();
    }

    /**
     * Get the state of the forwards limit switch.
     *
     * @return True if the forwards limit switch is triggered, false otherwise.
     */
    @Override
    public boolean getForwardLimit() {
        return motor.getFwdLimitSwitch();
    }

    /**
     * Set the position to 0.
     */
    @Override
    public void resetPosition() {
        motor.resetPosition();
    }

    /**
     * Check if the mechanism has reached the setpoint.
     *
     * @return True if the setpoint has been reached, false otherwise.
     */
    @Override
    public boolean onTarget() {
        return Math.abs(motor.getError()) < onTargetTolerance;
    }

    /**
     * Enable the motors of this subsystem.
     */
    @Override
    public void enableMotor() {
        motor.enable();
    }

    /**
     * Disable the motors of this subsystem.
     */
    @Override
    public void disableMotor() {
        motor.disable();
    }
}