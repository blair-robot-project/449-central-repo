package org.usfirst.frc.team449.robot.other;

import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;

/**
 * Associates object instances ID based on order of registration.
 *
 * @implNote ID begins at {@code 1} and increments by {@code 1}.
 */
public final class RegistrationOrderIDUtil {
  @NotNull private static final IdentityHashMap<Object, Integer> IDs = new IdentityHashMap<>();
  private static int CURRENT_ID = 0;
  private RegistrationOrderIDUtil() {
    throw new AssertionError("Utility class.");
  }

  /**
   * Associates an ID with the specified object. Does nothing if the specified object has already
   * been registered.
   *
   * @param instance the object instance to register
   * @return the ID associated with the object
   */
  @SuppressWarnings("UnusedReturnValue")
  public static int registerInstance(final Object instance) {
    final Integer oldID;
    if ((oldID = IDs.get(instance)) != null) return oldID;

    IDs.put(instance, ++CURRENT_ID);
    return CURRENT_ID;
  }

  /**
   * Gets the ID associated with the specified object.
   *
   * @param instance the object instance to retrieve the ID of
   * @return the ID associated with the object
   * @throws IllegalStateException if the specified object has never been registered
   */
  public static int getExistingID(final Object instance) throws IllegalStateException {
    final Integer result = IDs.get(instance);
    if (result == null) {
      throw new IllegalStateException("Specified object not registered.");
    }
    return result;
  }
}
