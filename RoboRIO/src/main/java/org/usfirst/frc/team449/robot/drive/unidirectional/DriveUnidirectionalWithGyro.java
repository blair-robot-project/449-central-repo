package org.usfirst.frc.team449.robot.drive.unidirectional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.AHRS.SubsystemAHRS;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;

/** A drive with a cluster of any number of CANTalonSRX controlled motors on each side. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveUnidirectionalWithGyro extends SubsystemBase
    implements SubsystemAHRS, DriveUnidirectional, Loggable {

  /** Right master Talon */
  @NotNull protected final SmartMotor rightMaster;

  /** Left master Talon */
  @NotNull protected final SmartMotor leftMaster;

  /** The NavX gyro */
  @NotNull private final MappedAHRS ahrs;

  /** Drivetrain kinematics processor for measuring individual wheel speeds */
  private final DifferentialDriveKinematics driveKinematics;

  /** Drivetrain odometry tracker for tracking position */
  private final DifferentialDriveOdometry driveOdometry;
  /** Whether or not to use the NavX for driving straight */
  private boolean overrideGyro;
  /** Cached values for various sensor readings. */
  private double cachedLeftVel = Double.NaN;
  private double cachedRightVel = Double.NaN;
  private double cachedLeftPos = Double.NaN;
  private double cachedRightPos = Double.NaN;

  /**
   * Default constructor.
   *
   * @param leftMaster The master talon on the left side of the drive.
   * @param rightMaster The master talon on the right side of the drive.
   * @param ahrs The NavX gyro for calculating this drive's heading and angular velocity.
   * @param trackWidthMeters The width between the left and right wheels in meters
   */
  @JsonCreator
  public DriveUnidirectionalWithGyro(
      @NotNull @JsonProperty(required = true) final SmartMotor leftMaster,
      @NotNull @JsonProperty(required = true) final SmartMotor rightMaster,
      @NotNull @JsonProperty(required = true) final MappedAHRS ahrs,
      @JsonProperty(required = true) final double trackWidthMeters) {
    super();
    // Initialize stuff
    this.rightMaster = rightMaster;
    this.leftMaster = leftMaster;
    this.ahrs = ahrs;
    this.overrideGyro = false;
    this.driveKinematics = new DifferentialDriveKinematics(trackWidthMeters);
    this.driveOdometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(this.getHeading()));
  }

  @Override
  public void periodic() {
    updateOdometry();
  }

  /**
   * Set the output of each side of the drive.
   *
   * @param left The output for the left side of the drive, from [-1, 1]
   * @param right the output for the right side of the drive, from [-1, 1]
   */
  @Override
  public void setOutput(final double left, final double right) {
    // scale by the max speed
    this.leftMaster.setVelocity(left);
    this.rightMaster.setVelocity(right);
  }

  /**
   * Set voltage output raw
   *
   * @param left The voltage output for the left side of the drive from [-12, 12]
   * @param right The voltage output for the right side of the drive from [-12, 12]
   */
  public void setVoltage(final double left, final double right) {
    leftMaster.setVoltage(left);
    rightMaster.setVoltage(right);
  }

  /**
   * Get the velocity of the left side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @Override
  @NotNull
  public Double getLeftVel() {
    return this.leftMaster.getVelocity();
  }

  /**
   * Get the velocity of the right side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @Override
  @NotNull
  public Double getRightVel() {
    return this.rightMaster.getVelocity();
  }

  /**
   * Get the position of the left side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @NotNull
  @Override
  public Double getLeftPos() {
    return this.leftMaster.getPositionUnits();
  }

  /**
   * Get the position of the right side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @NotNull
  @Override
  public Double getRightPos() {
    return this.rightMaster.getPositionUnits();
  }

  /**
   * Get the cached velocity of the left side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @NotNull
  @Override
  public Double getLeftVelCached() {
    return this.cachedLeftVel;
  }

  /**
   * Get the cached velocity of the right side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @NotNull
  @Override
  public Double getRightVelCached() {
    return this.cachedRightVel;
  }

  /**
   * Get the cached position of the left side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @NotNull
  @Override
  public Double getLeftPosCached() {
    return this.cachedLeftPos;
  }

  /**
   * Get the cached position of the right side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @NotNull
  @Override
  public Double getRightPosCached() {
    return this.cachedRightPos;
  }

  /** @return The feedforward calculator for left motors */
  public SimpleMotorFeedforward getLeftFeedforwardCalculator() {
    return this.leftMaster.getCurrentGearFeedForward();
  }

  /** @return The feedforward calculator for right motors */
  public SimpleMotorFeedforward getRightFeedforwardCalculator() {
    return this.rightMaster.getCurrentGearFeedForward();
  }

  /** Completely stop the robot by setting the voltage to each side to be 0. */
  @Override
  public void fullStop() {
    this.leftMaster.setPercentVoltage(0);
    this.rightMaster.setPercentVoltage(0);
  }

  /** If this drive uses motors that can be disabled, enable them. */
  @Override
  public void enableMotors() {
    this.leftMaster.enable();
    this.rightMaster.enable();
  }

  /**
   * Get the robot's heading using the AHRS
   *
   * @return robot heading, in degrees, on [-180, 180]
   */
  @Override
  public double getHeading() {
    return this.ahrs.getHeading();
  }

  /**
   * Set the robot's heading.
   *
   * @param heading The heading to set to, in degrees on [-180, 180].
   */
  @Override
  public void setHeading(final double heading) {
    this.ahrs.setHeading(heading);
  }

  /**
   * Get the robot's cached heading.
   *
   * @return robot heading, in degrees, on [-180, 180].
   */
  @Override
  public double getHeadingCached() {
    return this.ahrs.getCachedHeading();
  }

  /**
   * Get the robot's angular velocity.
   *
   * @return Angular velocity in degrees/sec
   */
  @Override
  public double getAngularVel() {
    return this.ahrs.getAngularVelocity();
  }

  /**
   * Get the robot's cached angular velocity.
   *
   * @return Angular velocity in degrees/sec
   */
  @Override
  public double getAngularVelCached() {
    return this.ahrs.getCachedAngularVelocity();
  }

  /**
   * Get the robot's angular displacement since being turned on.
   *
   * @return Angular displacement in degrees.
   */
  @Override
  public double getAngularDisplacement() {
    return this.ahrs.getAngularDisplacement();
  }

  /**
   * Get the robot's cached angular displacement since being turned on.
   *
   * @return Angular displacement in degrees.
   */
  @Override
  public double getAngularDisplacementCached() {
    return this.ahrs.getCachedAngularDisplacement();
  }

  /**
   * Get the pitch value.
   *
   * @return The pitch, in degrees from [-180, 180]
   */
  @Override
  public double getPitch() {
    return this.ahrs.getPitch();
  }

  /**
   * Get the cached pitch value.
   *
   * @return The pitch, in degrees from [-180, 180]
   */
  @Override
  public double getCachedPitch() {
    return this.ahrs.getCachedPitch();
  }

  /** @return true if the NavX is currently overriden, false otherwise. */
  @Override
  @Log
  public boolean getOverrideGyro() {
    return this.overrideGyro;
  }

  /** @param override true to override the NavX, false to un-override it. */
  @Override
  public void setOverrideGyro(final boolean override) {
    this.overrideGyro = override;
  }

  /** Reset odometry tracker to current robot pose */
  @Log
  public void resetOdometry(final Pose2d pose) {
    resetPosition();
    ahrs.setHeading(pose.getRotation().getDegrees());
    driveOdometry.resetPosition(pose, Rotation2d.fromDegrees(this.getHeading()));
  }

  /** Update odometry tracker with current heading, and encoder readings */
  public void updateOdometry() {
    // need to convert to meters
    this.driveOdometry.update(
        Rotation2d.fromDegrees(this.getHeading()), this.getLeftPos(), this.getRightPos());
  }

  /** @return Current estimated pose based on odometry tracker data */
  @Log.ToString
  public Pose2d getCurrentPose() {
    return this.driveOdometry.getPoseMeters() != null
        ? this.driveOdometry.getPoseMeters()
        : new Pose2d(new Translation2d(0, 0), new Rotation2d(0));
  }

  /** @return Current wheel speeds based on encoder readings for future pose correction */
  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    // need to convert to meters
    return new DifferentialDriveWheelSpeeds(this.getLeftVel(), this.getRightVel());
  }

  /** @return Kinematics processor for wheel speeds */
  public DifferentialDriveKinematics getDriveKinematics() {
    return this.driveKinematics;
  }

  /** Disable the motors. */
  public void disable() {
    this.leftMaster.disable();
    this.rightMaster.disable();
  }

  /**
   * Hold the current position.
   *
   * @param pos the position to stop at
   */
  public void holdPosition(final double pos) {
    this.holdPosition(pos, pos);
  }

  /** Reset the position of the drive if it has encoders. */
  @Override
  public void resetPosition() {
    this.leftMaster.resetPosition();
    this.rightMaster.resetPosition();
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    this.cachedLeftVel = this.getLeftVel();
    this.cachedLeftPos = this.getLeftPos();
    this.cachedRightVel = this.getRightVel();
    this.cachedRightPos = this.getRightPos();
  }

  /**
   * Hold the current position.
   *
   * @param leftPos the position to stop the left side at
   * @param rightPos the position to stop the right side at
   */
  public void holdPosition(final double leftPos, final double rightPos) {
    this.leftMaster.setPositionSetpoint(leftPos);
    this.rightMaster.setPositionSetpoint(rightPos);
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName()
        + "_"
        + this.leftMaster.getPort()
        + "_"
        + this.rightMaster.getPort();
  }

  @Override
  public String configureLogName() {
    return this.getName();
  }
}
