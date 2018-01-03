package org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A simple SubsystemSolenoid.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SolenoidSimple extends YamlSubsystem implements SubsystemSolenoid {

	/**
	 * Piston for pushing gears
	 */
	@NotNull
	private final DoubleSolenoid piston;

	/**
	 * The piston's current position
	 */
	private DoubleSolenoid.Value pistonPos;

	/**
	 * Default constructor
	 *
	 * @param piston The piston that comprises this subsystem.
	 */
	@JsonCreator
	public SolenoidSimple(@NotNull @JsonProperty(required = true) MappedDoubleSolenoid piston) {
		this.piston = piston;
	}

	/**
	 * @param value The position to set the solenoid to.
	 */
	public void setSolenoid(@NotNull DoubleSolenoid.Value value) {
		piston.set(value);
		pistonPos = value;
	}

	/**
	 * @return the current position of the solenoid.
	 */
	@NotNull
	@Override
	public DoubleSolenoid.Value getSolenoidPosition() {
		return pistonPos;
	}

	/**
	 * Initialize the default command for a subsystem. By default subsystems have no default command, but if they do,
	 * the default command is set with this method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}
}
