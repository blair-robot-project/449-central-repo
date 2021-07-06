package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.CentripetalAccelerationConstraint;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.trajectory.constraint.TrajectoryConstraint;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectionalWithGyro;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public class TrajectoryGenerationCubicComponent implements TrajectoryGenerationComponent {

  DriveUnidirectionalWithGyro drivetrain;
  double maxSpeedMeters;
  double maxAccelMeters;
  double maxCentripitalAcceleration;
  Trajectory trajectory;

  @JsonCreator
  public TrajectoryGenerationCubicComponent(
      @JsonProperty(required = true) final DriveUnidirectionalWithGyro drivetrain,
      @JsonProperty(required = true) final double maxSpeedMeters,
      @JsonProperty(required = true) final double maxAccelMeters,
      @JsonProperty(required = true) final MappedTranslationSet waypoints,
      @Nullable Double maxCentripetalAcceleration,
      boolean reversed) {

    this.drivetrain = drivetrain;
    this.maxAccelMeters = maxAccelMeters;
    this.maxSpeedMeters = maxSpeedMeters;

    TrajectoryConstraint voltageConstraint =
        new DifferentialDriveVoltageConstraint(
            drivetrain.getLeftFeedforwardCalculator(), drivetrain.getDriveKinematics(), 12);

    // Create config for trajectory
    TrajectoryConfig configuration =
        new TrajectoryConfig(maxSpeedMeters, maxAccelMeters)
            .setKinematics(drivetrain.getDriveKinematics())
            .addConstraint(voltageConstraint)
            .setReversed(reversed);

    if (maxCentripetalAcceleration != null) {
      configuration.addConstraint(
          new CentripetalAccelerationConstraint(maxCentripetalAcceleration));
      this.maxCentripitalAcceleration = maxCentripetalAcceleration;
    }

    trajectory =
        TrajectoryGenerator.generateTrajectory(
            waypoints.getStartingPose(),
            waypoints.getTranslations(),
            waypoints.getEndingPose(),
            configuration);
  }

  @Override
  public Trajectory getTrajectory() {
    return trajectory;
  }
}
