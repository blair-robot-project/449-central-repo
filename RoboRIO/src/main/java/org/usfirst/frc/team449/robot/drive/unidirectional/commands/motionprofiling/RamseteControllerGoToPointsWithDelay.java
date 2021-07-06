package org.usfirst.frc.team449.robot.drive.unidirectional.commands.motionprofiling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectionalWithGyro;

import java.util.Collections;
import java.util.List;

/**
 * Go to a bunch of positions, but stop a while each time.
 * WARNING: THIS CODE IS UNTESTED!! DON'T EXPECT IT TO WORK FLAWLESSLY!
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RamseteControllerGoToPointsWithDelay extends SequentialCommandGroup {

  @JsonCreator
  public RamseteControllerGoToPointsWithDelay(
      @JsonProperty(required = true) DriveUnidirectionalWithGyro drivetrain,
      @JsonProperty(required = true) final double maxSpeedMeters,
      @JsonProperty(required = true) final double maxAccelMeters,
      Double maxCentripetalAcceleration,
      @JsonProperty(required = true) final double waitSeconds,
      @JsonProperty(required = true) MappedPIDController leftPidController,
      @JsonProperty(required = true) MappedPIDController rightPidController,
      @JsonProperty(required = true) List<Pose2d> poses,
      boolean reversed) {
    super(intersperseWithWaitCommands(drivetrain, maxSpeedMeters, maxAccelMeters,
        maxCentripetalAcceleration, waitSeconds, leftPidController, rightPidController, poses,
        reversed));
    addRequirements(drivetrain);
  }

  private static Command[] intersperseWithWaitCommands(
      @JsonProperty(required = true) DriveUnidirectionalWithGyro drivetrain,
      final double maxSpeedMeters,
      final double maxAccelMeters,
      Double maxCentripetalAcceleration,
      final double waitSeconds,
      MappedPIDController leftPidController,
      MappedPIDController rightPidController,
      List<Pose2d> poses,
      boolean reversed) {
    int numPoses = poses.size();
    Command[] res = new Command[poses.size() * 2];
//    Command waitCommand = new WaitCommand(waitSeconds);

    for (int i = 0; i < numPoses; i++) {
      res[i * 2] = new RamseteControllerGoToPosition(
          drivetrain,
          maxSpeedMeters,
          maxAccelMeters,
          maxCentripetalAcceleration,
          leftPidController,
          rightPidController,
          poses.get(i),
          Collections.emptyList(),
          reversed
      );
      res[i * 2 + 1] = new WaitCommand(waitSeconds);//waitCommand;
    }

    return res;
  }

}
