package org.usfirst.frc.team449.robot.generalInterfaces.subsystems;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.oblarg.oblog.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A subsystem with a condition that's sometimes met, e.g. a limit switch, a current/power limit, an
 * IR sensor.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "@class")
public interface SubsystemConditional extends Updatable, Loggable {

  /**
   * Computes the current state of the condition.
   *
   * @return {@code true} if the condition is met, {@code false} otherwise
   */
  boolean isConditionTrue();

  /**
   * Gets the state of the condition when {@link SubsystemConditional#update()} was last called.
   *
   * @return {@code false} if the condition was met when cached, {@code false} otherwise
   *
   * @implNote See documentation for {@link SubsystemConditional#update()}
   * @implSpec Both this method and {@link SubsystemConditional#update()} must be overridden
   * together.
   */
  default boolean isConditionTrueCached() {
    return MixinImpl.isConditionTrueCached(this);
  }

  /**
   * Updates the cached value of the condition.
   *
   * @implNote The default implementation caches the return value of {@link
   * SubsystemConditional#isConditionTrue()} in a {@link ConcurrentHashMap} of instances of this
   * interface.
   * @implSpec Both this method and {@link SubsystemConditional#isConditionTrueCached()} must be
   * overridden together.
   */
  @Override
  default void update() {
    MixinImpl.update(this);
  }
}

@SuppressWarnings("ClassNameDiffersFromFileName")
final class MixinImpl {
  private static final ConcurrentMap<SubsystemConditional, Boolean> cachedConditions = new ConcurrentHashMap<>();

  private MixinImpl() { }

  static boolean isConditionTrueCached(final SubsystemConditional self) {
    final Boolean cached = cachedConditions.get(self);
    if (cached != null) return cached;

    final boolean updated = self.isConditionTrue();
    cachedConditions.put(self, updated);
    return updated;
  }

  static void update(final SubsystemConditional self) {
    cachedConditions.put(self, self.isConditionTrue());
  }
}
