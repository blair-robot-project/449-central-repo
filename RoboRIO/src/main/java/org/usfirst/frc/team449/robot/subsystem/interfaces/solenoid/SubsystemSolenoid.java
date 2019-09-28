package org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;

/**
 * A subsystem with a single DoubleSolenoid piston.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemSolenoid {

    /**
     * @param value The position to set the solenoid to.
     */
    void setSolenoid(@NotNull DoubleSolenoid.Value value);

    /**
     * @return the current position of the solenoid.
     */
    @NotNull
    DoubleSolenoid.Value getSolenoidPosition();
}
