package org.usfirst.frc.team449.robot.commands.multiInterface.limelight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.commands.limelight.SetPipeline;
import org.usfirst.frc.team449.robot.commands.multiInterface.drive.UnidirectionalNavXDefaultDrive;
import org.usfirst.frc.team449.robot.components.limelight.LimelightComponent;

/**
 * Turns on the limelight LEDs and starts scanning for a target When one is found, it overrides the
 * default drive command and turns to that target
 */
public class ScanThenTurn extends SequentialCommandGroup {

  /**
   * Default constructor
   *
   * @param scannerPipe the pipeline index used while scanning
   * @param driveCommand the default drive command in map
   * @param limelightCommand the command that uses the limelight (turn to vision, drive to vision,
   *     etc) as of 2020, only one is turnToAngleLimelight, so that's what this should be
   * @param driverPipe the pipeline index that turns the LEDs off and which the driver uses as a
   *     camera
   */
  @JsonCreator
  public ScanThenTurn(
      @JsonProperty(required = true) int scannerPipe,
      @NotNull @JsonProperty(required = true) UnidirectionalNavXDefaultDrive driveCommand,
      @NotNull @JsonProperty(required = true) Command limelightCommand,
      @JsonProperty(required = true) int driverPipe) {
    addCommands(
        new SetPipeline(scannerPipe),
        driveCommand.withInterrupt(LimelightComponent::hasTarget),
        limelightCommand,
        new SetPipeline(driverPipe));
  }
}
