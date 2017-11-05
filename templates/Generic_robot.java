package templates;

import com.google.protobuf.Message;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import org.usfirst.frc.team449.robot.MappedSubsystem;

import java.io.IOException;

/**
 * A broad template for all robot classes
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Generic_robot extends IterativeRobot {

	/**
	 * Generic subsystem
	 */
	public static GenericMappedSubsytem GenericMappedSubsystem;

	/**
	 * The object constructed directly from map.cfg.
	 */
	public static RobotMap cfg;

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		System.out.println("Started robotInit.");
		try {
			//Try to construct map from the cfg file
			cfg = (Message) MappedSubsystem.readConfig("/home/lvuser/449_resources/map.cfg",
					//Replace 2017 with your current year.
					Robot2017Map.Robot2017.newBuilder());
		} catch (IOException e) {
			//This is either the map file not being in the file system OR it being improperly formatted.
			System.out.println("Config file is bad/nonexistent!");
			e.printStackTrace();
		}
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
	}

	/**
	 * Run every tick in teleop.
	 */
	@Override
	public void teleopPeriodic() {
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we first enable in autonomous
	 */
	@Override
	public void autonomousInit() {
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}
}
