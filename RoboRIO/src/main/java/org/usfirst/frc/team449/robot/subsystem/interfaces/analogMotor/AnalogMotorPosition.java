package org.usfirst.frc.team449.robot.subsystem.interfaces.analogMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;

/**
 * An analogMotor that uses position instead of velocity.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnalogMotorPosition extends Subsystem implements SubsystemAnalogMotor {

    /**
     * The motor this subsystem controls.
     */
    @NotNull
    private final FPSTalon motor;

    /**
     * The constants that are added to and multiplied by a [-1, 1] setpoint to turn it into the desired range of
     * distances in feet.
     */
    private final double addToSP, multiplyBySP;

    /**
     * Default constructor.
     *
     * @param motor  The motor this subsystem controls.
     * @param minPos The lowest position, in feet, this subsystem should go to. Defaults to 0.
     * @param maxPos The greatest position, in feet, this subsystem should go to.
     */
    @JsonCreator
    public AnalogMotorPosition(@NotNull @JsonProperty(required = true) FPSTalon motor,
                               double minPos,
                               @JsonProperty(required = true) double maxPos) {
        this.motor = motor;
        this.addToSP = (maxPos + minPos) / 2.;
        this.multiplyBySP = Math.abs((maxPos - minPos) / 2.);
    }

    /**
     * Set output to a given input.
     *
     * @param input The input to give to the motor.
     */
    @Override
    public void set(double input) {
        motor.setPositionSetpoint(addToSP + input * multiplyBySP);
    }

    /**
     * Disable the motor.
     */
    @Override
    public void disable() {
        motor.disable();
    }

    /**
     * Initialize the default command for a subsystem By default subsystems have no default command, but if they do, the
     * default command is set with this method. It is called on all Subsystems by CommandBase in the users program after
     * all the Subsystems are created.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }
}
