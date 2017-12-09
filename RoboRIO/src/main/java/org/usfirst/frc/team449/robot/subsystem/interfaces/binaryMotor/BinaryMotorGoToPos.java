package org.usfirst.frc.team449.robot.subsystem.interfaces.binaryMotor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A binary motor subsystem that uses PID to go to a given position when turned on.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class BinaryMotorGoToPos extends YamlSubsystem implements SubsystemBinaryMotor {

    /**
     * The talon to move to the given position.
     */
    @NotNull
    private final FPSTalon talon;

    /**
     * The position, in feet, for the talon to go to.
     */
    private final double positionFeet;

    /**
     * Whether or not the motor is on.
     */
    private boolean motorOn;

    /**
     * Default constructor
     *
     * @param talon        The talon to move to the given position.
     * @param positionFeet The position, in feet, for the talon to go to. Defaults to 0.
     */
    @JsonCreator
    public BinaryMotorGoToPos(@JsonProperty(required = true) @NotNull FPSTalon talon,
                              double positionFeet) {
        this.talon = talon;
        this.positionFeet = positionFeet;
        motorOn = false;
    }

    /**
     * Do nothing.
     */
    @Override
    protected void initDefaultCommand() {
    }

    /**
     * Turns the motor on, and sets it to a map-specified position.
     */
    @Override
    public void turnMotorOn() {
        talon.enable();
        talon.setPositionSetpoint(positionFeet);
        motorOn = true;
    }

    /**
     * Turns the motor off.
     */
    @Override
    public void turnMotorOff() {
        talon.disable();
        motorOn = false;
    }

    /**
     * @return true if the motor is on, false otherwise.
     */
    @Override
    public boolean isMotorOn() {
        return motorOn;
    }
}
