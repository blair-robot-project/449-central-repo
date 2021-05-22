package org.usfirst.frc.team449.robot.components.booleanSuppliers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/** Intended to be used as a base class. */
public class BooleanSupplierBased implements BooleanSupplier {
  private final BooleanSupplier source;

  /** Default constructor. */
  @JsonCreator
  public BooleanSupplierBased(
      @NotNull @JsonProperty(required = true) final BooleanSupplier supplier) {
    this.source = supplier;
  }

  @Override
  public final boolean getAsBoolean() {
    return this.source.getAsBoolean();
  }
}
