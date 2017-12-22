package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommand;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A simple analogMotor that uses velocity.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogMotorSimple extends YamlSubsystem implements SubsystemAnalogMotor{

    /**
     * The motor this subsystem controls.
     */
    @NotNull
    private final SimpleMotor motor;

    /**
     * The default command to run. Can be null to not have a default command.
     */
    @Nullable
    private final Command defaultCommand;

    /**
     * Default constructor.
     *
     * @param motor The motor this subsystem controls.
     * @param defaultCommand The default command to run. Can be null to not have a default command.
     */
    @JsonCreator
    public AnalogMotorSimple(@NotNull @JsonProperty(required = true) SimpleMotor motor,
                             @Nullable YamlCommand defaultCommand) {
        this.motor = motor;
        this.defaultCommand = defaultCommand != null ? defaultCommand.getCommand() : null;
    }

    /**
     * Initialize the default command, if it exists.
     */
    @Override
    protected void initDefaultCommand() {
        if(defaultCommand != null){
            setDefaultCommand(defaultCommand);
        }
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
