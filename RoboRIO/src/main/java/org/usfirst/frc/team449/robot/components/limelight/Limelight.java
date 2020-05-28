package org.usfirst.frc.team449.robot.components.limelight;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Has all of the methods for getting any desired value from the limelight. Poorly documented,
 * reference docs.limelightvision.io/en/latest/networktables_api.html for full documentation
 */
public class Limelight {

  /** The network table to get the values from */
  private static NetworkTable netTable;

  static {
    netTable = NetworkTableInstance.getDefault().getTable("limelight");
  }

  /**
   * Gets the value from the corresponding param from netTable. Used exclusively to avoid typing
   * "netTable.getEntry(param).getDouble(0)" for every method
   *
   * @param param the value to ask for from the limelight
   * @return the value asked for
   */
  private static double tableGet(String param) {
    return netTable.getEntry(param).getDouble(0);
  }

  /** @return whether the limelight can see valid targets (0 or 1) */
  public static double validTargets() {
    return tableGet("tv");
  }

  /** @return horizontal offset of target (as an angle) */
  public static double getXOffset() {
    return tableGet("tx");
  }

  /** @return vertical offset from crosshair to target (as an angle) */
  public static double getYOffset() {
    return tableGet("ty");
  }

  /** @return area of target (as a percent of the image onscreen) */
  public static double getTargetArea() {
    return tableGet("ta");
  }

  /** @return skew or rotation (-90 to 0, in degrees) */
  public static double getSkew() {
    return tableGet("ts");
  }

  public static double getLatency() {
    return tableGet("tl");
  }

  public static double getShortest() {
    return tableGet("tshort");
  }

  public static double getLongest() {
    return tableGet("tlong");
  }

  public static double getHorizontalSideLen() {
    return tableGet("thor");
  }

  public static double getVerticalSideLen() {
    return tableGet("tvert");
  }

  public static double getPipelineIndex() {
    return tableGet("getpipe");
  }

  public static double getCamTran() {
    return tableGet("camtran");
  }
}
