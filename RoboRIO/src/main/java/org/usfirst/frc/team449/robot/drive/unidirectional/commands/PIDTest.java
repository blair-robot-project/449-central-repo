package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * Drive forward at constant speed then stop to tune PID.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PIDTest<T extends YamlSubsystem & DriveUnidirectional> extends YamlCommandGroupWrapper {

	/**
	 * Default constructor
	 *
	 * @param subsystem the subsystem to execute this command on
	 * @param driveTime How long to drive forwards for, in seconds.
	 * @param speed     The speed to drive at, from [0, 1].
	 */
	@JsonCreator
	public PIDTest(@NotNull @JsonProperty(required = true) T subsystem,
	               @JsonProperty(required = true) double driveTime,
	               @JsonProperty(required = true) double speed) {
		//Drive forward for a bit
		addSequential(new DriveAtSpeed<>(subsystem, speed, driveTime));
		//Stop actively to see how the PID responds.
		addSequential(new DriveAtSpeed<>(subsystem, 0, 100));
	}
}
