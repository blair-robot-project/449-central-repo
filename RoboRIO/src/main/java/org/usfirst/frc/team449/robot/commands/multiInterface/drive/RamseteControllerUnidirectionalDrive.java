package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import io.github.oblarg.oblog.Loggable;
import org.usfirst.frc.team449.robot.components.TrajectoryGenerationComponent;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectionalWithGyro;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RamseteControllerUnidirectionalDrive extends RamseteCommand implements Loggable {

  private DriveUnidirectionalWithGyro driveTrain;
  private NetworkTable falconDashboard;

  @JsonCreator
  public RamseteControllerUnidirectionalDrive(
      @JsonProperty(required = true) DriveUnidirectionalWithGyro drivetrain,
      @JsonProperty(required = true) double P,
      @JsonProperty(required = true) double D,
      @JsonProperty(required = true) TrajectoryGenerationComponent trajectoryGenerator) {
    super(
        trajectoryGenerator.getTrajectory(),
        drivetrain::getCurrentPose,
        new RamseteController(),
        drivetrain.getLeftFeedforwardCalculator(),
        drivetrain.getDriveKinematics(),
        drivetrain::getWheelSpeeds,
        new PIDController(P, 0, D),
        new PIDController(P, 0, D),
        drivetrain::setVoltage);

    this.driveTrain = drivetrain;
    addRequirements(driveTrain);

    driveTrain.resetOdometry(trajectoryGenerator.getTrajectory().getInitialPose());

    falconDashboard = NetworkTableInstance.getDefault().getTable("Live_Dashboard");
    // todo hook into timer to get expected pose at each
    falconDashboard.getEntry("isFollowingPath").setBoolean(true);
    //
    // falconDashboard.getEntry("pathX").setDouble(trajectoryGenerator.getTrajectory().sample(0).poseMeters.getTranslation().getX());
  }

  @Override
  public void execute() {
    super.execute();

    // update falcondashboard
    falconDashboard
        .getEntry("robotX")
        .setDouble(Units.metersToFeet(driveTrain.getCurrentPose().getTranslation().getX()));
    falconDashboard
        .getEntry("robotY")
        .setDouble(Units.metersToFeet(driveTrain.getCurrentPose().getTranslation().getY()));
    falconDashboard
        .getEntry("robotHeading")
        .setDouble(driveTrain.getCurrentPose().getRotation().getRadians());
    System.out.println(falconDashboard.getEntry("robotHeading").getDouble(0));
  }

  @Override
  public void end(boolean interrupted) {
    super.end(interrupted);
    falconDashboard.getEntry("isFollowingPath").setBoolean(false);
    driveTrain.fullStop();
  }
}
