package org.usfirst.frc.team449.robot.subsystem.singleImplementation.pneumatics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Compressor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.jacksonWrappers.PressureSensor;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A subsystem representing the pneumatics control system (e.g. the compressor and maybe a pressure sensor)
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Pneumatics extends YamlSubsystem implements Loggable {

    /**
     * The compressor that provides pressure to the robot's pneumatics.
     */
    @NotNull
    private final Compressor compressor;

    /**
     * The pressure sensor that reads the pneumatic pressure.
     */
    @Nullable
    private final PressureSensor pressureSensor;

    /**
     * Default constructor
     *
     * @param nodeID         The node ID of the compressor.
     * @param pressureSensor The pressure sensor attached to this pneumatics system. Can be null.
     */
    @JsonCreator
    public Pneumatics(@JsonProperty(required = true) int nodeID,
                      @Nullable PressureSensor pressureSensor) {
        compressor = new Compressor(nodeID);
        this.pressureSensor = pressureSensor;
    }

    /**
     * Do nothing.
     */
    @Override
    public void initDefaultCommand() {
        //Do Nothing
    }

    /**
     * Start up the compressor in closed loop control mode.
     */
    public void startCompressor() {
        compressor.setClosedLoopControl(true);
        compressor.start();
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @NotNull
    @Override
    public String[] getHeader() {
        return new String[]{"pressure"};
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @NotNull
    @Override
    public Object[] getData() {
        if (pressureSensor == null) {
            return new Object[]{"N/A"};
        } else {
            return new Object[]{pressureSensor.getPressure()};
        }
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "pneumatics";
    }
}
