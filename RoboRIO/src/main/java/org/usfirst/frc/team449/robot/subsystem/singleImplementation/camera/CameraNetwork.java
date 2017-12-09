package org.usfirst.frc.team449.robot.subsystem.singleImplementation.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.cscore.MjpegServer;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedUsbCamera;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;

import java.util.List;

/**
 * Subsystem to initialize cameras and put video on SmartDashboard.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CameraNetwork extends YamlSubsystem {

    /**
     * Video server to view on SmartDashboard.
     */
    @NotNull
    private final MjpegServer server;

    /**
     * List of cameras used on the robot.
     */
    @NotNull
    private final List<MappedUsbCamera> cameras;

    /**
     * Camera currently being streamed from.
     */
    private int camNum;

    /**
     * Default constructor
     *
     * @param serverPort The port of the {@link MjpegServer} this subsystem uses.
     * @param serverName The human-friendly name of the {@link MjpegServer} this subsystem uses.
     * @param cameras    The cameras this subsystem controls.
     */
    @JsonCreator
    public CameraNetwork(@JsonProperty(required = true) int serverPort,
                         @NotNull @JsonProperty(required = true) String serverName,
                         @NotNull @JsonProperty(required = true) List<MappedUsbCamera> cameras) {
        //Logging
        Logger.addEvent("CameraSubsystem construct start", this.getClass());
        Logger.addEvent("Set URL of MJPGServer to \"http://roboRIO-449-frc.local:" + serverPort +
                "/stream.mjpg\"", this.getClass());

        //Instantiates server
        server = new MjpegServer(serverName, serverPort);

        //Instantiates cameras
        this.cameras = cameras;

        //Starts streaming video from first camera, marks that via camNum
        server.setSource(cameras.get(0));
        camNum = 0;

        //Logging
        Logger.addEvent("CameraSubsystem construct end", this.getClass());
    }

    /**
     * Initialize the default command for a subsystem. By default subsystems have no default command, but if they do,
     * the default command is set with this method. It is called on all Subsystems by CommandBase in the users program
     * after all the Subsystems are created.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }

    /**
     * @return Video server to view on SmartDashboard.
     */
    @NotNull
    public MjpegServer getServer() {
        return server;
    }

    /**
     * @return List of cameras used on the robot.
     */
    @NotNull
    public List<MappedUsbCamera> getCameras() {
        return cameras;
    }

    /**
     * @return The index of the active camera in the list of cameras.
     */
    public int getCamNum() {
        return camNum;
    }

    /**
     * @param camNum The index of the camera to make active in the list of cameras.
     */
    public void setCamNum(int camNum) {
        this.camNum = camNum;
    }
}
