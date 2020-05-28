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
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.SmartMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;
import org.usfirst.frc.team449.robot.subsystem.interfaces.AHRS.SubsystemAHRS;

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
  @Nullable private Double cachedLeftVel, cachedRightVel, cachedLeftPos, cachedRightPos;

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
      @NotNull @JsonProperty(required = true) SmartMotor leftMaster,
      @NotNull @JsonProperty(required = true) SmartMotor rightMaster,
      @NotNull @JsonProperty(required = true) MappedAHRS ahrs,
      @JsonProperty(required = true) double trackWidthMeters) {
    super();
    // Initialize stuff
    this.rightMaster = rightMaster;
    this.leftMaster = leftMaster;
    this.ahrs = ahrs;
    this.overrideGyro = false;
    this.driveKinematics = new DifferentialDriveKinematics(trackWidthMeters);
    this.driveOdometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(this.getHeading()));
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
    this.leftMaster.setPercentVoltage(left / 12);
    this.rightMaster.setPercentVoltage(right / 12);
  }

  /**
   * Get the velocity of the left side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @Override
  @Nullable
  public Double getLeftVel() {
    return this.leftMaster.getVelocity();
  }

  /**
   * Get the velocity of the right side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @Override
  @Nullable
  public Double getRightVel() {
    return this.rightMaster.getVelocity();
  }

  /**
   * Get the position of the left side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @Nullable
  @Override
  public Double getLeftPos() {
    return this.leftMaster.getPositionUnits();
  }

  /**
   * Get the position of the right side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @Nullable
  @Override
  public Double getRightPos() {
    return this.rightMaster.getPositionUnits();
  }

  /**
   * Get the cached velocity of the left side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @Nullable
  @Override
  public Double getLeftVelCached() {
    return this.cachedLeftVel;
  }

  /**
   * Get the cached velocity of the right side of the drive.
   *
   * @return The signed velocity in feet per second, or null if the drive doesn't have encoders.
   */
  @Nullable
  @Override
  public Double getRightVelCached() {
    return this.cachedRightVel;
  }

  /**
   * Get the cached position of the left side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @Nullable
  @Override
  public Double getLeftPosCached() {
    return this.cachedLeftPos;
  }

  /**
   * Get the cached position of the right side of the drive.
   *
   * @return The signed position in feet, or null if the drive doesn't have encoders.
   */
  @Nullable
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
    this.resetPosition();
    this.driveOdometry.resetPosition(pose, Rotation2d.fromDegrees(this.getHeading()));
  }

  /** Update odometry tracker with current heading, and encoder readings */
  public void updateOdometry() {
    // need to convert to meters
    this.driveOdometry.update(
        Rotation2d.fromDegrees(this.getHeading()),
        Units.feetToMeters(this.getLeftPos()),
        Units.feetToMeters(this.getRightPos()));
  }

  /** @return Current estimated pose based on odometry tracker data */
  public Pose2d getCurrentPose() {
    return this.driveOdometry.getPoseMeters() != null
        ? this.driveOdometry.getPoseMeters()
        : new Pose2d(new Translation2d(0, 0), new Rotation2d(0));
  }

  /** @return Current wheel speeds based on encoder readings for future pose correction */
  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    // need to convert to meters
    return new DifferentialDriveWheelSpeeds(
        Units.feetToMeters(this.getLeftVel()), Units.feetToMeters(this.getRightVel()));
  }
  //
  //    /**
  //     * Get the headers for the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of String labels for data, where N is the length of the
  // Object[] returned by getData().
  //     */
  //    @Override
  //    @NotNull
  //    @Contract(pure = true)
  //    public String[] getHeader() {
  //        return new String[]{"override_gyro"};
  //    }
  //
  //    /**
  //     * Get the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
  //     */
  //    @Override
  //    @NotNull
  //    public Object[] getData() {
  //        return new Object[]{getOverrideGyro()};
  //    }
  //
  //    /**
  //     * Get the name of this object.
  //     *
  //     * @return A string that will identify this object in the log file.
  //     */
  //    @Override
  //    @NotNull
  //    @Contract(pure = true)
  //    public String getLogName() {
  //        return "Drive";
  //    }

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
    this.updateOdometry();
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
