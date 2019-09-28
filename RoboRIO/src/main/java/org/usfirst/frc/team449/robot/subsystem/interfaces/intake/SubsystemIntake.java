package org.usfirst.frc.team449.robot.subsystem.interfaces.intake;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * A subsystem used for intaking and possibly ejecting game pieces.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemIntake {

    /**
     * @return the current mode of the intake.
     */
    @NotNull
    IntakeMode getMode();

    /**
     * @param mode The mode to switch the intake to.
     */
    void setMode(@NotNull IntakeMode mode);

    /**
     * An enum for the possible states of the intake.
     */
    enum IntakeMode {
        OFF, IN_SLOW, IN_FAST, OUT_SLOW, OUT_FAST
    }
}
