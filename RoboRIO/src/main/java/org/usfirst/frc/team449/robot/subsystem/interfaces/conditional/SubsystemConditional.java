package org.usfirst.frc.team449.robot.subsystem.interfaces.conditional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

/**
 * A subsystem with a condition that's sometimes met, e.g. a limit switch, a current/power limit, an IR sensor.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemConditional extends Updatable {

    /**
     * @return true if the condition is met, false otherwise
     */
    boolean isConditionTrue();

    /**
     * @return true if the condition was met when cached, false otherwise
     */
    boolean isConditionTrueCached();
}
