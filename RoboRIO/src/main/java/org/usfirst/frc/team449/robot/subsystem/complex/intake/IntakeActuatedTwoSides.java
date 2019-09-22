package org.usfirst.frc.team449.robot.subsystem.complex.intake;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.IntakeTwoSidesSimple;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.intakeTwoSides.SubsystemIntakeTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;

/**
 * An intake that goes up and down with a piston.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeActuatedTwoSides extends IntakeTwoSidesSimple implements SubsystemSolenoid, SubsystemIntakeTwoSides {

    /**
     * The piston for actuating the intake.
     */
    private final DoubleSolenoid piston;
    /**
     * The current position of the piston
     */
    private DoubleSolenoid.Value currentPistonPos;

    /**
     * Default constructor.
     *
     * @param piston     The piston for actuating the intake.
     * @param leftMotor  The left motor that this subsystem controls.
     * @param rightMotor The left motor that this subsystem controls.
     * @param fastSpeed  The speed to run the motor at going fast.
     * @param slowSpeed  The speed to run the motor at going slow.
     */
    @JsonCreator
    public IntakeActuatedTwoSides(@NotNull @JsonProperty(required = true) MappedDoubleSolenoid piston,
                                  @NotNull @JsonProperty(required = true) SimpleMotor leftMotor,
                                  @NotNull @JsonProperty(required = true) SimpleMotor rightMotor,
                                  @JsonProperty(required = true) double fastSpeed,
                                  @JsonProperty(required = true) double slowSpeed) {
        super(leftMotor, rightMotor, slowSpeed, fastSpeed, -slowSpeed, -fastSpeed);
        this.piston = piston;
    }

    /**
     * Initialize the default command for a subsystem By default subsystems have no default command, but if they do, the
     * default command is set with this method. It is called on all Subsystems by CommandBase in the users program after
     * all the Subsystems are created.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing
    }

    /**
     * @param value The position to set the solenoid to.
     */
    @Override
    public void setSolenoid(@NotNull DoubleSolenoid.Value value) {
        currentPistonPos = value;
        piston.set(value);
    }

    /**
     * @return the current position of the solenoid.
     */
    @Override
    @NotNull
    public DoubleSolenoid.Value getSolenoidPosition() {
        return currentPistonPos;
    }
}
