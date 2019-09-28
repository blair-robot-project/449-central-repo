package org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * A simple two-sided intake subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeTwoSidesSimple extends Subsystem implements SubsystemIntakeTwoSides {

    /**
     * The motors this subsystem controls.
     */
    @NotNull
    private final SimpleMotor leftMotor, rightMotor;

    /**
     * The velocities for the motor to go at for each of the modes, on [-1, 1]. Can be null to indicate that this intake
     * doesn't have/use that mode.
     */
    @Nullable
    private final Double inSlowVel;
    @Nullable
    private final Double inFastVel;
    @Nullable
    private final Double outSlowVel;
    @Nullable
    private final Double outFastVel;

    /**
     * The current mode.
     */
    @NotNull
    private SubsystemIntake.IntakeMode mode;

    /**
     * Default constructor
     *
     * @param leftMotor  The left motor that this subsystem controls.
     * @param rightMotor The left motor that this subsystem controls.
     * @param inSlowVel  The velocity for the motor to go at for the IN_SLOW {@link IntakeMode}, on [-1, 1]. Can be null
     *                   to indicate that this intake doesn't have/use IN_SLOW.
     * @param inFastVel  The velocity for the motor to go at for the IN_FAST {@link IntakeMode}, on [-1, 1]. Can be null
     *                   to indicate that this intake doesn't have/use IN_FAST.
     * @param outSlowVel The velocity for the motor to go at for the OUT_SLOW {@link IntakeMode}, on [-1, 1]. Can be
     *                   null to indicate that this intake doesn't have/use OUT_SLOW.
     * @param outFastVel The velocity for the motor to go at for the OUT_FAST {@link IntakeMode}, on [-1, 1]. Can be
     *                   null to indicate that this intake doesn't have/use OUT_FAST.
     */
    @JsonCreator
    public IntakeTwoSidesSimple(@JsonProperty(required = true) @NotNull SimpleMotor leftMotor,
                                @JsonProperty(required = true) @NotNull SimpleMotor rightMotor,
                                @Nullable Double inSlowVel,
                                @Nullable Double inFastVel,
                                @Nullable Double outSlowVel,
                                @Nullable Double outFastVel) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.inSlowVel = inSlowVel;
        this.inFastVel = inFastVel;
        this.outSlowVel = outSlowVel;
        this.outFastVel = outFastVel;
        this.mode = IntakeMode.OFF;
    }

    /**
     * Initialize the default command for a subsystem. By default subsystems have no default command, but if they do,
     * the default command is set with this method. It is called on all Subsystems by CommandBase in the users program
     * after all the Subsystems are created.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }

    /**
     * @return the current mode of the intake.
     */
    @NotNull
    @Override
    public SubsystemIntake.IntakeMode getMode() {
        return mode;
    }

    /**
     * @param mode The mode to switch the left side of the intake to.
     */
    @Override
    public void setLeftMode(@NotNull SubsystemIntake.IntakeMode mode) {
        setMode(mode, leftMotor);
    }

    /**
     * @param mode The mode to switch the right side of the intake to.
     */
    @Override
    public void setRightMode(@NotNull SubsystemIntake.IntakeMode mode) {
        setMode(mode, rightMotor);
    }

    /**
     * @param mode
     * @param motor
     */
    private void setMode(@NotNull IntakeMode mode, @NotNull SimpleMotor motor) {
        switch (mode) {
            case OFF:
                motor.setVelocity(0);
                motor.disable();
                this.mode = IntakeMode.OFF;
                break;
            case IN_FAST:
                if (inFastVel != null) {
                    motor.enable();
                    motor.setVelocity(inFastVel);
                    this.mode = IntakeMode.IN_FAST;
                }
                break;
            case IN_SLOW:
                if (inSlowVel != null) {
                    motor.enable();
                    motor.setVelocity(inSlowVel);
                    this.mode = IntakeMode.IN_SLOW;
                }
                break;
            case OUT_FAST:
                if (outFastVel != null) {
                    motor.enable();
                    motor.setVelocity(outFastVel);
                    this.mode = IntakeMode.OUT_FAST;
                }
                break;
            case OUT_SLOW:
                if (outSlowVel != null) {
                    motor.enable();
                    motor.setVelocity(outSlowVel);
                    this.mode = IntakeMode.OUT_SLOW;
                }
                break;
        }
    }
}
