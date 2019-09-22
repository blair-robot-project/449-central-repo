package org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides;

import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * A subsystem for an intake with left and right motors controlled separately.
 */
public interface SubsystemIntakeTwoSides extends SubsystemIntake {

    /**
     * @param mode The mode to switch the left side of the intake to.
     */
    void setLeftMode(@NotNull IntakeMode mode);

    /**
     * @param mode The mode to switch the right side of the intake to.
     */
    void setRightMode(@NotNull IntakeMode mode);

    /**
     * @param mode The mode to switch the intake to.
     */
    @Override
    default void setMode(@NotNull IntakeMode mode) {
        setLeftMode(mode);
        setRightMode(mode);
    }
}
