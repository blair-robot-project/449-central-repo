package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DigitalInput;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import java.util.function.BooleanSupplier;

/** A roboRIO digital input pin. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedDigitalInput extends DigitalInput implements Loggable, BooleanSupplier {

  /**
   * Create an instance of a Digital Input class. Creates a digital input given a channel.
   *
   * @param channel the DIO channel for the digital input 0-9 are on-board, 10-25 are on the MXP
   */
  @JsonCreator
  public MappedDigitalInput(@JsonProperty(required = true) int channel) {
    super(channel);
  }

  /**
   * Get the value from a digital input channel. Retrieve the value of a single digital input
   * channel from the FPGA.
   *
   * @return the status of the digital input
   */
  @Override
  @Log
  public boolean get() {
    return !super.get(); // true is off by default in WPILib, and that's dumb
  }

  /**
   * Returns the result of {@link MappedDigitalInput#get()}.
   *
   * @return the return value of {@link MappedDigitalInput#get()}
   */
  @Override
  public boolean getAsBoolean() {
    return this.get();
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
  //        return new String[]{"value"};
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
  //        return new Object[]{this.get()};
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
  //        return "DigitalInput" + this.getChannel();
  //    }
}
