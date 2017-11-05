package templates;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A generic example of an interface for a subsystem.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface GenericSubsystemInterface {

}
