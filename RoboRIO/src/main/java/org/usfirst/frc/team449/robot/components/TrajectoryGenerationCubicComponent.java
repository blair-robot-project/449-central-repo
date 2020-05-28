package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.trajectory.constraint.TrajectoryConstraint;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectionalWithGyro;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedTranslationSet;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public class TrajectoryGenerationCubicComponent implements TrajectoryGenerationComponent {

  TrajectoryConstraint constraint;
  TrajectoryConfig configuration;
  MappedTranslationSet translations;
  Trajectory trajectory;

  @JsonCreator
  public TrajectoryGenerationCubicComponent(
      @JsonProperty(required = true) final DriveUnidirectionalWithGyro drivetrain,
      @JsonProperty(required = true) final double maxSpeedMeters,
      @JsonProperty(required = true) final double maxAccelMeters,
      @JsonProperty(required = true) final MappedTranslationSet waypoints) {
    this.constraint =
        new DifferentialDriveVoltageConstraint(
            drivetrain.getLeftFeedforwardCalculator(), drivetrain.getDriveKinematics(), 12);

    // Create config for trajectory
    this.configuration =
        new TrajectoryConfig(maxSpeedMeters, maxAccelMeters)
            .setKinematics(drivetrain.getDriveKinematics())
            .addConstraint(this.constraint);

    this.translations = waypoints;
  }

  @Override
  public Trajectory getTrajectory() {
    this.trajectory =
        TrajectoryGenerator.generateTrajectory(
            this.translations.getStartingPose(),
            this.translations.getTranslations(),
            this.translations.getEndingPose(),
            this.configuration);
    return this.trajectory;
  }
}
