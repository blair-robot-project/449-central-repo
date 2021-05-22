package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DigitalInput;
import io.github.oblarg.oblog.Loggable;

import java.util.function.BooleanSupplier;

/**
 * A roboRIO digital input pin.
 * Logging currently disabled b/c I xcouldn't figure out why it didn't like me using 2 of them - Jade
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedDigitalInput extends DigitalInput implements Loggable, BooleanSupplier {

  /**
   * Create an instance of a Digital Input class. Creates a digital input given a channel.
   *
   * @param channel the DIO channel for the digital input 0-9 are on-board, 10-25 are on the MXP
   */
  @JsonCreator
  public MappedDigitalInput(@JsonProperty(required = true) final int channel) {
    super(channel);
  }

  /**
   * Get the value from a digital input channel. Retrieve the value of a single digital input
   * channel from the FPGA.
   *
   * @return the status of the digital input
   */
  @Override
//  @Log
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

}
