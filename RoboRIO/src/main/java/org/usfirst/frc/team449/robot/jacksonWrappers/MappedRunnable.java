package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A jackson-compatible wrapper on Java's {@link Runnable}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface MappedRunnable extends Runnable {

}
