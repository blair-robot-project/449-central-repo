package org.usfirst.frc.team449.robot.commands.multiSubsystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.commands.multiInterface.IntakeUntilConditonMet;
import org.usfirst.frc.team449.robot.subsystem.interfaces.conditional.SubsystemConditional;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.SubsystemPosition;
import org.usfirst.frc.team449.robot.subsystem.interfaces.position.commands.GoToPosition;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.commands.SetSolenoid;

/**
 * Lower a position subsystem and an intake subsystem, then intake until a condition is met, then raise both.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LowerIntakeRaise<T extends Subsystem & SubsystemIntake & SubsystemSolenoid,
        U extends Subsystem & SubsystemIntake & SubsystemConditional,
        V extends Subsystem & SubsystemPosition> extends CommandGroup {

    /**
     * Default constructor.
     *
     * @param actuatedIntake     The intake that raises and lowers with solenoids but doesn't move with the elevator.
     * @param carriage           The intake mounted to the elevator, capable of detecting if it has the object to
     *                           intake.
     * @param elevator           The elevator that raises and lowers the carriage.
     * @param intakeSolenoidPos  The position for the actuated intake to be at to intake game pieces.
     * @param elevatorIntakePos  The position for the elevator to be at to intake game pieces. Defaults to 0.
     * @param elevatorUpPos      The position for the elevator to be at to give the actuated intake enough clearance to
     *                           move around.
     * @param actuatedIntakeMode The mode for the actuated intake to be in to intake game pieces.
     * @param carriageIntakeMode The mode for the carriage intake to be in to intake game pieces.
     * @param carriageHoldMode   The mode for the carriage intake to be in to hold game pieces after intaking them.
     *                           Defaults to off.
     * @param raiseIntake        Whether to raise the intake after the condition is met. Defaults to false.
     */
    @JsonCreator
    public LowerIntakeRaise(@NotNull @JsonProperty(required = true) T actuatedIntake,
                            @NotNull @JsonProperty(required = true) U carriage,
                            @NotNull @JsonProperty(required = true) V elevator,
                            @NotNull @JsonProperty(required = true) DoubleSolenoid.Value intakeSolenoidPos,
                            double elevatorIntakePos,
                            @JsonProperty(required = true) double elevatorUpPos,
                            @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode actuatedIntakeMode,
                            @NotNull @JsonProperty(required = true) SubsystemIntake.IntakeMode carriageIntakeMode,
                            @Nullable SubsystemIntake.IntakeMode carriageHoldMode,
                            @Nullable Boolean raiseIntake) {
        addSequential(new SetSolenoid(actuatedIntake, intakeSolenoidPos));
        addSequential(new GoToPosition<>(elevator, elevatorIntakePos));
        addSequential(new SetIntakeMode<>(actuatedIntake, actuatedIntakeMode));
        addSequential(new SetIntakeMode<>(carriage, carriageIntakeMode));
        addSequential(new IntakeUntilConditonMet<>(carriage, carriageIntakeMode, carriageHoldMode));
        addSequential(new SetIntakeMode<>(actuatedIntake, SubsystemIntake.IntakeMode.OFF));
        addSequential(new GoToPosition<>(elevator, elevatorUpPos));
        //Retract the intake by setting the piston to the opposite
        if (raiseIntake != null && raiseIntake) {
            addSequential(new SetSolenoid(actuatedIntake,
                    intakeSolenoidPos == DoubleSolenoid.Value.kForward ? DoubleSolenoid.Value.kReverse :
                            DoubleSolenoid.Value.kForward));
        }
    }

}
