package org.usfirst.frc.team449.robot.components.limelight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;

/** The component that supplies distances from the limelight to a vision target */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LimelightComponent implements DoubleSupplier {

/** Whether the limelight has a valid target in sight. Will return 0 for no, 1 for yes */
  private static final NetworkTableEntry tv =
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv");
    /**
   * Which value to ask from the limelight Can be: x: lateral offset from target, in degrees y:
   * vertical offset from target, in degrees area: area of the target (0% to 100% of the camera
   * screen) skew: rotation (-90 to 0, in degrees) of the target (as the limelight sees it) latency:
   * the pipeline's latency contribution, in ms shortest: sidelength of the shortest side of the
   * vision target box, in pixels longest: sidelength of the longest side of the vision target box,
   * in pixels width: width of target box, in pixels height: height of target box, in pixels
   * pipeIndex: pipelineIndex of the camera poseX: x position of the target in a 3D model poseY: y
   * position of the target in a 3D model poseZ: z position of the target in a 3D model pitch: angle
   * from limelight to target on x-z coordinate plane, determined by the 3D model from camtran yaw:
   * same as pitch, but on x-y coordinate plane roll: same as pitch, on y-z coordinate plane
   */
  private final ReturnValue value;
  /** Added to the output double */
  private final double offset;
  /** The NetworkTableEntry that supplies the desired value Determined by the ReturnValue value */
  @NotNull NetworkTableEntry entry;

  /**
   * Default creator
   *
   * @param value what to request from the Limelight
   */
  @JsonCreator
  public LimelightComponent(@JsonProperty(required = true) ReturnValue value, double offset) {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    this.value = value;
    this.offset = offset;
    switch (value) {
      case x:
        entry = table.getEntry("tx");
        break;
      case y:
        entry = table.getEntry("ty");
        break;
      case area:
        entry = table.getEntry("ta");
        break;
      case skew:
        entry = table.getEntry("ts");
        break;
      case latency:
        entry = table.getEntry("tl");
        // Offset should be 0
        offset = 0;
        break;
      case shortest:
        entry = table.getEntry("tshort");
        // Offset should be 0
        offset = 0;
        break;
      case width:
        entry = table.getEntry("thor");
        // Offset should be 0
        offset = 0;
        break;
      case height:
        entry = table.getEntry("tvert");
        // Offset should be 0
        offset = 0;
        break;
      case pipeIndex:
        entry = table.getEntry("getpipe");
        // Offset should be 0
        offset = 0;
        break;
      default:
        entry = table.getEntry("camtran");
    }
  }

  /**
   * Sets the pipeline of the limelight
   *
   * @param index the index to set the pipeline to
   */
  public static void setPipeline(int index) {
    LimelightComponent pipeline =
        new LimelightComponent(ReturnValue.pipeIndex, 0);
    pipeline.entry.setNumber(index);
  }

  public static boolean hasTarget() {
    return tv.getBoolean(false);
  }

  /** @return requested value from LimeLight */
  @Override
  public double getAsDouble() {
    if (tv.getDouble(0) == 0) {
      return Double.NaN;
    }
    double[] camtran = entry.getDoubleArray(new double[6]);
    switch (value) {
      case poseX:
        return camtran[0] + offset;
      case poseY:
        return camtran[1] + offset;
      case poseZ:
        return camtran[2] + offset;
      case pitch:
        return camtran[3] + offset;
      case yaw:
        return camtran[4] + offset;
      case roll:
        return camtran[5] + offset;
      default:
        return entry.getDouble(0.0) + offset;
    }
  }

  public enum ReturnValue {
    x,
    y,
    area,
    skew,
    latency,
    shortest,
    longest,
    width,
    height,
    pipeIndex,
    poseX,
    poseY,
    poseZ,
    pitch,
    yaw,
    roll
  }
}
