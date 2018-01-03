package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A simple analogMotor that uses velocity.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogMotorSimple extends YamlSubsystem implements SubsystemAnalogMotor {

    /**
     * The motor this subsystem controls.
     */
    @NotNull
    private final SimpleMotor motor;

    /**
     * Default constructor.
     *
     * @param motor The motor this subsystem controls.
     */
    @JsonCreator
    public AnalogMotorSimple(@NotNull @JsonProperty(required = true) SimpleMotor motor) {
        this.motor = motor;
    }

    /**
     * Initialize the default command, if it exists.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing
    }

    /**
     * Set output to a given input.
     *
     * @param input The input to give to the motor.
     */
    @Override
    public void set(double input) {
        motor.enable();
        motor.setVelocity(input);
    }

    /**
     * Disable the motor.
     */
    @Override
    public void disable() {
        motor.disable();
    }
}
