package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import io.github.oblarg.oblog.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.other.Clock;
import org.yaml.snakeyaml.Yaml;

/** The main class of the robot, constructs all the subsystems and initializes default commands. */
public class Robot extends TimedRobot {
  /**
   * The absolute filepath to the resources folder containing the config files when the robot is
   * real.
   */
  @NotNull
  public static final String RESOURCES_PATH_REAL =
      Filesystem.getDeployDirectory().getAbsolutePath();
  /**
   * The relative filepath to the resources folder containing the config files when the robot is
   * simulated.
   */
  @NotNull public static final String RESOURCES_PATH_SIMULATED = "./src/main/deploy/";
  /** The name of the map to read from. Should be overriden by a subclass to change the name. */
  @NotNull public static final String mapName = "map.yml";
  /** The filepath to the resources folder containing the config files. */
  @NotNull
  public static final String RESOURCES_PATH =
      RobotBase.isReal() ? RESOURCES_PATH_REAL : RESOURCES_PATH_SIMULATED;
  /**
   * Format for the reference chain (place in the map where the error occurred) when a map error is
   * printed.
   */
  private static final MapErrorFormat MAP_REF_CHAIN_FORMAT = MapErrorFormat.TABLE;
  private static boolean isUnitTesting = false;
  private static boolean isTestingHasBeenCalled = false;
  /** The object constructed directly from the yaml map. */
  @NotNull protected final RobotMap robotMap = Objects.requireNonNull(loadMap());

  /** The method that runs when the robot is turned on. Initializes all subsystems from the map. */
  public static @Nullable RobotMap loadMap() {
    try {
      // Read the yaml file with SnakeYaml so we can use anchors and merge syntax.
      final Map<?, ?> normalized =
          (Map<?, ?>) new Yaml().load(new FileReader(RESOURCES_PATH + "/" + mapName));

      final YAMLMapper mapper = new YAMLMapper();

      // Turn the Map read by SnakeYaml into a String so Jackson can read it.
      final String fixed = mapper.writeValueAsString(normalized);

      // Use a parameter name module so we don't have to specify name for every field.
      mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));

      // Add mix-ins
      mapper.registerModule(new WPIModule());
      mapper.registerModule(new JavaModule());

      // Deserialize the map into an object.
      return mapper.readValue(fixed, RobotMap.class);

    } catch (final IOException ex) {
      // The map file is either absent from the file system or improperly formatted.
      System.out.println("Config file is bad/nonexistent!");

      formatAndPrintMapException(ex);

      // Prevent watchdog from restarting by looping infinitely but only when on the robot is in a
      // simulation in order not to hang unit tests.
      if (RobotBase.isSimulation()) return null;
      // Suppress IntelliJ inspections.
      //noinspection InfiniteLoopStatement,StatementWithEmptyBody
      while (true) {}
    }
  }

  /**
   * Whether robot code is being unit tested. Note that this is NOT the same as test mode.
   *
   * <p>The return value will never change observably. {@link Robot#notifyTesting()} will thus
   * throw an exception if it is first called after the first time that this method is called.
   *
   * @return whether the current run is a unit test
   */
  public static boolean isUnitTesting() {
    isTestingHasBeenCalled = true;
    return isUnitTesting;
  }

  /**
   * Notifies robot code that it is being unit tested.
   *
   * @throws UnsupportedOperationException if the robot is not running in a simulation
   * @throws IllegalStateException if {@link Robot#isUnitTesting()} has already been called before
   * this method is first called
   */
  public static void notifyTesting() throws UnsupportedOperationException, IllegalStateException {
    if (RobotBase.isReal()) {
      throw new IllegalStateException(
          "Attempt to enable unit testing mode while not running in simulation");
    }

    if (isUnitTesting) return;

    if (isTestingHasBeenCalled) {
      throw new IllegalStateException("isTesting() has already been called at least once");
    }

    System.out.println("ROBOT UNIT TESTING");
    isUnitTesting = true;
  }

  /**
   * Formats and prints the stack trace of an exception raised by Jackson due to a problem with the
   * map
   *
   * @param ex the exception to format and print
   */
  private static void formatAndPrintMapException(final IOException ex) {
    final var out = new StringWriter();
    ex.printStackTrace(new PrintWriter(out));
    final String[] lines = out.toString().split("[\\r\\n]");

    for (final String line : lines) {
      if (line.length() == 0) continue;

      final String SEP = "->";

      if (MAP_REF_CHAIN_FORMAT == MapErrorFormat.NONE || !line.contains(SEP)) {
        System.err.println(line);
        continue;
      }

      final String[] links = line.split(SEP);

      // Remove the prefix from the first link and print it separately.
      final int prefixEndIndex = links[0].lastIndexOf(':');
      final String prefix = links[0].substring(0, prefixEndIndex + 1);
      links[0] = links[0].substring(prefixEndIndex + 2);
      System.err.println(prefix);

      // Remove the suffix (a closing parenthesis) from the last link.
      final String lastLink = links[links.length - 1];
      links[links.length - 1] = lastLink.substring(0, lastLink.length() - 1);

      switch (MAP_REF_CHAIN_FORMAT) {
        case LEFT_ALIGN:
        case RIGHT_ALIGN:
          final Optional<String> longest =
              Arrays.stream(links).max(Comparator.comparingInt(String::length));

          final int maxLinkLength = longest.get().length();
          final String linkFormat =
              "\t\t->%"
                  + (MAP_REF_CHAIN_FORMAT == MapErrorFormat.LEFT_ALIGN ? "" : maxLinkLength)
                  + "s\n";

          for (final String s : links) {
            System.err.format(linkFormat, s);
          }
          break;

        case TABLE:
          final List<List<String>> formattedLinks = new ArrayList<>(links.length);

          for (final String link : links) {
            // Each link is of the format className[location]
            final int locationBegin = link.lastIndexOf('[');

            final String location = link.substring(locationBegin + 1, link.length() - 1);
            final String className = link.substring(0, locationBegin);

            formattedLinks.add(List.of(String.format("\t\t-> %s", location), className));
          }

          System.err.print(formatTable(formattedLinks));
          break;
      }
    }
  }

  /**
   * Converts a {@code String[][]} representation of a table to its {@code String} representation
   * where every column is the same width as the widest cell in that column.
   *
   * @param rows a {@code String[][]} containing the data of the table stored in row-major order
   * @return a {@code String} containing the result of formatting the table
   */
  private static String formatTable(final List<List<String>> rows) {
    final int columnCount = rows.get(0).size();

    final StringBuilder sb = new StringBuilder();

    // Make each column the same width as the widest cell in it.
    final int[] maxColumnWidths = new int[columnCount];
    for (final var row : rows) {
      for (int column = 0; column < columnCount; column++) {
        maxColumnWidths[column] = Math.max(maxColumnWidths[column], row.get(column).length() + 1);
      }
    }

    // Write to the StringBuilder row by row.
    for (final var row : rows) {
      for (int column = 0; column < columnCount; column++) {
        // Use the thin vertical box-drawing character.
        sb.append(String.format("%-" + maxColumnWidths[column] + "s", row.get(column)));
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  @Override
  public void robotInit() {
    // Set up start time
    Clock.setStartTime();

    // Yes this should be a print statement, it's useful to know that robotInit started.
    System.out.println("Started robotInit.");

    if (this.robotMap.useCameraServer()) {
      CameraServer.getInstance().startAutomaticCapture();
    }

    // Read sensors
    this.robotMap.getUpdater().run();

    Logger.configureLoggingAndConfig(this.robotMap, false);
    Shuffleboard.setRecordingFileNameFormat("log-${time}");
    Shuffleboard.startRecording();

    // start systems
    if (this.robotMap.getRobotStartupCommands() != null) {
      this.robotMap.getRobotStartupCommands().forEachRemaining(Command::schedule);
    }
  }

  @Override
  public void robotPeriodic() {
    // save current time
    Clock.updateTime();
    // Read sensors
    this.robotMap.getUpdater().run();
    // update shuffleboard
    Logger.updateEntries();
    // Run all commands. This is a WPILib thing you don't really have to worry about.
    CommandScheduler.getInstance().run();
  }

  /** Run when we first enable in teleop. */
  @Override
  public void teleopInit() {
    // cancel remaining auto commands
    if (this.robotMap.getAutoStartupCommands() != null) {
      this.robotMap.getAutoStartupCommands().forEachRemaining(Command::cancel);
    }

    // Run teleop startup commands
    if (this.robotMap.getTeleopStartupCommands() != null) {
      this.robotMap.getTeleopStartupCommands().forEachRemaining(Command::schedule);
    }
  }

  /** Run when we first enable in autonomous */
  @Override
  public void autonomousInit() {
    // Run the auto startup command
    if (this.robotMap.getAutoStartupCommands() != null
        && !DriverStation.getInstance().getGameSpecificMessage().isEmpty()) {
      this.robotMap.getAutoStartupCommands().forEachRemaining(Command::schedule);
    }
  }

  /** Run when we first enable in test mode. */
  @Override
  public void testInit() {
    // Run startup command if we start in test mode.
    if (this.robotMap.getTestStartupCommands() != null) {
      this.robotMap.getTestStartupCommands().forEachRemaining(Command::schedule);
    }
  }

  /** Formatting for map reference chain of exception caused by map error. */
  private enum MapErrorFormat {
    /** The chain is printed as-is on one line. */
    NONE,
    /** The chain is split up into one frame per line and left-justified. */
    LEFT_ALIGN,
    /** The chain is split up into one frame per line and right-justified. */
    RIGHT_ALIGN,
    /**
     * The chain is split up into one frame per line and formatted as a table with locations to the
     * left of class names.
     */
    TABLE
  }
}
