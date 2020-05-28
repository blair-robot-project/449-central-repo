package org.usfirst.frc.team449.robot.commands.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A Command that invokes a method on a given object by name using reflection. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class InvokeMethodByNameCommand extends InstantCommand {
  // A field to avoid repeated array instantiation.
  private static final Subsystem[] EMPTY_ARR = new Subsystem[0];

  /**
   * Creates a new InvokeMethodByNameCommand that invokes the method with the specified name on the
   * specified object with the specified requirements.
   *
   * @param object the object instance whose method to invoke
   * @param method the name of the method to invoke
   * @param requiredSubsystems the subsystems required by this command
   */
  @JsonCreator
  public InvokeMethodByNameCommand(
      @NotNull @JsonProperty(required = true) final Subsystem object,
      @NotNull @JsonProperty(required = true) final String method,
      @Nullable final Subsystem[] requiredSubsystems) {
    super(
        getRunnableFromMethodUsingReflection(object, method),
        requiredSubsystems != null ? requiredSubsystems : EMPTY_ARR);
  }

  /**
   * Uses reflection to create a reference in the form of a {@link Runnable} to the specified
   * object's parameterless instance method with the specified name.
   *
   * @param object The receiver of the method. Static methods are not supported.
   * @param methodName the name of the method
   * @return a Runnable that when run invokes the method of the specified object with the specified
   *     name
   */
  @NotNull
  private static Runnable getRunnableFromMethodUsingReflection(
      @NotNull final Object object, @NotNull final String methodName) {
    final Method method;

    try {
      method = object.getClass().getMethod(methodName);
    } catch (final NoSuchMethodException ex) {
      throw new RuntimeException(ex);
    }

    return () -> {
      try {
        method.invoke(object);
      } catch (final IllegalAccessException | InvocationTargetException ex) {
        throw new RuntimeException(ex);
      }
    };
  }
}
