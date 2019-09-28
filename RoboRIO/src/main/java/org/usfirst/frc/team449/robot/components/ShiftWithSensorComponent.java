package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.Notifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDigitalInput;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.other.BufferTimer;

import java.util.List;

/**
 * A component that a subsystem can use for shifting when the pistons have sensor to detect position.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftWithSensorComponent extends ShiftComponent {

    /**
     * The reed switches that detect if the shifter pistons are in high gear.
     */
    @NotNull
    private final List<MappedDigitalInput> highGearSensors;

    /**
     * The reed switches that detect if the shifter pistons are in low gear.
     */
    @NotNull
    private final List<MappedDigitalInput> lowGearSensors;

    /**
     * The motors that should be disabled while the piston is shifting.
     */
    @NotNull
    private final List<SimpleMotor> motorsToDisable;

    /**
     * The timer for how long the piston can be considered shifting before we ignore the sensors and re-enable the
     * motors.
     */
    @NotNull
    private final BufferTimer motorDisableTimer;

    /**
     * The Notifier that runs checkToReenable periodically.
     */
    @NotNull
    private final Notifier sensorChecker;

    /**
     * The period for the loop that checks the sensors and enables/disables the motors, in seconds.
     */
    private final double sensorCheckerPeriodSecs;

    /**
     * Whether the piston's position was correct the last time checkToReenable was run.
     */
    private boolean pistonWasCorrect;

    /**
     * Whether the piston's position is currently correct. Field to avoid garbage collection.
     */
    private boolean pistonCorrect;

    /**
     * Default constructor.
     *
     * @param otherShiftables         All objects that should be shifted when this component's piston is.
     * @param piston                  The piston that shifts.
     * @param startingGear            The gear to start in. Can be null, and if it is, the starting gear is gotten from
     *                                the piston's position.
     * @param highGearSensors         The reed switches that detect if the shifter pistons are in high gear.
     * @param lowGearSensors          The reed switches that detect if the shifter pistons are in low gear.
     * @param motorsToDisable         The motors that should be disabled while the piston is shifting.
     * @param motorDisableTimer       The timer for how long the piston can be considered shifting before we ignore the
     *                                sensors and re-enable the motors.
     * @param sensorCheckerPeriodSecs The period for the loop that checks the sensors and enables/disables the motors,
     *                                in seconds.
     */
    @JsonCreator
    public ShiftWithSensorComponent(@NotNull @JsonProperty(required = true) List<Shiftable> otherShiftables,
                                    @NotNull @JsonProperty(required = true) MappedDoubleSolenoid piston,
                                    @Nullable Shiftable.gear startingGear,
                                    @NotNull @JsonProperty(required = true) List<MappedDigitalInput> highGearSensors,
                                    @NotNull @JsonProperty(required = true) List<MappedDigitalInput> lowGearSensors,
                                    @NotNull @JsonProperty(required = true) List<SimpleMotor> motorsToDisable,
                                    @NotNull @JsonProperty(required = true) BufferTimer motorDisableTimer,
                                    @JsonProperty(required = true) double sensorCheckerPeriodSecs) {
        super(otherShiftables, piston, startingGear);
        this.highGearSensors = highGearSensors;
        this.lowGearSensors = lowGearSensors;
        this.motorsToDisable = motorsToDisable;
        this.motorDisableTimer = motorDisableTimer;
        this.sensorChecker = new Notifier(this::checkToReenable);
        this.sensorChecker.startPeriodic(sensorCheckerPeriodSecs);
        this.sensorCheckerPeriodSecs = sensorCheckerPeriodSecs;
    }

    /**
     * Check the sensors and enable/disable the motors accordingly.
     */
    private void checkToReenable() {
        //Check if the piston is in correct position by making sure each sensor is reading correctly.
        pistonCorrect = true;
        if (currentGear == Shiftable.gear.HIGH.getNumVal()) {
            for (MappedDigitalInput sensor : highGearSensors) {
                //The position is correct if all the sensors read true.
                pistonCorrect = pistonCorrect && sensor.get();
            }
        } else {
            for (MappedDigitalInput sensor : lowGearSensors) {
                //The position is correct if all the sensors read true.
                pistonCorrect = pistonCorrect && sensor.get();
            }
        }

        //If the pistons haven't been correct for more than a certain amount of time, we assume something went wrong and
        // keep the motors enabled (essentially ignoring the sensors) so the robot can still drive.
        if (motorDisableTimer.get(!pistonCorrect)) {
            //We set pistonCorrect here because we're basically just overriding what the sensors say.
            pistonCorrect = true;
            for (SimpleMotor motor : motorsToDisable) {
                motor.enable();
            }
            sensorChecker.stop();
        }
        //Otherwise, if the piston is wrong, disable all the motors. We do this constantly in case some other part of
        //the code tries to re-enable them.
        //TODO set up a lock system so no other part of the code can re-enable the motors.
        else if (!pistonCorrect) {
            for (SimpleMotor motor : motorsToDisable) {
                motor.disable();
            }
        }
        //If the piston is correct, but wasn't the last time we checked, that means the piston has finished shifting and
        //we should re-enable the motors.
        else if (!pistonWasCorrect) {
            for (SimpleMotor motor : motorsToDisable) {
                motor.enable();
            }
            sensorChecker.stop();
        }

        //Record the piston position so we can have rising/falling edge detection
        pistonWasCorrect = pistonCorrect;
    }

    /**
     * Shifts to a given gear.
     *
     * @param gear The gear to shift to.
     */
    @Override
    public void shiftToGear(int gear) {
        super.shiftToGear(gear);
        sensorChecker.startPeriodic(sensorCheckerPeriodSecs);
    }
}
