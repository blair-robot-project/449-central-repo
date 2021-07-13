package org.usfirst.frc.team449.robot.mixIn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;

import java.util.function.BooleanSupplier;

/**
 * A mix-in for {@link Command} that annotates its constructor for use with Jackson. Don't make
 * sublasses of this.
 */
public abstract class ConditionalCommandMixIn {

  /** @see ConditionalCommand#ConditionalCommand(Command, Command, BooleanSupplier) */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ConditionalCommandMixIn(
      @JsonProperty(value = "onTrue", required = true) final Command onTrue,
      @JsonProperty(value = "onFalse", required = true) final Command onFalse,
      @JsonProperty(value = "condition", required = true) final BooleanSupplier condition) {}
}
