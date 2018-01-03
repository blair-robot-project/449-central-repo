package org.usfirst.frc.team449.robot.subsystem.complex.intake;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedVictor;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/**
 * An intake with a piston that actuates it and a fixed and actuated motor.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeFixedAndActuated extends YamlSubsystem implements SubsystemSolenoid, SubsystemIntake {

    /**
     * Motor for the fixed intake
     */
    @NotNull
    private final SimpleMotor fixedMotor;

    /**
     * Motor for the actuated intake
     */
    @NotNull
    private final SimpleMotor actuatedMotor;

    /**
     * Piston for raising and lowering the intake
     */
    @NotNull
    private final DoubleSolenoid piston;

    /**
     * How fast the fixed victor should go to pick up balls, on [-1, 1]
     */
    private final double fixedIntakeSpeed;

    /**
     * How fast the fixed victor should go to agitate balls while they're being fed into the multiSubsystem, on [-1, 1]
     */
    private final double fixedAgitateSpeed;

    /**
     * How fast the actuated victor should go to pick up balls, on [-1, 1]
     */
    private final double actuatedSpeed;

    /**
     * The intake's position.
     */
    private DoubleSolenoid.Value pistonPos;

    /**
     * The mode the intake's currently in.
     */
    @NotNull
    private IntakeMode mode;

    /**
     * Default constructor.
     *
     * @param fixedMotor        The SimpleMotor powering the fixed intake.
     * @param fixedAgitateSpeed The speed to run the fixed victor at to agitate balls, on [-1, 1]
     * @param fixedIntakeSpeed  The speed to run the fixed victor to intake balls, on [-1, 1]
     * @param actuatedMotor     The SimpleMotor powering the actuated intake.
     * @param actuatedSpeed     The speed to run the actuated victor to intake balls, on [-1, 1].
     * @param piston            The piston for raising and lowering the actuated intake.
     */
    @JsonCreator
    public IntakeFixedAndActuated(@NotNull @JsonProperty(required = true) MappedVictor fixedMotor,
                                  @JsonProperty(required = true) double fixedAgitateSpeed,
                                  @JsonProperty(required = true) double fixedIntakeSpeed,
                                  @NotNull @JsonProperty(required = true) MappedVictor actuatedMotor,
                                  @JsonProperty(required = true) double actuatedSpeed,
                                  @NotNull @JsonProperty(required = true) MappedDoubleSolenoid piston) {
        //Instantiate stuff.
        this.fixedMotor = fixedMotor;
        this.fixedIntakeSpeed = fixedIntakeSpeed;
        this.fixedAgitateSpeed = fixedAgitateSpeed;
        this.actuatedMotor = actuatedMotor;
        this.actuatedSpeed = actuatedSpeed;
        this.piston = piston;
        mode = IntakeMode.OFF;
    }

    /**
     * @param value The position to set the solenoid to.
     */
    @Override
    public void setSolenoid(@NotNull DoubleSolenoid.Value value) {
        piston.set(value);
        pistonPos = value;
    }

    /**
     * @return the current position of the solenoid.
     */
    @NotNull
    public DoubleSolenoid.Value getSolenoidPosition() {
        return pistonPos;
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
    public IntakeMode getMode() {
        return mode;
    }

    /**
     * @param mode The mode to switch the intake to.
     */
    @Override
    public void setMode(@NotNull IntakeMode mode) {
        this.mode = mode;
        switch (mode) {
            case OFF:
                actuatedMotor.disable();
                fixedMotor.disable();
                break;
            case IN_FAST:
                //In fast is used for picking up balls.
                actuatedMotor.enable();
                fixedMotor.enable();
                fixedMotor.setVelocity(fixedIntakeSpeed);
                actuatedMotor.setVelocity(actuatedSpeed);
                break;
            case IN_SLOW:
                //In slow is used for agitation.
                actuatedMotor.disable();
                fixedMotor.enable();
                fixedMotor.setVelocity(fixedAgitateSpeed);
                break;
            default:
                Logger.addEvent("Unsupported mode!", this.getClass());
        }
    }
}
