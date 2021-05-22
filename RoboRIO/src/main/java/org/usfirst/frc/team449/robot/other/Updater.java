package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

import java.util.ArrayList;
import java.util.List;

/** A Runnable for updating cached variables. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Updater implements Runnable {

  /** Default instance that is run whenever any instance is run. */
  private static final Updater defaultInstance = new Updater(new ArrayList<>());
  /** The objects to update. */
  @NotNull private final List<Updatable> updatables;

  /**
   * Default constructor
   *
   * @param updatables The objects to update.
   */
  public Updater(@NotNull @JsonProperty(required = true) final List<Updatable> updatables) {
    this.updatables = updatables;
  }

  /** Subscribes the specified updatable to being updated. */
  public static void subscribe(final Updatable updatable) {
    defaultInstance.updatables.add(updatable);
  }

  /**
   * Constructs an updatable that also calls {@link Updater#run()} on the default updatable instance
   * whenever it is run.
   *
   * @param updatables The objects to update.
   */
  @JsonCreator
  public static Updater subscribe(
      @NotNull @JsonProperty(required = true) final List<Updatable> updatables) {
    defaultInstance.updatables.addAll(updatables);
    return defaultInstance;
  }

  /** Update all the updatables. */
  @Override
  public void run() {
    for (final Updatable updatable : this.updatables) {
      updatable.update();
    }
  }
}
