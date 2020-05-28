package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

/** Wraps a {@link BooleanSupplier} and only updates its result when told to. */
public class BooleanSupplierUpdatable implements BooleanSupplier, Updatable {
  @NotNull private final BooleanSupplier source;
  private boolean cachedValue;

  /**
   * Default constructor
   *
   * @param source the {@link BooleanSupplier} from which to obtain values
   */
  @JsonCreator
  public BooleanSupplierUpdatable(
      @NotNull @JsonProperty(required = true) final BooleanSupplier source,
      @Nullable final Boolean initialValue) {
    this.source = source;
    this.cachedValue = Objects.requireNonNullElseGet(initialValue, source::getAsBoolean);
  }

  /**
   * Gets the cached value of the supplier.
   *
   * @return a result
   */
  @Override
  public boolean getAsBoolean() {
    return this.cachedValue;
  }

  /** Updates the cached value of the supplier. */
  @Override
  public void update() {
    this.cachedValue = this.source.getAsBoolean();
  }
}
