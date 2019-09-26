package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A mix-in for {@link java.util.function.DoubleUnaryOperator} that adds JsonTypeInfo. Don't make sublasses of this.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface DoubleUnaryOperatorMixIn {
}
