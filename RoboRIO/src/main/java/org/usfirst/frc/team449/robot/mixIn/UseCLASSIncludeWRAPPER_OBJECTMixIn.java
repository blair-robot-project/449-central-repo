package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A mix-in that adds the annotation {@code @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS,
 * include=JsonTypeInfo.As.WRAPPER_OBJECT)}. Don't make sublasses of this.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface UseCLASSIncludeWRAPPER_OBJECTMixIn {}
