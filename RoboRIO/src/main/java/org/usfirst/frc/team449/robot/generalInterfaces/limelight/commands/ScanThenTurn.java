package org.usfirst.frc.team449.robot.generalInterfaces.limelight.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.commands.AHRS.NavXTurnToAngleLimelight;
import org.usfirst.frc.team449.robot.generalInterfaces.limelight.Limelight;

/**
 * Turns on the limelight LEDs and starts scanning for a target When one is found, it overrides the
 * default drive command and turns to that target
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ScanThenTurn extends CommandBase implements Loggable {

  private final Limelight limelight;
  private final int scannerPipe;
  private final NavXTurnToAngleLimelight<?> turnCommand;
  private final int driverPipe;

  /**
   * Default constructor
   *
   * @param scannerPipe the pipeline index used while scanning
   * @param turnCommand the command that uses the limelight (turn to vision, drive to vision, etc)
   * @param driverPipe the pipeline index that turns the LEDs off and which the driver uses as a
   *     camera
   */
  @JsonCreator
  public ScanThenTurn(
      @JsonProperty(required = true) int scannerPipe,
      @NotNull @JsonProperty(required = true) NavXTurnToAngleLimelight<?> turnCommand,
      @JsonProperty(required = true) int driverPipe,
      @JsonProperty(required = true) Limelight limelight) {
    this.limelight = limelight;
    this.scannerPipe = scannerPipe;
    this.turnCommand = turnCommand;
    this.driverPipe = driverPipe;
  }

  @Override
  public void initialize() {
    new SetPipeline(limelight, scannerPipe).schedule();
  }

  @Override
  public boolean isFinished() {
    return limelight.hasTarget();
  }

  @Override
  public void end(boolean interrupted) {
    new SetPipeline(limelight, driverPipe).schedule();

    if (!interrupted) {
      turnCommand.schedule();
    }
  }
}
