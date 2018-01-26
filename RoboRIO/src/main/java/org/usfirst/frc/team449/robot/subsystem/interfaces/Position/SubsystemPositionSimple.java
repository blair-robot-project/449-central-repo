package org.usfirst.frc.team449.robot.subsystem.interfaces.Position;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;

public class SubsystemPositionSimple extends Subsystem implements SubsystemPosition{

    /**
     * Motor that controls the elevator
     */
    @NotNull
    private final FPSTalon motor;

    /**
     * Default Constructor
     * @param motor The motor changing the position
     */
    @JsonCreator
    public SubsystemPositionSimple(@NotNull@JsonProperty(required = true) FPSTalon motor){
        this.motor = motor;
    }

    /**
     * Initialize the default command to do nothing.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }

    /**
     * @param value the position to set the motor to
     */
    public void setPosition(double value){
        motor.setPositionSetpoint(value);
    }

    /**
     * @param value the velocity to set the motor to
     */
    public void setMotorOutput(double value){
        motor.setVelocity(value);
    }

    /**
     * @return the state of the reverse limit switch
     */
    public boolean getReverseLimit(){
        return motor.getRevLimitSwitch();
    }

    /**
     * @return the state of the forward limit switch
     */
    public boolean getForwardLimit(){
        return motor.getFwdLimitSwitch();
    }

    /**
     * Enables the motor
     */
    public void enableMotor(){
        motor.enable();
    }

    /**
     * Disables the motor
     */
    public void disableMotor(){
        motor.disable();
    }
}
