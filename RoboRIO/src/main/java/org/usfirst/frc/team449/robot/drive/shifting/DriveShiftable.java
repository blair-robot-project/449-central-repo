package org.usfirst.frc.team449.robot.drive.shifting;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;

/**
 * A drive that has a high gear and a low gear and can switch between them.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface DriveShiftable extends Shiftable {

    /**
     * @return true if currently overriding autoshifting, false otherwise.
     */
    boolean getOverrideAutoshift();

    /**
     * @param override Whether or not to override autoshifting.
     */
    void setOverrideAutoshift(boolean override);
}
