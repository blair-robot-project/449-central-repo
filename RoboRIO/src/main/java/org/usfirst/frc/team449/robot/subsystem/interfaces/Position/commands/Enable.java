package org.usfirst.frc.team449.robot.subsystem.interfaces.Position.commands;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Enable extends YamlCommandWrapper {



	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}
}
