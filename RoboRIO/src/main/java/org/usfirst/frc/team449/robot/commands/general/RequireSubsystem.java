package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Require a subsystem, stopping the default command until another command is run.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RequireSubsystem extends Command {

    /**
     * Default constructor.
     *
     * @param subsystem The subsystem to require.
     */
    @JsonCreator
    public RequireSubsystem(Subsystem subsystem) {
        requires(subsystem);
    }

    /**
     * Don't exit unless interrupted.
     *
     * @return false
     */
    @Override
    protected boolean isFinished() {
        return false;
    }
}
