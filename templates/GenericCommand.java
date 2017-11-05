package templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * A broad template for all commands.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class GenericCommand extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final GenericSubsystemInterface subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	@JsonCreator
	public GenericCommand(@NotNull @JsonProperty(required = true) GenericSubsystemInterface subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 *
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("GenericCommand init.", this.getClass());
	}

	/**
	 *
	 */
	@Override
	protected void execute() {
	}

	/**
	 * @return
	 */
	@Override
	protected boolean isFinished() {
		//This does NOT have to be true.
		return true;
	}

	/**
	 *
	 */
	@Override
	protected void end() {
		Logger.addEvent("GenericCommand end.", this.getClass());
	}

	/**
	 *
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("GenericCommand Interrupted!", this.getClass());
	}
}