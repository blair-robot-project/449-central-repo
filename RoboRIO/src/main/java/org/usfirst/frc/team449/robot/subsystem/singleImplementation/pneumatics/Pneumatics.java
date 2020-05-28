package org.usfirst.frc.team449.robot.subsystem.singleImplementation.pneumatics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.PressureSensor;

/**
 * A subsystem representing the pneumatics control system (e.g. the compressor and maybe a pressure
 * sensor)
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Pneumatics extends SubsystemBase implements Loggable {

  /** The compressor that provides pressure to the robot's pneumatics. */
  @NotNull private final Compressor compressor;

  /** The pressure sensor that reads the pneumatic pressure. */
  @Nullable private final PressureSensor pressureSensor;

  /**
   * Default constructor
   *
   * @param nodeID The node ID of the compressor.
   * @param pressureSensor The pressure sensor attached to this pneumatics system. Can be null.
   */
  @JsonCreator
  public Pneumatics(
      @JsonProperty(required = true) int nodeID, @Nullable PressureSensor pressureSensor) {
    compressor = new Compressor(nodeID);
    this.pressureSensor = pressureSensor;
  }

  /** Start up the compressor in closed loop control mode. */
  public void startCompressor() {
    compressor.setClosedLoopControl(true);
    compressor.start();
  }

  /** Stop the compressor. */
  public void stopCompressor() {
    compressor.setClosedLoopControl(false);
    compressor.stop();
  }

  //    /**
  //     * Get the headers for the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of String labels for data, where N is the length of the
  // Object[] returned by getData().
  //     */
  //    @NotNull
  //    @Override
  //    public String[] getHeader() {
  //        return new String[]{"pressure"};
  //    }
  //
  //    /**
  //     * Get the data this subsystem logs every loop.
  //     *
  //     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
  //     */
  //    @NotNull
  //    @Override
  //    public Object[] getData() {
  //        if (pressureSensor == null) {
  //            return new Object[]{"N/A"};
  //        } else {
  //            return new Object[]{pressureSensor.getPressure()};
  //        }
  //    }
  //
  //    /**
  //     * Get the name of this object.
  //     *
  //     * @return A string that will identify this object in the log file.
  //     */
  //    @NotNull
  //    @Override
  //    public String getLogName() {
  //        return "pneumatics";
  //    }

  @Log
  public double getPressure() {
    if (pressureSensor == null) {
      return -1;
    } else {
      return pressureSensor.getPressure();
    }
  }
}
