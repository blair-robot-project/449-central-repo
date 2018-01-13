package org.usfirst.frc.team449.robot.subsystem.singleImplementation.camera.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.singleImplementation.camera.CameraNetwork;

/**
 * Toggles camera on button press.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ChangeCam extends Command {

    /**
     * The subsystem to execute this command on
     */
    @NotNull
    private final CameraNetwork subsystem;

    /**
     * Default constructor.
     *
     * @param subsystem The subsystem to execute this command on.
     */
    @JsonCreator
    public ChangeCam(@NotNull @JsonProperty(required = true) CameraNetwork subsystem) {
        this.subsystem = subsystem;
        requires(subsystem);
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("ChangeCam init", this.getClass());
    }

    /**
     * Switch the MjpegServer to use the next camera in the list
     */
    @Override
    protected void execute() {
        //Switches camNum to next camera, if applicable
        if (subsystem.getCameras().size() == 1) {
            Logger.addEvent("You're trying to switch cameras, but your robot only has one camera!", this.getClass());
        } else {
            subsystem.setCamNum((subsystem.getCamNum() + 1) % subsystem.getCameras().size());
        }

        //Switches to set camera
        subsystem.getServer().setSource(subsystem.getCameras().get(subsystem.getCamNum()));
    }

    /**
     * Finish immediately because this is a state-change command.
     *
     * @return true
     */
    @Override
    protected boolean isFinished() {
        return true;
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("ChangeCam end", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("ChangeCam interrupted!", this.getClass());
    }
}
