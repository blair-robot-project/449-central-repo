package org.usfirst.frc.team449.robot.other;

import org.jetbrains.annotations.NotNull;

/** Stuff that doesn't fit anywhere else */
public class Util {
  private Util() {}

  /**
   * Gets the prefix for the specified object instance when logging.
   *
   * @param o the object to retrieve a prefix for
   * @return the prefix to be prepended to the message when the specified object logs
   * @implNote the prefix is the simple name of the type of the object within brackets
   */
  public static String getLogPrefix(@NotNull final Object o) {
    return getLogPrefix(o.getClass());
  }

  /**
   * Gets the prefix for the specified type when logging.
   *
   * @param clazz the type to retrieve a prefix for
   * @return the prefix to be prepended to the message when the specified type logs
   * @implNote the prefix is the simple name of the type within brackets
   */
  public static String getLogPrefix(final Class<?> clazz) {
    return "[" + clazz.getSimpleName() + "] ";
  }

  /**
   * Clamps the specified value to be within the specified bounds by returning the nearer bound if
   * the value is out of range.
   *
   * @param value the value to clamp
   * @param lBound the lower bound to clamp to
   * @param uBound the upper bound to clamp to
   * @return {@code min(max(value, lBound), uBound)}
   */
  public static double clamp(final double value, final double lBound, final double uBound) {
    if (uBound < lBound) throw new IllegalArgumentException("uBound < lBound");
    if (value > uBound) return uBound;
    if (value < lBound) return lBound;
    return value;
  }

  /**
   * Clamps the absolute value of the specified value to the interval {@code [-absBound, absBound]}
   *
   * @param value the value to clamp
   * @param absBound the absolute bound to clamp to
   * @return {@code clamp(value, -absBound, absBound)}
   */
  public static double clamp(final double value, final double absBound) {
    return clamp(value, -absBound, absBound);
  }

  /**
   * Clamps the absolute value of the specified value to the interval {@code [-1, 1]}.
   *
   * @param value the value to clamp
   * @return {@code clamp(value, 1)}
   */
  public static double clamp(final double value) {
    return clamp(value, 1);
  }
}
