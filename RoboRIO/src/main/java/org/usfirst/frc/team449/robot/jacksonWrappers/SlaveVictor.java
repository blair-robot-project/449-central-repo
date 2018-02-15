package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link VictorSPX} that will be slaved to another Victor or a {@link com.ctre.phoenix.motorcontrol.can.TalonSRX}.
 */
public class SlaveVictor {

    /**
     * The Victor this is a wrapper on.
     */
    @NotNull
    private final VictorSPX victorSPX;

    /**
     * Default constructor.
     *
     * @param port     The CAN ID of this Victor SPX.
     * @param inverted Whether or not to invert this Victor. Note this is not relative to the master. Defaults to
     *                 false.
     */
    @JsonCreator
    public SlaveVictor(@JsonProperty(required = true) int port,
                       boolean inverted) {
        victorSPX = new VictorSPX(port);
        victorSPX.setInverted(inverted);
    }

    /**
     * Set this Victor to follow another CAN device.
     *
     * @param toFollow  The motor controller to follow.
     * @param brakeMode Whether this Talon should be in brake mode or coast mode.
     */
    public void setMaster(IMotorController toFollow, boolean brakeMode) {
        //Brake mode doesn't automatically follow master
        victorSPX.setNeutralMode(brakeMode ? NeutralMode.Brake : NeutralMode.Coast);

        //Follow the leader
        victorSPX.follow(toFollow);
    }
}
