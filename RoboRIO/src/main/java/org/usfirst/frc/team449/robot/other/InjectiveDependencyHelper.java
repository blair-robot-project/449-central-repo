package org.usfirst.frc.team449.robot.other;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * Ensures that <em>depended</em> object instances are not each depended on by more than one
 * <em>dependent</em> object within a set of criteria specified on a per-dependency basis.
 * Dependencies are registered by either dependent or depended instances.
 *
 * <p>In other words, asserts that the function mapping instances of depended objects to instances
 * of dependent objects is injective for each intersection of the set of all dependent objects and
 * the sets for which each dependency-specified predicate returns {@code true}.
 */
public final class InjectiveDependencyHelper {
  private static final Multimap<Object, Object> mappings =
      Multimaps.newMultimap(new HashMap<>(), LinkedList::new);

  private InjectiveDependencyHelper() {}

  public static void assertInjective(
      @NotNull final Object dependent, @NotNull final Object depended)
      throws IllegalStateException {
    assertInjective(dependent, depended, dependent.getClass());
  }

  public static void assertInjective(
      @NotNull final Object dependent,
      @NotNull final Object depended,
      @NotNull final Class<?> instanceOf)
      throws IllegalStateException {
    assertInjective(dependent, depended, instanceOf::isInstance);
  }

  public static void assertInjective(
      @NotNull final Object dependent,
      @NotNull final Object depended,
      @NotNull final Predicate<Object> dependentClass)
      throws IllegalStateException {
    if (dependentIsDuplicate(dependent, depended, dependentClass)) {
      throw new IllegalStateException(
          "Non-injective dependency of "
              + dependent
              + " on "
              + depended
              + " (multiple instances of former depend on one instance of latter)");
    }
  }

  private static boolean dependentIsDuplicate(
      @NotNull final Object dependent,
      @NotNull final Object depended,
      @NotNull final Predicate<Object> dependentClass) {
    final Collection<Object> dependents = mappings.get(depended);
    final boolean dependentIsDuplicate = dependents.stream().anyMatch(dependentClass);
    dependents.add(dependent);
    return dependentIsDuplicate;
  }
}
