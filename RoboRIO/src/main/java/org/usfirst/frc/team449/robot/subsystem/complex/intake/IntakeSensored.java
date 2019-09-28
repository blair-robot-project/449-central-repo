package org.usfirst.frc.team449.robot.subsystem.complex.intake;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DigitalInput;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.subsystem.interfaces.conditional.SubsystemConditional;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.IntakeSimple;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;

/**
 * An intake with a digital input.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class IntakeSensored extends IntakeSimple implements SubsystemIntake, SubsystemConditional {

    /**
     * The sensor for detecting if there's something in the intake.
     */
    private final DigitalInput sensor;

    /**
     * The state of the condition when {@link IntakeSensored#update()} was called.
     */
    private boolean cachedCondition;

    /**
     * Default constructor.
     *
     * @param sensor    The sensor for detecting if there's something in the intake.
     * @param motor     The motor for the intake.
     * @param fastSpeed The speed to run the motor at going fast.
     * @param slowSpeed The speed to run the motor at going slow.
     */
    @JsonCreator
    public IntakeSensored(@NotNull @JsonProperty(required = true) MappedDigitalInput sensor,
                          @NotNull @JsonProperty(required = true) SimpleMotor motor,
                          @JsonProperty(required = true) double fastSpeed,
                          @JsonProperty(required = true) double slowSpeed) {
        super(motor, slowSpeed, fastSpeed, -slowSpeed, -fastSpeed);
        this.sensor = sensor;
    }

    /**
     * No default command.
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing
    }

    /**
     * @return true if the condition is met, false otherwise
     */
    @Override
    public boolean isConditionTrue() {
        return sensor.get();
    }

    /**
     * @return true if the condition was met when cached, false otherwise
     */
    @Override
    public boolean isConditionTrueCached() {
        return cachedCondition;
    }

    /**
     * Updates all cached values with current ones.
     */
    @Override
    public void update() {
        cachedCondition = isConditionTrue();
    }
}
