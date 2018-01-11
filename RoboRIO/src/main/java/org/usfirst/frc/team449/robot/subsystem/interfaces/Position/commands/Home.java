package org.usfirst.frc.team449.robot.subsystem.interfaces.Position.commands;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.subsystem.interfaces.Position.SubsystemPosition;


/*
- go backwards until the reverse limit switch is pressed
Then finish and disable command

- need to pass the speed to go in as a parameter

-pass whether to use the forward or reverse limit switch for homing in as a parameter

-speed should be a double from 0 to 1
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Home extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final SubsystemPosition subsystem;

	@NotNull
	private double speed;

	@NotNull
	private boolean useForward;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public Home(@NotNull@JsonProperty(required = true) SubsystemPosition subsystem, double speed, boolean useForward){
		this.subsystem = subsystem;
		this.speed = speed;
		this.useForward = useForward;
	}

	@Override
	protected void execute(){
		if(useForward){
			subsystem.setMotorOutput(speed);
		}else
		{
			subsystem.setMotorOutput(-speed);
		}
	}

	@Override
	protected boolean isFinished() {

		if(useForward == true){
			return subsystem.getForwardLimit();
		}
		else {
			return subsystem.getReverseLimit();
		}
	}
	@Override
	public void end(){
		subsystem.disableMotor();
	}

}