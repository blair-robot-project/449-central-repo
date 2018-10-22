package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Clock;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * The main class of the robot, constructs all the subsystems and initializes default commands.
 */
public class Robot extends TimedRobot {

    /**
     * The absolute filepath to the resources folder containing the config files.
     */
    @NotNull
    public static final String RESOURCES_PATH = "/home/lvuser/449_resources/";

    /**
     * The name of the map to read from. Should be overriden by a subclass to change the name.
     */
    protected String mapName = "test_bed.yml";

    /**
     * The object constructed directly from the yaml map.
     */
    protected RobotMap robotMap;

    /**
     * Whether or not the robot has been enabled yet.
     */
    protected boolean enabled;

    /**
     * Whether or not the auto command should be started.
     */
    protected boolean shouldStartAuto;

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
            Map<?, ?> normalized = (Map<?, ?>) yaml.load(new FileReader(RESOURCES_PATH + mapName));
            YAMLMapper mapper = new YAMLMapper();
            //Turn the Map read by SnakeYaml into a String so Jackson can read it.
            String fixed = mapper.writeValueAsString(normalized);
            //Use a parameter name module so we don't have to specify name for every field.
            mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
            //Add mix-ins
            mapper.registerModule(new WPIModule());
            mapper.registerModule(new JavaModule());
            //Deserialize the map into an object.
            robotMap = mapper.readValue(fixed, RobotMap.class);
        } catch (IOException e) {
            //This is either the map file not being in the file system OR it being improperly formatted.
            System.out.println("Config file is bad/nonexistent!");
            e.printStackTrace();
        }

        //Read sensors
        this.robotMap.getUpdater().run();

        shouldStartAuto = this.robotMap.getAutoStartupCommand() != null;

        robotMap.getLogger().start();
    }

    /**
     * Run when we first enable in teleop.
     */
    @Override
    public void teleopInit() {
        //Read sensors
        this.robotMap.getUpdater().run();

        if (this.robotMap.getAutoStartupCommand() != null) {
            this.robotMap.getAutoStartupCommand().cancel();
        }

        //Run startup command if we start in teleop.
        if (!enabled) {
            if (robotMap.getStartupCommand() != null) {
                robotMap.getStartupCommand().start();
            }
            enabled = true;
        }

        //Run the teleop startup command
        if (robotMap.getTeleopStartupCommand() != null) {
            robotMap.getTeleopStartupCommand().start();
        }
    }

    /**
     * Run every tick in teleop.
     */
    @Override
    public void teleopPeriodic() {
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
        //Read sensors
        this.robotMap.getUpdater().run();

        //Run startup command if we start in auto.
        if (!enabled) {
            if (robotMap.getStartupCommand() != null) {
                robotMap.getStartupCommand().start();
            }
            enabled = true;
        }

        //Run the auto startup command
        if (shouldStartAuto && !DriverStation.getInstance().getGameSpecificMessage().isEmpty()) {
            robotMap.getAutoStartupCommand().start();
            shouldStartAuto = false;
        }
    }

    /**
     * Runs every tick in autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        //Read sensors
        this.robotMap.getUpdater().run();

        //Start auto if the game-specific message has been set
        if (shouldStartAuto && !DriverStation.getInstance().getGameSpecificMessage().isEmpty()) {
            robotMap.getAutoStartupCommand().start();
            shouldStartAuto = false;
        }

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
        //Read sensors
        this.robotMap.getUpdater().run();
    }
}
