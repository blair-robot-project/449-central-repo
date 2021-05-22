package org.usfirst.frc.team449.robot.intake.feeder2020.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.CommandBase;
import io.github.oblarg.oblog.Loggable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.ConditionTimingComponentDecorator;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.subsystem.flywheel.FlywheelWithTimeout;
import org.usfirst.frc.team449.robot.subsystem.intake.SubsystemIntake;

import java.util.function.BooleanSupplier;

/**
 * Runs feeder when shooting and feeder (along with index wheel) when indexing sensor tripped.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DefaultFeederCommand extends CommandBase implements Loggable {
    @NotNull
    private final SubsystemIntake.IntakeMode transitionWheelIndexingMode;
    @NotNull
    private final SubsystemIntake.IntakeMode transitionWheelShootingMode;
    @NotNull
    private final SubsystemIntake.IntakeMode feederIndexingMode;
    @NotNull
    private final SubsystemIntake.IntakeMode feederShootingMode;
    @NotNull
    private final SubsystemIntake.IntakeMode feederCoughingMode;

    @Nullable
    private final Double indexingTimeout;

    @NotNull
    private final SubsystemIntake feeder;
    @NotNull
    private final SubsystemIntake bumper;
    @NotNull
    private final SubsystemIntake transitionWheel;
    @NotNull
    private final FlywheelWithTimeout shooter;

    @NotNull
    private final ConditionTimingComponentDecorator sensor1, sensor2, indexing, shooting, flywheelOn;

    /**
     * Whether or not the feeder has picked up a ball yet.
     * Used in 2021 code, b/c this was the indexer we were using, not the counter
     */
    private boolean gotBall;

    /**
     * Default constructor
     *
     * @param transitionWheelIndexingMode the {@link SubsystemIntake.IntakeMode} to run the transition
     *                                    wheel at when indexing
     * @param transitionWheelShootingMode the {@link SubsystemIntake.IntakeMode} to run the transition
     *                                    wheel at when shooting
     * @param feederIndexingMode          the {@link SubsystemIntake.IntakeMode} to run the feeder at when
     *                                    indexing
     * @param feederCoughingMode          the {@link SubsystemIntake.IntakeMode} that the feeder will be in
     *                                    when it is coughing. Used so the command will cease action while coughing takes place
     * @param feederShootingMode          the {@link SubsystemIntake.IntakeMode} to run the feeder at when
     *                                    indexing
     * @param sensor                      the first sensor of the transition from intake to feeder
     * @param sensor2                     the second sensor of the transition
     * @param transitionWheel             the transition wheel of the intake
     * @param feeder                      the feeder
     * @param shooter                     the shooter
     * @param indexingTimeout             maximum duration for which to keep running the feeder if the sensors
     *                                    remain continuously activated
     */
    @JsonCreator
    public DefaultFeederCommand(
            @NotNull @JsonProperty(required = true) final SubsystemIntake.IntakeMode transitionWheelIndexingMode,
            @NotNull @JsonProperty(required = true) final SubsystemIntake.IntakeMode transitionWheelShootingMode,
            @NotNull @JsonProperty(required = true) final SubsystemIntake.IntakeMode feederIndexingMode,
            @NotNull @JsonProperty(required = true) final SubsystemIntake.IntakeMode feederCoughingMode,
            @NotNull @JsonProperty(required = true) final SubsystemIntake.IntakeMode feederShootingMode,
            @NotNull @JsonProperty(required = true) final BooleanSupplier sensor,
            @Nullable final BooleanSupplier sensor2,
            @NotNull @JsonProperty(required = true) final SubsystemIntake bumper,
            @NotNull @JsonProperty(required = true) final SubsystemIntake transitionWheel,
            @NotNull @JsonProperty(required = true) final SubsystemIntake feeder,
            @NotNull @JsonProperty(required = true) final FlywheelWithTimeout shooter,
            @Nullable final Double indexingTimeout) {
        this.transitionWheelIndexingMode = transitionWheelIndexingMode;
        this.transitionWheelShootingMode = transitionWheelShootingMode;
        this.feederIndexingMode = feederIndexingMode;
        this.feederCoughingMode = feederCoughingMode;
        this.feederShootingMode = feederShootingMode;

        this.bumper = bumper;
        this.transitionWheel = transitionWheel;
        this.feeder = feeder;
        this.shooter = shooter;

        this.indexingTimeout = indexingTimeout;

        this.flywheelOn = new ConditionTimingComponentDecorator(shooter::isFlywheelOn, false);
        this.shooting = new ConditionTimingComponentDecorator(shooter::isConditionTrueCached, false);

        this.sensor1 = new ConditionTimingComponentDecorator(sensor, false);
        if(sensor2 != null){
            this.sensor2 = new ConditionTimingComponentDecorator(sensor2, false);
        } else{
            this.sensor2 = null;
        }

        this.indexing = new ConditionTimingComponentDecorator(() -> {
            // Give up if it's been long enough after either sensor last activated and there's still something
            // activating one of them. This specifically will continue giving up even if one of the sensors
            // deactivates but the other still surpasses the timeout.
            if (this.indexingTimeout != null && (this.sensor1.timeSinceLastBecameTrue() > this.indexingTimeout ||
                    (this.sensor2 != null && this.sensor2.timeSinceLastBecameTrue() > this.indexingTimeout))) {
                return false;
            }

            // Run when either sensor is being actively tripped.
            if(sensor2 != null){
                return this.sensor1.isTrue() || this.sensor2.isTrue();
            }
            return this.sensor1.isTrue();
        }, false);

        this.gotBall = false;
    }

    @Override
    public void execute() {
        final double currentTime = Clock.currentTimeSeconds();

        this.sensor1.update(currentTime);
        if(sensor2 != null){
            this.sensor2.update(currentTime);
        }
        this.indexing.update(currentTime);
        this.shooting.update(currentTime);
        this.flywheelOn.update(currentTime);

        if (this.feeder.getMode() == feederCoughingMode) {
            return;
        }

        if (this.shooting.isTrue()) {
            this.transitionWheel.setMode(this.transitionWheelShootingMode);
            this.feeder.setMode(this.feederShootingMode);

            return;
        }

        if (this.shooting.justBecameFalse()) {
            this.feeder.setMode(SubsystemIntake.IntakeMode.OFF);
            this.transitionWheel.setMode(SubsystemIntake.IntakeMode.OFF);
        }

        if (this.flywheelOn.isTrue()) {
            this.feeder.setMode(SubsystemIntake.IntakeMode.OFF);

            // Also turn off the intake to prevent jamming if it's on and a ball gets in somehow.
            this.bumper.setMode(SubsystemIntake.IntakeMode.OFF);
            this.transitionWheel.setMode(SubsystemIntake.IntakeMode.OFF);

            // Indexing will time out if not being able to shoot is why the transition wheel was stopped,
            // so we must pretend that the sensor has just activated if it was already activated while shooting.
            this.sensor1.forceUpdate(currentTime, this.sensor1.isTrue());
            if(sensor2 != null){
                this.sensor2.forceUpdate(currentTime, this.sensor2.isTrue());
            }

            return;
        }

        if (this.indexing.justBecameFalse()) {
            this.feeder.setMode(SubsystemIntake.IntakeMode.OFF);

            return;
        }

        if (this.indexing.justBecameTrue()) {
            this.transitionWheel.setMode(this.transitionWheelIndexingMode);
            this.feeder.setMode(this.feederIndexingMode);
            this.gotBall = true;
        }
    }

    /**
     * 2021 code for the Galactic Search sub-challenge.
     * Checks if a ball has been picked up to determine which of 4 possible paths need to run
     * Then resets to prepare for the next checkpoint
     * @return true if a ball has been picked up since the most recent checkpoint,
     *          false otherwise
     */
    public boolean hasGotBall(){
        boolean out = this.gotBall;
        this.gotBall = false;
        return out;
    }
}
