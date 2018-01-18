package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;

/**
 * A wrapper for a {@link VictorSP} allowing it to be easily constructed from a map object.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedVictor extends VictorSP implements SimpleMotor {

    /**
     * Json constructor using a port and inversion.
     *
     * @param port     The port number of the Victor.
     * @param inverted Whether the motor is inverted. Defaults to false.
     */
    @JsonCreator
    public MappedVictor(@JsonProperty(required = true) int port,
                        boolean inverted) {
        super(port);
        this.setInverted(inverted);
        this.setSafetyEnabled(false);
    }

    /**
     * Set the velocity for the motor to go at.
     *
     * @param velocity the desired velocity, on [-1, 1].
     */
    @Override
    public void setVelocity(double velocity) {
        set(velocity);
    }

    /**
     * Enables the motor, if applicable.
     */
    @Override
    public void enable() {
        //Do nothing.
    }
}
