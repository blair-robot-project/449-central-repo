package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveTalonCluster;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunLoadedProfile;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The main class of the robot, constructs all the subsystems and initializes default commands.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Robot extends IterativeRobot {

	/**
	 * The absolute filepath to the resources folder containing the config files.
	 */
	@NotNull
	public static final String RESOURCES_PATH = "/home/lvuser/449_resources/";

	/**
	 * The object constructed directly from the yaml map.
	 */
	private GenericRobotMap robotMap;

	/**
	 * The Notifier running the logging thread.
	 */
	private Notifier loggerNotifier;

	/**
	 * The command to run during autonomous. Null to do nothing during autonomous.
	 */
	@Nullable
	private Command autonomousCommand;

	/**
	 * Whether or not the robot has been enabled yet.
	 */
	private boolean enabled;

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		//Set up start time
		Clock.setStartTime();
		Clock.updateTime();

		enabled = false;

		//Yes this should be a print statement, it's useful to know that robotInit started.
		System.out.println("Started robotInit.");

		Yaml yaml = new Yaml();
		try {
			//Read the yaml file with SnakeYaml so we can use anchors and merge syntax.
			Map<?, ?> normalized = (Map<?, ?>) yaml.load(new FileReader(RESOURCES_PATH + "map.yml"));
			YAMLMapper mapper = new YAMLMapper();
			//Turn the Map read by SnakeYaml into a String so Jackson can read it.
			String fixed = mapper.writeValueAsString(normalized);
			//Use a parameter name module so we don't have to specify name for every field.
			mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
			//Deserialize the map into an object.
			robotMap = mapper.readValue(fixed, RobotMap2017.class);
		} catch (IOException e) {
			//This is either the map file not being in the file system OR it being improperly formatted.
			System.out.println("Config file is bad/nonexistent!");
			e.printStackTrace();
		}

		//Read sensors
		this.robotMap.getUpdater().run();

		//Set fields from the map.
		this.loggerNotifier = new Notifier(robotMap.getLogger());

		autonomousCommand = robotMap.getAutoCommand();

		//Run the logger to write all the events that happened during initialization to a file.
		robotMap.getLogger().run();
		Clock.updateTime();
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
		//Do the startup tasks
		doStartupTasks();

		//Read sensors
		this.robotMap.getUpdater().run();

		//Run startup command if we start in teleop.
		if (!enabled) {
			if (robotMap.getStartupCommand() != null) {
				robotMap.getStartupCommand().start();
			}
			enabled = true;
		}

		if (robotMap.getTeleopStartupCommand() != null) {
			robotMap.getTeleopStartupCommand().start();
		}
	}

	/**
	 * Run every tick in teleop.
	 */
	@Override
	public void teleopPeriodic() {
		//Refresh the current time.
		Clock.updateTime();

		//Read sensors
		this.robotMap.getUpdater().run();

		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we first enable in autonomous
	 */
	@Override
	public void autonomousInit() {
		//Do startup tasks
		doStartupTasks();

		//Read sensors
		this.robotMap.getUpdater().run();

		//Run startup command if we start in auto.
		if (!enabled) {
			if (robotMap.getStartupCommand() != null) {
				robotMap.getStartupCommand().start();
			}
			enabled = true;
		}

		if (robotMap.getAutoStartupCommand() != null) {
			robotMap.getAutoStartupCommand().start();
		}

		//Start running the autonomous command
		if (autonomousCommand != null) {
			autonomousCommand.start();
		}
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Update the current time
		Clock.updateTime();
		//Read sensors
		this.robotMap.getUpdater().run();
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we disable.
	 */
	@Override
	public void disabledInit() {
		//Do nothing
	}

	/**
	 * Run when we first enable in test mode.
	 */
	@Override
	public void testInit() {
		//Run startup command if we start in test mode.
		if (!enabled) {
			if (robotMap.getStartupCommand() != null) {
				robotMap.getStartupCommand().start();
			}
			enabled = true;
		}
	}

	/**
	 * Run every tic while disabled
	 */
	@Override
	public void disabledPeriodic() {
		Clock.updateTime();
		//Read sensors
		this.robotMap.getUpdater().run();
	}


	/**
	 * Do tasks that should be done when we first enable, in both auto and teleop.
	 */
	private void doStartupTasks() {
		//Refresh the current time.
		Clock.updateTime();

		//Start running the logger
		loggerNotifier.startPeriodic(robotMap.getLogger().getLoopTimeSecs());
	}
}
