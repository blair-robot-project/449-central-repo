package org.usfirst.frc.team449.robot.subsystem.intake.intake2020;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.intake.SubsystemIntake;

import java.util.List;

/**
 * A cluster of intakes that acts as a single intake. Use for complex intakes with multiple motors.
 *
 * <p>Replaces IntakeTwoSides and friends.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeCluster implements Subsystem, SubsystemIntake {
  @NotNull private final List<SubsystemIntake> intakes;
  @NotNull private IntakeMode mode = IntakeMode.OFF;

  @JsonCreator
  public IntakeCluster(@NotNull @JsonProperty(required = true) final SubsystemIntake[] intakes) {
    this.intakes = List.of(intakes);
  }

  /** @return the current mode of the first intake in this group. */
  @Override
  public @NotNull IntakeMode getMode() {
    return this.mode;
  }

  /** @param mode The mode to switch the intake to. */
  @Override
  public void setMode(@NotNull final IntakeMode mode) {
    this.mode = mode;
    for (final SubsystemIntake intake : this.intakes) {
      intake.setMode(mode);
    }
  }

  /**
   * Returns an immutable list
   *
   * @return an immutable list of the intakes in this cluster
   */
  @NotNull
  public List<SubsystemIntake> intakes() {
    return this.intakes;
  }
}
