package org.usfirst.frc.team449.robot.subsystem.singleImplementation.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedUsbCamera;

/** Subsystem to initialize cameras and put video on Shuffleboard. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CameraNetwork extends SubsystemBase implements Loggable {

  /** Video server to view on Shuffleboard. */
  @NotNull private final MjpegServer server;

  /** List of cameras used on the robot. */
  @NotNull private final List<MappedUsbCamera> cameras;

  /** Camera currently being streamed from. */
  private int camNum;

  /**
   * Default constructor
   *
   * @param serverPort The port of the {@link MjpegServer} this subsystem uses.
   * @param serverName The human-friendly name of the {@link MjpegServer} this subsystem uses.
   * @param cameras The cameras this subsystem controls.
   */
  @JsonCreator
  public CameraNetwork(
      @JsonProperty(required = true) int serverPort,
      @NotNull @JsonProperty(required = true) String serverName,
      @NotNull @JsonProperty(required = true) List<MappedUsbCamera> cameras) {
    // Logging
    Shuffleboard.addEventMarker(
        "CameraSubsystem construct start",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("CameraSubsystem construct start", this.getClass());

    Shuffleboard.addEventMarker(
        "Set URL of MJPGServer to \\\"http://roboRIO-449-frc.local:\" + serverPort +\n"
            + "                \"/stream.mjpg\\\"",
        this.getClass().getSimpleName(),
        EventImportance.kNormal);
    // Logger.addEvent("Set URL of MJPGServer to \"http://roboRIO-449-frc.local:" + serverPort +
    // "/stream.mjpg\"", this.getClass());

    // Instantiates server
    server = new MjpegServer(serverName, serverPort);

    // Instantiates cameras
    this.cameras = cameras;

    // Starts streaming video from first camera, marks that via camNum
    server.setSource(cameras.get(0));
    camNum = 0;

    // Logging
    Shuffleboard.addEventMarker(
        "CameraSubsystem construct end", this.getClass().getSimpleName(), EventImportance.kNormal);
    // Logger.addEvent("CameraSubsystem construct end", this.getClass());
  }

  /** @return Video server to view on Shuffleboard. */
  @NotNull
  @Log
  public MjpegServer getServer() {
    return server;
  }

  /** @return List of cameras used on the robot. */
  @NotNull
  @Log
  public List<MappedUsbCamera> getCameras() {
    return cameras;
  }

  /** @return The index of the active camera in the list of cameras. */
  @Log
  public int getCamNum() {
    return camNum;
  }

  /** @param camNum The index of the camera to make active in the list of cameras. */
  public void setCamNum(int camNum) {
    this.camNum = camNum;
  }
}
