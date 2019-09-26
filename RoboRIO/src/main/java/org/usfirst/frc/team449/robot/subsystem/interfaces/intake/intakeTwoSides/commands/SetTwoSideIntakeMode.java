package org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.SubsystemIntakeTwoSides;

/**
 * Set the intake modes of a two-sided intake to something different for each side.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetTwoSideIntakeMode<T extends Subsystem & SubsystemIntakeTwoSides> extends InstantCommand {
    /**
     * The subsystem to execute this command on.
     */
    @NotNull
    private final T subsystem;

    /**
     * The mode to set this subsystem to.
     */
    @NotNull
    private final SubsystemIntake.IntakeMode leftMode, rightMode;

    /**
     * Default constructor
     *
     * @param subsystem The subsystem to execute this command on.
     * @param leftMode  The mode for the left side of the intake to run at.
     * @param rightMode The mode for the right side of the intake to run at.
     */
    @JsonCreator
    public SetTwoSideIntakeMode(@NotNull @JsonProperty(required = true) T subsystem,
                                @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode leftMode,
                                @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode rightMode) {
        requires(subsystem);
        this.subsystem = subsystem;
        this.leftMode = leftMode;
        this.rightMode = rightMode;
    }

    /**
     * Log when this command is initialized
     */
    @Override
    protected void initialize() {
        Logger.addEvent("SetTwoSideIntakeMode init.", this.getClass());
    }

    /**
     * Set the intake to the given mode.
     */
    @Override
    protected void execute() {
        subsystem.setLeftMode(leftMode);
        subsystem.setRightMode(rightMode);
    }

    /**
     * Finish immediately because this is a state-change command.
     *
     * @return true
     */
    @Override
    protected boolean isFinished() {
        return true;
    }

    /**
     * Log when this command ends
     */
    @Override
    protected void end() {
        Logger.addEvent("SetTwoSideIntakeMode end.", this.getClass());
    }

    /**
     * Log when this command is interrupted.
     */
    @Override
    protected void interrupted() {
        Logger.addEvent("SetTwoSideIntakeMode Interrupted!", this.getClass());
    }
}
