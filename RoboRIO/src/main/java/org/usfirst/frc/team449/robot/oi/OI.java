package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.oblarg.oblog.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface OI extends Updatable, Loggable {}
