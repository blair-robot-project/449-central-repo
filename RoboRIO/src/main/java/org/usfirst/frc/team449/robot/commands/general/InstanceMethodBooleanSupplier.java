package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

/** @param <T> */
public class InstanceMethodBooleanSupplier<T> implements BooleanSupplier {
  private final BooleanSupplier invoke;

  @JsonCreator
  public InstanceMethodBooleanSupplier(
      @JsonProperty(required = true) Subsystem source,
      @JsonProperty(required = true) String methodName)
      throws NoSuchMethodException {
    Method method = source.getClass().getMethod(methodName);
    this.invoke =
        () -> {
          try {
            return (boolean) method.invoke(source);
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
        };
  }

  @Override
  public boolean getAsBoolean() {
    return this.invoke.getAsBoolean();
  }
}
