package org.usfirst.frc.team449.robot.other;

import edu.wpi.first.hal.HALValue;
import edu.wpi.first.hal.SimValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/** Helpers for using the {@link edu.wpi.first.hal.SimDevice} framework. */
public class SimUtil {
  /**
   * Gets the value for a property based on either a simulation variable or the real implementation.
   *
   * @param useSimValue whether to use the value provided by {@code simValue} instead of that
   *     provided by {@code regularImplementation}
   * @param setSimValue whether to set the value of {@code simValue} to the returned value when
   *     using {@code regularImplementation}
   * @param simValue the simulation variable that appears as an input in the WPILib simulation GUI
   * @param regularImplementation a reference to the regular getter of the property
   * @param <T> The type of the property. Used to cast the value obtained from {@code simValue}.
   * @return a value for the property obtained from either a simulation input or the return value of
   *     the actual getter
   */
  @Contract("true, _, null, _ -> fail")
  public static <T> T getWithSimHelper(
      final boolean useSimValue,
      final boolean setSimValue,
      @Nullable final SimValue simValue,
      @NotNull final Supplier<T> regularImplementation) {
    final T result;

    if (useSimValue) {
      Objects.requireNonNull(simValue);
      //noinspection unchecked
      result = (T) unwrapHALValue(simValue.getValue());
    } else {
      result = regularImplementation.get();
      if (simValue != null && setSimValue) simValue.setValue(makeHALValue(result));
    }

    return result;
  }

  /**
   * Gets the underlying value of the specified {@link HALValue} instance.
   *
   * @param value the object to retrieve the value from
   * @return Depending on {@link HALValue#getType()}:
   *     <ul>
   *       <li>{@link HALValue#kUnassigned}: {@code null}
   *       <li>{@link HALValue#kBoolean}: {@link HALValue#getBoolean()}
   *       <li>{@link HALValue#kDouble}: {@link HALValue#getDouble()}
   *       <li>{@link HALValue#kInt}, {@link HALValue#kEnum}: {@code (int)}{@link
   *           HALValue#getLong()}
   *       <li>{@link HALValue#kLong}: {@link HALValue#getLong()}
   *     </ul>
   */
  @Nullable
  private static Object unwrapHALValue(@NotNull final HALValue value) {
    switch (value.getType()) {
      case HALValue.kUnassigned:
        return null;
      case HALValue.kBoolean:
        return value.getBoolean();
      case HALValue.kDouble:
        return value.getDouble();
      case HALValue.kEnum:
      case HALValue.kInt:
        return (int) value.getLong();
      case HALValue.kLong:
        return value.getLong();
    }
    throw new IllegalStateException("value.getType() is invalid.");
  }

  /**
   * Creates an instance of {@link HALValue} from the specified value.
   *
   * @param value the object to retrieve the value from
   * @return Depending on {@code value}:
   *     <ul>
   *       <li>{@code null}: {@link HALValue#makeUnassigned()}
   *       <li>{@link Boolean}: {@link HALValue#makeBoolean(boolean)}
   *       <li>{@link Double}: {@link HALValue#makeDouble(double)}
   *       <li>{@link Integer}: {@link HALValue#makeInt(int)}
   *       <li>{@link Enum}: {@link HALValue#makeInt(int)} with {@link Enum#ordinal()}
   *       <li>{@link Long}: {@link HALValue#makeLong(long)}
   *     </ul>
   */
  @NotNull
  private static HALValue makeHALValue(@Nullable final Object value) {
    if (value == null) return HALValue.makeUnassigned();
    if (Boolean.class.equals(value.getClass())) return HALValue.makeBoolean((Boolean) value);
    if (Double.class.equals(value.getClass())) return HALValue.makeDouble((Double) value);
    if (Integer.class.equals(value.getClass())) return HALValue.makeInt((Integer) value);
    if (Enum.class.isAssignableFrom(value.getClass())) return HALValue.makeEnum(((Enum<?>) value).ordinal());
    if (Long.class.equals(value.getClass())) return HALValue.makeLong((Long) value);
    throw new IllegalArgumentException("value must be Boolean, Double, Integer, Enum, or Long.");
  }
}
