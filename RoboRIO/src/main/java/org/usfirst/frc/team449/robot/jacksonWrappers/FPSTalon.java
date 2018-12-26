package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Notifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.feedForwardComponent.FeedForwardComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.other.MotionProfileData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component wrapper on the CTRE {@link TalonSRX}, with unit conversions to/from FPS built in. Every non-unit-conversion
 * in this class takes arguments in post-gearing FPS.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FPSTalon implements SimpleMotor, Shiftable, Loggable {

    /**
     * The CTRE CAN Talon SRX that this class is a wrapper on
     */
    @NotNull
    protected final TalonSRX canTalon;
    /**
     * The PDP this Talon is connected to.
     */
    @Nullable
    protected final PDP PDP;
    /**
     * The counts per rotation of the encoder being used, or null if there is no encoder.
     */
    @Nullable
    private final Integer encoderCPR;
    /**
     * The coefficient the output changes by after being measured by the encoder, e.g. this would be 1/70 if there was a
     * 70:1 gearing between the encoder and the final output.
     */
    private final double postEncoderGearing;
    /**
     * The number of feet travelled per rotation of the motor this is attached to, or null if there is no encoder.
     */
    private final double feetPerRotation;
    /**
     * The minimum number of points that must be in the bottom-level MP buffer before starting a profile.
     */
    private final int minNumPointsInBottomBuffer;
    /**
     * The motion profile motionProfileStatus of the Talon.
     */
    @NotNull
    private final MotionProfileStatus motionProfileStatus;
    /**
     * A notifier that moves points from the API-level buffer to the talon-level one.
     */
    private final Notifier bottomBufferLoader;
    /**
     * The period for bottomBufferLoader, in seconds.
     */
    private final double updaterProcessPeriodSecs;
    /**
     * A list of all the gears this robot has and their settings.
     */
    @NotNull
    private final Map<Integer, PerGearSettings> perGearSettings;
    /**
     * The talon's name, used for logging purposes.
     */
    @NotNull
    private final String name;
    /**
     * The component for doing linear regression to find the resistance.
     */
    @Nullable
    private final RunningLinRegComponent voltagePerCurrentLinReg;
    /**
     * Whether the forwards or reverse limit switches are normally open or closed, respectively.
     */
    private final boolean fwdLimitSwitchNormallyOpen, revLimitSwitchNormallyOpen;
    /**
     * A notifier that updates the motion magic feedforward based on the current setpoint.
     */
    private final Notifier motionMagicNotifier;

    /**
     * The period for the {@link Notifier} that updates the feedforward based on the current motion magic velocity setpoint.
     */
    private final double updateMMPeriodSecs;
    /**
     * The settings currently being used by this Talon.
     */
    @NotNull
    protected PerGearSettings currentGearSettings;
    /**
     * The most recently set setpoint.
     */
    private double setpoint;
    /**
     * RPS as used in a unit conversion method. Field to avoid garbage collection.
     */
    private Double RPS;

    /**
     * The setpoint in native units. Field to avoid garbage collection.
     */
    private double nativeSetpoint;

    /**
     * Default constructor.
     *
     * @param port                       CAN port of this Talon.
     * @param name                       The talon's name, used for logging purposes. Defaults to talon_portnum
     * @param reverseOutput              Whether to reverse the output.
     * @param enableBrakeMode            Whether to brake or coast when stopped.
     * @param voltagePerCurrentLinReg    The component for doing linear regression to find the resistance.
     * @param PDP                        The PDP this Talon is connected to.
     * @param fwdLimitSwitchNormallyOpen Whether the forward limit switch is normally open or closed. If this is null,
     *                                   the forward limit switch is disabled.
     * @param revLimitSwitchNormallyOpen Whether the reverse limit switch is normally open or closed. If this is null,
     *                                   the reverse limit switch is disabled.
     * @param remoteLimitSwitchID        The CAN port of the Talon the limit switch to use for this talon is plugged
     *                                   into, or null to not use a limit switch or use the limit switch plugged into
     *                                   this talon.
     * @param fwdSoftLimit               The forward software limit, in feet. If this is null, the forward software
     *                                   limit is disabled. Ignored if there's no encoder.
     * @param revSoftLimit               The reverse software limit, in feet. If this is null, the reverse software
     *                                   limit is disabled. Ignored if there's no encoder.
     * @param postEncoderGearing         The coefficient the output changes by after being measured by the encoder, e.g.
     *                                   this would be 1/70 if there was a 70:1 gearing between the encoder and the
     *                                   final output. Defaults to 1.
     * @param feetPerRotation            The number of feet travelled per rotation of the motor this is attached to.
     *                                   Defaults to 1.
     * @param currentLimit               The max amps this device can draw. If this is null, no current limit is used.
     * @param enableVoltageComp          Whether or not to use voltage compensation. Defaults to false.
     * @param voltageCompSamples         The number of 1-millisecond samples to use for voltage compensation. Defaults
     *                                   to 32.
     * @param feedbackDevice             The type of encoder used to measure the output velocity of this motor. Can be
     *                                   null if there is no encoder attached to this Talon.
     * @param encoderCPR                 The counts per rotation of the encoder on this Talon. Can be null if
     *                                   feedbackDevice is, but otherwise must have a value.
     * @param reverseSensor              Whether or not to reverse the reading from the encoder on this Talon. Ignored
     *                                   if feedbackDevice is null. Defaults to false.
     * @param perGearSettings            The settings for each gear this motor has. Can be null to use default values
     *                                   and gear # of zero. Gear numbers can't be repeated.
     * @param startingGear               The gear to start in. Can be null to use startingGearNum instead.
     * @param startingGearNum            The number of the gear to start in. Ignored if startingGear isn't null.
     *                                   Defaults to the lowest gear.
     * @param minNumPointsInBottomBuffer The minimum number of points that must be in the bottom-level MP buffer before
     *                                   starting a profile. Defaults to 20.
     * @param updaterProcessPeriodSecs   The period for the {@link Notifier} that moves points between the MP buffers, in
     *                                   seconds. Defaults to 0.005.
     * @param updateMMPeriodSecs         The period for the {@link Notifier} that updates the feedforward based on the current motion magic velocity setpoint. Defaults to 0.05.
     * @param statusFrameRatesMillis     The update rates, in millis, for each of the Talon status frames.
     * @param controlFrameRatesMillis    The update rate, in milliseconds, for each of the control frame.
     * @param slaveTalons                The other {@link TalonSRX}s that are slaved to this one.
     * @param slaveVictors               The {@link com.ctre.phoenix.motorcontrol.can.VictorSPX}s that are slaved to
     *                                   this Talon.
     */
    @JsonCreator
    public FPSTalon(@JsonProperty(required = true) int port,
                    @Nullable String name,
                    boolean reverseOutput,
                    @JsonProperty(required = true) boolean enableBrakeMode,
                    @Nullable RunningLinRegComponent voltagePerCurrentLinReg,
                    @Nullable PDP PDP,
                    @Nullable Boolean fwdLimitSwitchNormallyOpen,
                    @Nullable Boolean revLimitSwitchNormallyOpen,
                    @Nullable Integer remoteLimitSwitchID,
                    @Nullable Double fwdSoftLimit,
                    @Nullable Double revSoftLimit,
                    @Nullable Double postEncoderGearing,
                    @Nullable Double feetPerRotation,
                    @Nullable Integer currentLimit,
                    boolean enableVoltageComp,
                    @Nullable Integer voltageCompSamples,
                    @Nullable FeedbackDevice feedbackDevice,
                    @Nullable Integer encoderCPR,
                    boolean reverseSensor,
                    @Nullable List<PerGearSettings> perGearSettings,
                    @Nullable Shiftable.gear startingGear,
                    @Nullable Integer startingGearNum,
                    @Nullable Integer minNumPointsInBottomBuffer,
                    @Nullable Double updaterProcessPeriodSecs,
                    @Nullable Double updateMMPeriodSecs,
                    @Nullable Map<StatusFrameEnhanced, Integer> statusFrameRatesMillis,
                    @Nullable Map<ControlFrame, Integer> controlFrameRatesMillis,
                    @Nullable List<SlaveTalon> slaveTalons,
                    @Nullable List<SlaveVictor> slaveVictors) {
        //Instantiate the base CANTalon this is a wrapper on.
        canTalon = new TalonSRX(port);
        //Set the name to the given one or to talon_portnum
        this.name = name != null ? name : ("talon_" + port);
        //Set this to false because we only use reverseOutput for slaves.
        canTalon.setInverted(reverseOutput);
        //Set brake mode
        canTalon.setNeutralMode(enableBrakeMode ? NeutralMode.Brake : NeutralMode.Coast);
        //Reset the position
        resetPosition();

        this.PDP = PDP;
        this.voltagePerCurrentLinReg = voltagePerCurrentLinReg;

        //Set frame rates
        if (controlFrameRatesMillis != null) {
            for (ControlFrame controlFrame : controlFrameRatesMillis.keySet()) {
                canTalon.setControlFramePeriod(controlFrame, controlFrameRatesMillis.get(controlFrame));
            }
        }
        if (statusFrameRatesMillis != null) {
            for (StatusFrameEnhanced statusFrame : statusFrameRatesMillis.keySet()) {
                canTalon.setStatusFramePeriod(statusFrame, statusFrameRatesMillis.get(statusFrame), 0);
            }
        }

        //Set fields
        this.feetPerRotation = feetPerRotation != null ? feetPerRotation : 1;
        this.updaterProcessPeriodSecs = updaterProcessPeriodSecs != null ? updaterProcessPeriodSecs : 0.005;
        this.updateMMPeriodSecs = updateMMPeriodSecs != null ? updateMMPeriodSecs : 0.05;
        this.minNumPointsInBottomBuffer = minNumPointsInBottomBuffer != null ? minNumPointsInBottomBuffer : 20;

        //Initialize
        this.motionProfileStatus = new MotionProfileStatus();
        this.perGearSettings = new HashMap<>();

        //If given no gear settings, use the default values.
        if (perGearSettings == null || perGearSettings.size() == 0) {
            this.perGearSettings.put(0, new PerGearSettings());
            this.perGearSettings.get(0).getFeedForwardComponent().setTalon(this);
        }
        //Otherwise, map the settings to the gear they are.
        else {
            for (PerGearSettings settings : perGearSettings) {
                settings.getFeedForwardComponent().setTalon(this);
                this.perGearSettings.put(settings.getGear(), settings);
            }
        }

        int currentGear;
        //If the starting gear isn't given, assume we start in low gear.
        if (startingGear == null) {
            if (startingGearNum == null) {
                currentGear = Integer.MAX_VALUE;
                for (Integer gear : this.perGearSettings.keySet()) {
                    if (gear < currentGear) {
                        currentGear = gear;
                    }
                }
            } else {
                currentGear = startingGearNum;
            }
        } else {
            currentGear = startingGear.getNumVal();
        }
        currentGearSettings = this.perGearSettings.get(currentGear);

        //Only enable the limit switches if it was specified if they're normally open or closed.
        if (fwdLimitSwitchNormallyOpen != null) {
            if (remoteLimitSwitchID != null) {
                canTalon.configForwardLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX,
                        fwdLimitSwitchNormallyOpen ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed,
                        remoteLimitSwitchID, 0);
            } else {
                canTalon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                        fwdLimitSwitchNormallyOpen ? LimitSwitchNormal.NormallyOpen :
                                LimitSwitchNormal.NormallyClosed, 0);
            }
            this.fwdLimitSwitchNormallyOpen = fwdLimitSwitchNormallyOpen;
        } else {
            canTalon.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
            this.fwdLimitSwitchNormallyOpen = true;
        }
        if (revLimitSwitchNormallyOpen != null) {
            if (remoteLimitSwitchID != null) {
                canTalon.configReverseLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX,
                        revLimitSwitchNormallyOpen ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed,
                        remoteLimitSwitchID, 0);
            } else {
                canTalon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                        revLimitSwitchNormallyOpen ? LimitSwitchNormal.NormallyOpen :
                                LimitSwitchNormal.NormallyClosed, 0);
            }
            this.revLimitSwitchNormallyOpen = revLimitSwitchNormallyOpen;
        } else {
            canTalon.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
            this.revLimitSwitchNormallyOpen = true;
        }

        //Set up the feedback device if it exists.
        if (feedbackDevice != null) {
            //CTRE encoder use RPM instead of native units, and can be used as QuadEncoders, so we switch them to avoid
            //having to support RPM.
            if (feedbackDevice.equals(FeedbackDevice.CTRE_MagEncoder_Absolute) ||
                    feedbackDevice.equals(FeedbackDevice.CTRE_MagEncoder_Relative)) {
                canTalon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
            } else {
                canTalon.configSelectedFeedbackSensor(feedbackDevice, 0, 0);
            }
            this.encoderCPR = encoderCPR;
            canTalon.setSensorPhase(reverseSensor);

            //Only enable the software limits if they were given a value and there's an encoder.
            if (fwdSoftLimit != null) {
                canTalon.configForwardSoftLimitEnable(true, 0);
                canTalon.configForwardSoftLimitThreshold(feetToEncoder(fwdSoftLimit).intValue(), 0);
            } else {
                canTalon.configForwardSoftLimitEnable(false, 0);
            }
            if (revSoftLimit != null) {
                canTalon.configReverseSoftLimitEnable(true, 0);
                canTalon.configReverseSoftLimitThreshold(feetToEncoder(revSoftLimit).intValue(), 0);
            } else {
                canTalon.configReverseSoftLimitEnable(false, 0);
            }
        } else {
            this.encoderCPR = null;
//            canTalon.configSelectedFeedbackSensor(FeedbackDevice.None, 0, 0); Uncomment this if FeedbackDevice.None
// is re-added in a future release.
        }

        //postEncoderGearing defaults to 1
        this.postEncoderGearing = postEncoderGearing != null ? postEncoderGearing : 1.;

        //Set up gear-based settings.
        setGear(currentGear);

        //Set the current limit if it was given
        if (currentLimit != null) {
            canTalon.configContinuousCurrentLimit(currentLimit, 0);
            canTalon.configPeakCurrentDuration(0, 0);
            canTalon.configPeakCurrentLimit(0, 0); // No duration
            canTalon.enableCurrentLimit(true);
        } else {
            //If we don't have a current limit, disable current limiting.
            canTalon.enableCurrentLimit(false);
        }

        //Enable or disable voltage comp
        canTalon.enableVoltageCompensation(enableVoltageComp);
        canTalon.configVoltageCompSaturation(12, 0);
        int notNullVoltageCompSamples = voltageCompSamples != null ? voltageCompSamples : 32;
        canTalon.configVoltageMeasurementFilter(notNullVoltageCompSamples, 0);

        //Set up MP notifier
        bottomBufferLoader = new Notifier(this::processMotionProfileBuffer);
        motionMagicNotifier = new Notifier(this::updateMotionMagicSetpoint);

        //Use slot 0
        canTalon.selectProfileSlot(0, 0);

        if (slaveTalons != null) {
            //Set up slaves.
            for (SlaveTalon slave : slaveTalons) {
                slave.setMaster(port, enableBrakeMode, currentLimit,
                        enableVoltageComp ? notNullVoltageCompSamples : null, PDP, voltagePerCurrentLinReg);
            }
        }

        if (slaveVictors != null) {
            //Set up slaves.
            for (SlaveVictor slave : slaveVictors) {
                slave.setMaster(canTalon, enableBrakeMode,
                        enableVoltageComp ? notNullVoltageCompSamples : null);
            }
        }
    }

    /**
     * Set the motor output voltage to a given percent of available voltage.
     *
     * @param percentVoltage percent of total voltage from [-1, 1]
     */
    public void setPercentVoltage(double percentVoltage) {
        //Warn the user if they're setting Vbus to a number that's outside the range of values.
        if (Math.abs(percentVoltage) > 1.0) {
            Logger.addEvent("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVoltage, this.getClass());
            percentVoltage = Math.signum(percentVoltage);
        }

        setpoint = percentVoltage;

        canTalon.set(ControlMode.PercentOutput, percentVoltage);
    }

    /**
     * @return The gear this subsystem is currently in.
     */
    @Override
    public int getGear() {
        return currentGearSettings.getGear();
    }

    /**
     * Shift to a specific gear.
     *
     * @param gear Which gear to shift to.
     */
    @Override
    public void setGear(int gear) {
        //Set the current gear
        currentGearSettings = perGearSettings.get(gear);

        //Set max voltage
        canTalon.configPeakOutputForward(currentGearSettings.getFwdPeakOutputVoltage() / 12., 0);
        canTalon.configPeakOutputReverse(currentGearSettings.getRevPeakOutputVoltage() / 12., 0);

        //Set min voltage
        canTalon.configNominalOutputForward(currentGearSettings.getFwdNominalOutputVoltage() / 12., 0);
        canTalon.configNominalOutputReverse(currentGearSettings.getRevNominalOutputVoltage() / 12., 0);

        if (currentGearSettings.getRampRate() != null) {
            //Set ramp rate, converting from volts/sec to seconds until 12 volts.
            canTalon.configClosedloopRamp(1 / (currentGearSettings.getRampRate() / 12.), 0);
            canTalon.configOpenloopRamp(1 / (currentGearSettings.getRampRate() / 12.), 0);
        } else {
            canTalon.configClosedloopRamp(0, 0);
            canTalon.configOpenloopRamp(0, 0);
        }

        //Set motion magic stuff
        if (currentGearSettings.motionMagicMaxVel != null) {
            canTalon.configMotionCruiseVelocity(FPSToEncoder(currentGearSettings.getMotionMagicMaxVel()).intValue(), 0);
            //We can convert accel the same way we do vel because both are per second.
            canTalon.configMotionAcceleration(FPSToEncoder(currentGearSettings.getMotionMagicMaxAccel()).intValue(), 0);
        }

        //Set PID stuff
        //Slot 0 velocity gains. We don't set F yet because that changes based on setpoint.
        canTalon.config_kP(0, currentGearSettings.getkP(), 0);
        canTalon.config_kI(0, currentGearSettings.getkI(), 0);
        canTalon.config_kD(0, currentGearSettings.getkD(), 0);

        //We set the MP gains when loading a profile so no need to do it here.
    }

    /**
     * Convert from native units read by an encoder to feet moved. Note this DOES account for post-encoder gearing.
     *
     * @param nativeUnits A distance native units as measured by the encoder.
     * @return That distance in feet, or null if no encoder CPR was given.
     */
    @Nullable
    protected Double encoderToFeet(double nativeUnits) {
        if (encoderCPR == null) {
            return null;
        }
        return nativeUnits / (encoderCPR * 4) * postEncoderGearing * feetPerRotation;
    }

    /**
     * Convert a distance from feet to encoder reading in native units. Note this DOES account for post-encoder
     * gearing.
     *
     * @param feet A distance in feet.
     * @return That distance in native units as measured by the encoder, or null if no encoder CPR was given.
     */
    @Nullable
    protected Double feetToEncoder(double feet) {
        if (encoderCPR == null) {
            return null;
        }
        return feet / feetPerRotation * (encoderCPR * 4) / postEncoderGearing;
    }

    /**
     * Converts the velocity read by the talon's getVelocity() method to the FPS of the output shaft. Note this DOES
     * account for post-encoder gearing.
     *
     * @param encoderReading The velocity read from the encoder with no conversions.
     * @return The velocity of the output shaft, in FPS, when the encoder has that reading, or null if no encoder CPR
     * was given.
     */
    @Nullable
    protected Double encoderToFPS(double encoderReading) {
        RPS = nativeToRPS(encoderReading);
        if (RPS == null) {
            return null;
        }
        return RPS * postEncoderGearing * feetPerRotation;
    }

    /**
     * Converts from the velocity of the output shaft to what the talon's getVelocity() method would read at that
     * velocity. Note this DOES account for post-encoder gearing.
     *
     * @param FPS The velocity of the output shaft, in FPS.
     * @return What the raw encoder reading would be at that velocity, or null if no encoder CPR was given.
     */
    @Nullable
    protected Double FPSToEncoder(double FPS) {
        return RPSToNative((FPS / postEncoderGearing) / feetPerRotation);
    }

    /**
     * Convert from CANTalon native velocity units to output rotations per second. Note this DOES NOT account for
     * post-encoder gearing.
     *
     * @param nat A velocity in CANTalon native units.
     * @return That velocity in RPS, or null if no encoder CPR was given.
     */
    @Contract(pure = true)
    @Nullable
    private Double nativeToRPS(double nat) {
        if (encoderCPR == null) {
            return null;
        }
        return (nat / (encoderCPR * 4)) * 10; //4 edges per count, and 10 100ms per second.
    }

    /**
     * Convert from output RPS to the CANTalon native velocity units. Note this DOES NOT account for post-encoder
     * gearing.
     *
     * @param RPS The RPS velocity you want to convert.
     * @return That velocity in CANTalon native units, or null if no encoder CPR was given.
     */
    @Contract(pure = true)
    @Nullable
    private Double RPSToNative(double RPS) {
        if (encoderCPR == null) {
            return null;
        }
        return (RPS / 10) * (encoderCPR * 4); //4 edges per count, and 10 100ms per second.
    }

    /**
     * Set a position setpoint for the Talon.
     *
     * @param feet An absolute position setpoint, in feet.
     */
    public void setPositionSetpoint(double feet) {
        setpoint = feet;
        nativeSetpoint = feetToEncoder(feet);
        if (currentGearSettings.getMotionMagicMaxVel() != null) {
            motionMagicNotifier.stop();
            //We don't know the setpoint for motion magic so we can't do fancy F stuff
            canTalon.config_kF(0, 0, 0);
            canTalon.set(ControlMode.MotionMagic, nativeSetpoint);
            motionMagicNotifier.startPeriodic(updateMMPeriodSecs);
        } else {
            if (nativeSetpoint == 0) {
                canTalon.config_kF(0, 0, 0);
            } else {
                canTalon.config_kF(0,
                        1023. / 12. / nativeSetpoint * currentGearSettings.getFeedForwardComponent().applyAsDouble(feet), 0);
            }
            canTalon.set(ControlMode.Position, nativeSetpoint);
        }
    }

    private void updateMotionMagicSetpoint() {
        if (!canTalon.getControlMode().equals(ControlMode.MotionMagic)) {
            motionMagicNotifier.stop();
        } else {
            nativeSetpoint = feetToEncoder(setpoint);
            //TODO check that this actually works
            canTalon.set(ControlMode.MotionMagic, nativeSetpoint, DemandType.ArbitraryFeedForward,
                    currentGearSettings.getFeedForwardComponent().calcMPVoltage(canTalon.getActiveTrajectoryPosition(), canTalon.getActiveTrajectoryVelocity(), 0) * 1023. / 12.);
        }
    }

    /**
     * Get the velocity of the CANTalon in FPS.
     *
     * @return The CANTalon's velocity in FPS, or null if no encoder CPR was given.
     */
    @Nullable
    public Double getVelocity() {
        return encoderToFPS(canTalon.getSelectedSensorVelocity(0));
    }

    /**
     * Set the velocity for the motor to go at.
     *
     * @param velocity the desired velocity, on [-1, 1].
     */
    @Override
    public void setVelocity(double velocity) {
        if (currentGearSettings.getMaxSpeed() != null) {
            setVelocityFPS(velocity * currentGearSettings.getMaxSpeed());
        } else {
            setPercentVoltage(velocity);
        }
    }

    /**
     * Give a velocity closed loop setpoint in FPS.
     *
     * @param velocity velocity setpoint in FPS.
     */
    protected void setVelocityFPS(double velocity) {
        nativeSetpoint = FPSToEncoder(velocity);
        setpoint = velocity;
        canTalon.config_kF(0, 0, 0);
        canTalon.set(ControlMode.Velocity, nativeSetpoint, DemandType.ArbitraryFeedForward,
                currentGearSettings.getFeedForwardComponent().applyAsDouble(velocity) * 1023. / 12.);
    }

    /**
     * Get the current closed-loop velocity error in FPS. WARNING: will give garbage if not in velocity mode.
     *
     * @return The closed-loop error in FPS, or null if no encoder CPR was given.
     */
    @Nullable
    public Double getError() {
        if (canTalon.getControlMode().equals(ControlMode.Velocity)) {
            return encoderToFPS(canTalon.getClosedLoopError(0));
        } else {
            return encoderToFeet(canTalon.getClosedLoopError(0));
        }
    }

    /**
     * Get the current velocity setpoint of the Talon in FPS. WARNING: will give garbage if not in velocity mode.
     *
     * @return The closed-loop velocity setpoint in FPS, or null if no encoder CPR was given.
     */
    @Nullable
    public Double getSetpoint() {
        return setpoint;
    }

    /**
     * Get the voltage the Talon is currently drawing from the PDP.
     *
     * @return Voltage in volts.
     */
    public double getOutputVoltage() {
        return canTalon.getMotorOutputVoltage();
    }

    /**
     * Get the voltage available for the Talon.
     *
     * @return Voltage in volts.
     */
    public double getBatteryVoltage() {
        return canTalon.getBusVoltage();
    }

    /**
     * Get the current the Talon is currently drawing from the PDP.
     *
     * @return Current in amps.
     */
    public double getOutputCurrent() {
        return canTalon.getOutputCurrent();
    }

    /**
     * Get the current control mode of the Talon. Please don't use this for anything other than logging.
     *
     * @return Control mode as a string.
     */
    public String getControlMode() {
        return String.valueOf(canTalon.getControlMode());
    }

    /**
     * Enables the motor, if applicable.
     */
    @Override
    public void enable() {
        //Not a thing anymore
    }

    /**
     * Disables the motor, if applicable.
     */
    @Override
    public void disable() {
        canTalon.set(ControlMode.Disabled, 0);
    }

    /**
     * Set the velocity scaled to a given gear's max velocity. Used mostly when autoshifting.
     *
     * @param velocity The velocity to go at, from [-1, 1], where 1 is the max speed of the given gear.
     * @param gear     The number of the gear to use the max speed from to scale the velocity.
     */
    public void setGearScaledVelocity(double velocity, int gear) {
        if (currentGearSettings.getMaxSpeed() == null) {
            setPercentVoltage(velocity);
        } else {
            setVelocityFPS(perGearSettings.get(gear).getMaxSpeed() * velocity);
        }
    }

    /**
     * Set the velocity scaled to a given gear's max velocity. Used mostly when autoshifting.
     *
     * @param velocity The velocity to go at, from [-1, 1], where 1 is the max speed of the given gear.
     * @param gear     The gear to use the max speed from to scale the velocity.
     */
    public void setGearScaledVelocity(double velocity, Shiftable.gear gear) {
        setGearScaledVelocity(velocity, gear.getNumVal());
    }

    /**
     * @return the position of the talon in feet, or null of inches per rotation wasn't given.
     */
    public Double getPositionFeet() {
        return encoderToFeet(canTalon.getSelectedSensorPosition(0));
    }

    /**
     * Resets the position of the Talon to 0.
     */
    public void resetPosition() {
        canTalon.setSelectedSensorPosition(0, 0, 0);
    }

    /**
     * Get the status of the forwards limit switch.
     *
     * @return True if the forwards limit switch is closed, false if it's open or doesn't exist.
     */
    public boolean getFwdLimitSwitch() {
        return fwdLimitSwitchNormallyOpen == canTalon.getSensorCollection().isFwdLimitSwitchClosed();
    }

    /**
     * Get the status of the reverse limit switch.
     *
     * @return True if the reverse limit switch is closed, false if it's open or doesn't exist.
     */
    public boolean getRevLimitSwitch() {
        return revLimitSwitchNormallyOpen == canTalon.getSensorCollection().isRevLimitSwitchClosed();
    }

    /**
     * Whether this talon is ready to start running a profile.
     *
     * @return True if minNumPointsInBottomBuffer points have been loaded or the top buffer is empty, false otherwise.
     */
    public boolean readyForMP() {
        canTalon.getMotionProfileStatus(motionProfileStatus);
        return motionProfileStatus.topBufferCnt == 0 || motionProfileStatus.btmBufferCnt >= minNumPointsInBottomBuffer;
    }

    /**
     * Whether this talon has finished running a profile.
     *
     * @return True if the active point in the talon is the last point, false otherwise.
     */
    public boolean MPIsFinished() {
        canTalon.getMotionProfileStatus(motionProfileStatus);
        return motionProfileStatus.isLast;
    }

    /**
     * Reset all MP-related stuff, including all points loaded in both the API and bottom-level buffers.
     */
    private void clearMP() {
        canTalon.clearMotionProfileHasUnderrun(0);
        canTalon.clearMotionProfileTrajectories();
    }

    /**
     * Starts running the loaded motion profile.
     */
    public void startRunningMP() {
        setpoint = SetValueMotionProfile.Enable.value;
        canTalon.set(ControlMode.MotionProfile, SetValueMotionProfile.Enable.value);
    }

    /**
     * Holds the current position point in MP mode.
     */
    public void holdPositionMP() {
        setpoint = SetValueMotionProfile.Hold.value;
        canTalon.set(ControlMode.MotionProfile, SetValueMotionProfile.Hold.value);
    }

    /**
     * Disables the talon and loads the given profile into the talon.
     *
     * @param data The profile to load.
     */
    public void loadProfile(MotionProfileData data) {
        bottomBufferLoader.stop();
        setpoint = SetValueMotionProfile.Disable.value;
        canTalon.set(ControlMode.MotionProfile, SetValueMotionProfile.Disable.value);
        //Reset the Talon
        clearMP();

        //Declare this out here to avoid garbage collection
        double feedforward;

        //Set proper PID constants
        if (data.isInverted()) {
            if (data.isVelocityOnly()) {
                canTalon.config_kP(1, 0, 0);
                canTalon.config_kI(1, 0, 0);
                canTalon.config_kD(1, 0, 0);
            } else {
                canTalon.config_kP(1, currentGearSettings.getMotionProfilePRev(), 0);
                canTalon.config_kI(1, currentGearSettings.getMotionProfileIRev(), 0);
                canTalon.config_kD(1, currentGearSettings.getMotionProfileDRev(), 0);
            }
        } else {
            if (data.isVelocityOnly()) {
                canTalon.config_kP(1, 0, 0);
                canTalon.config_kI(1, 0, 0);
                canTalon.config_kD(1, 0, 0);
            } else {
                canTalon.config_kP(1, currentGearSettings.getMotionProfilePFwd(), 0);
                canTalon.config_kI(1, currentGearSettings.getMotionProfileIFwd(), 0);
                canTalon.config_kD(1, currentGearSettings.getMotionProfileDFwd(), 0);
            }
        }

        canTalon.config_kF(1, 1023. / 12., 0);

        //Only call position getter once
        double startPosition = data.resetPosition() ? 0 : getPositionFeet();

        //Set point time
        canTalon.configMotionProfileTrajectoryPeriod(data.getPointTimeMillis(), 0);

        //Load in profiles
        for (int i = 0; i < data.getData().length; ++i) {
            TrajectoryPoint point = new TrajectoryPoint();
            //Have to set this so the Talon doesn't throw a null pointer. May be fixed in a future release.
            point.timeDur = TrajectoryPoint.TrajectoryDuration.Trajectory_Duration_0ms;

            //Set parameters that are true for all points
            point.profileSlotSelect0 = 1;        // gain selection, we always put MP gains in slot 1.

            // Set all the fields of the profile point
            point.position = feetToEncoder(startPosition + (data.getData()[i][0] * (data.isInverted() ? -1 : 1)));

            if (data.isInverted()) {
                feedforward = currentGearSettings.getFeedForwardComponent().calcMPVoltage(-data.getData()[i][0],
                        -data.getData()[i][1], -data.getData()[i][2]);
            } else {
                feedforward = currentGearSettings.getFeedForwardComponent().calcMPVoltage(data.getData()[i][0],
                        data.getData()[i][1], data.getData()[i][2]);
            }
            point.velocity = feedforward;

            //Doing vel+accel shouldn't lead to impossible setpoints, so if it does, we log so we know to change
            // either the profile or kA.
            if (Math.abs(feedforward) > 12) {
                System.out.println("Point " + Arrays.toString(data.getData()[i]) + " has an unattainable " +
                        "velocity+acceleration setpoint!");
                Logger.addEvent("Point " + Arrays.toString(data.getData()[i]) + " has an unattainable " +
                        "velocity+acceleration setpoint!", this.getClass());
            }
            point.zeroPos = i == 0 && data.resetPosition(); // If it's the first point, set the encoder position to 0.
            point.isLastPoint = (i + 1) == data.getData().length; // If it's the last point, isLastPoint = true
            // Send the point to the Talon's buffer
            canTalon.pushMotionProfileTrajectory(point);
        }
        bottomBufferLoader.startPeriodic(updaterProcessPeriodSecs);
    }

    /**
     * Process the motion profile buffer and stop when the top buffer is empty.
     */
    protected void processMotionProfileBuffer() {
        canTalon.processMotionProfileBuffer();
        if (canTalon.getMotionProfileTopLevelBufferCount() == 0) {
            bottomBufferLoader.stop();
        }
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @NotNull
    @Override
    public String[] getHeader() {
        return new String[]{
                "velocity",
                "position",
                "setpoint",
                "error",
                "battery_voltage",
                "voltage",
                "current",
                "control_mode",
                "gear",
                "resistance"
        };
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @Nullable
    @Override
    public Object[] getData() {
        if (voltagePerCurrentLinReg != null && PDP != null) {
            voltagePerCurrentLinReg.addPoint(getOutputCurrent(), PDP.getVoltage() - getBatteryVoltage());
        }
        return new Object[]{
                getVelocity(),
                getPositionFeet(),
                getSetpoint(),
                getError(),
                getBatteryVoltage(),
                getOutputVoltage(),
                getOutputCurrent(),
                getControlMode(),
                getGear(),
                (voltagePerCurrentLinReg != null && PDP != null) ? -voltagePerCurrentLinReg.getSlope() : null
        };
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getLogName() {
        return name;
    }

    /**
     * An object representing the CANTalon settings that are different for each gear.
     */
    protected static class PerGearSettings {

        /**
         * The gear number this is the settings for.
         */
        private final int gear;

        /**
         * The forwards and reverse peak output voltages.
         */
        private final double fwdPeakOutputVoltage, revPeakOutputVoltage;

        /**
         * The forwards and reverse nominal output voltages.
         */
        private final double fwdNominalOutputVoltage, revNominalOutputVoltage;

        /**
         * The ramp rate, in volts/sec. null means no ramp rate.
         */
        @Nullable
        private final Double rampRate;

        /**
         * The maximum speed of the motor in this gear, in FPS. Used for throttle scaling.
         */
        @Nullable
        private final Double maxSpeed;

        /**
         * The PID constants for the motor in this gear. Ignored if maxSpeed is null.
         */
        private final double kP, kI, kD;

        /**
         * The forwards PID constants for motion profiles in this gear. Ignored if maxSpeed is null.
         */
        private final double motionProfilePFwd, motionProfileIFwd, motionProfileDFwd;

        /**
         * The reverse PID constants for motion profiles in this gear. Ignored if maxSpeed is null.
         */
        private final double motionProfilePRev, motionProfileIRev, motionProfileDRev;

        /**
         * The component for calculating feedforwards in closed-loop control modes. Ignored if maxSpeed is null.
         */
        @NotNull
        private final FeedForwardComponent feedForwardComponent;

        /**
         * The maximum velocity for motion magic mode, in FPS. Can be null to not use motion magic.
         */
        @Nullable
        private final Double motionMagicMaxVel;

        /**
         * The maximum acceleration for motion magic mode, in FPS per second.
         */
        private final double motionMagicMaxAccel;

        /**
         * Default constructor.
         *
         * @param gearNum                 The gear number this is the settings for. Ignored if gear isn't null.
         * @param gear                    The gear this is the settings for. Can be null.
         * @param fwdPeakOutputVoltage    The peak output voltage for closed-loop modes in the forwards direction, in
         *                                volts. Defaults to 12.
         * @param revPeakOutputVoltage    The peak output voltage for closed-loop modes in the reverse direction, in
         *                                volts. Defaults to -fwdPeakOutputVoltage.
         * @param fwdNominalOutputVoltage The minimum output voltage for closed-loop modes in the forwards direction.
         *                                This does not rescale, it just sets any output below this voltage to this
         *                                voltage. Defaults to 0.
         * @param revNominalOutputVoltage The minimum output voltage for closed-loop modes in the reverse direction.
         *                                This does not rescale, it just sets any output below this voltage to this
         *                                voltage. Defaults to -fwdNominalOutputVoltage.
         * @param rampRate                The ramp rate, in volts/sec. Can be null, and if it is, no ramp rate is used.
         * @param maxSpeed                The maximum speed of the motor in this gear, in FPS. Used for throttle
         *                                scaling. Ignored if kVFwd is null. Calculated from the drive characterization
         *                                terms if null.
         * @param kP                      The proportional PID constant for the motor in this gear. Ignored if kVFwd is
         *                                null. Defaults to 0.
         * @param kI                      The integral PID constant for the motor in this gear. Ignored if kVFwd is
         *                                null. Defaults to 0.
         * @param kD                      The derivative PID constant for the motor in this gear. Ignored if kVFwd is
         *                                null. Defaults to 0.
         * @param motionProfilePFwd       The proportional PID constant for forwards motion profiles in this gear.
         *                                Ignored if kVFwd is null. Defaults to 0.
         * @param motionProfileIFwd       The integral PID constant for forwards motion profiles in this gear. Ignored
         *                                if kVFwd is null. Defaults to 0.
         * @param motionProfileDFwd       The derivative PID constant for forwards motion profiles in this gear. Ignored
         *                                if kVFwd is null. Defaults to 0.
         * @param motionProfilePRev       The proportional PID constant for reverse motion profiles in this gear.
         *                                Ignored if kVFwd is null. Defaults to motionProfilePFwd.
         * @param motionProfileIRev       The integral PID constant for reverse motion profiles in this gear. Ignored if
         *                                kVFwd is null. Defaults to motionProfileIFwd.
         * @param motionProfileDRev       The derivative PID constant for reverse motion profiles in this gear. Ignored
         *                                if kVFwd is null. Defaults to motionProfileDFwd.
         * @param feedForwardComponent    The component for calculating feedforwards in closed-loop control modes.
         *                                Ignored if maxSpeed is null. Defaults to no feedforward.
         * @param motionMagicMaxVel       The maximum velocity for motion magic mode, in FPS. Can be null to not use
         *                                motion magic.
         * @param motionMagicMaxAccel     The maximum acceleration for motion magic mode, in FPS per second.
         */
        @JsonCreator
        public PerGearSettings(int gearNum,
                               @Nullable Shiftable.gear gear,
                               @Nullable Double fwdPeakOutputVoltage,
                               @Nullable Double revPeakOutputVoltage,
                               @Nullable Double fwdNominalOutputVoltage,
                               @Nullable Double revNominalOutputVoltage,
                               @Nullable Double rampRate,
                               @Nullable Double maxSpeed,
                               double kP,
                               double kI,
                               double kD,
                               double motionProfilePFwd,
                               double motionProfileIFwd,
                               double motionProfileDFwd,
                               @Nullable Double motionProfilePRev,
                               @Nullable Double motionProfileIRev,
                               @Nullable Double motionProfileDRev,
                               @Nullable FeedForwardComponent feedForwardComponent,
                               @Nullable Double motionMagicMaxVel,
                               double motionMagicMaxAccel) {
            this.gear = gear != null ? gear.getNumVal() : gearNum;
            this.fwdPeakOutputVoltage = fwdPeakOutputVoltage != null ? fwdPeakOutputVoltage : 12;
            this.revPeakOutputVoltage = revPeakOutputVoltage != null ? revPeakOutputVoltage :
                    -this.fwdPeakOutputVoltage;
            this.fwdNominalOutputVoltage = fwdNominalOutputVoltage != null ? fwdNominalOutputVoltage : 0;
            this.revNominalOutputVoltage = revNominalOutputVoltage != null ? revNominalOutputVoltage :
                    -this.fwdNominalOutputVoltage;
            this.rampRate = rampRate;
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.motionProfilePFwd = motionProfilePFwd;
            this.motionProfileIFwd = motionProfileIFwd;
            this.motionProfileDFwd = motionProfileDFwd;
            this.motionProfilePRev = motionProfilePRev != null ? motionProfilePRev : this.motionProfilePFwd;
            this.motionProfileIRev = motionProfileIRev != null ? motionProfileIRev : this.motionProfileIFwd;
            this.motionProfileDRev = motionProfileDRev != null ? motionProfileDRev : this.motionProfileDFwd;
            this.feedForwardComponent = feedForwardComponent != null ? feedForwardComponent :
                    FeedForwardComponent.getZeroFeedForward();
            this.maxSpeed = maxSpeed;
            this.motionMagicMaxVel = motionMagicMaxVel;
            this.motionMagicMaxAccel = motionMagicMaxAccel;
        }

        /**
         * Empty constructor that uses all default options.
         */
        public PerGearSettings() {
            this(0, null, null, null, null, null, null, null, 0, 0, 0, 0, 0, 0, null, null, null, null, null, 0);
        }

        /**
         * @return The gear number this is the settings for.
         */
        public int getGear() {
            return gear;
        }

        /**
         * @return The peak output voltage for closed-loop modes in the forwards direction, in volts.
         */
        public double getFwdPeakOutputVoltage() {
            return fwdPeakOutputVoltage;
        }

        /**
         * @return The peak output voltage for closed-loop modes in the reverse direction, in volts.
         */
        public double getRevPeakOutputVoltage() {
            return revPeakOutputVoltage;
        }

        /**
         * @return The minimum output voltage for closed-loop modes in the forwards direction. This does not rescale, it
         * just sets any output below this voltage to this voltage.
         */
        public double getFwdNominalOutputVoltage() {
            return fwdNominalOutputVoltage;
        }

        /**
         * @return The minimum output voltage for closed-loop modes in the reverse direction. This does not rescale, it
         * just sets any output below this voltage to this voltage.
         */
        public double getRevNominalOutputVoltage() {
            return revNominalOutputVoltage;
        }

        /**
         * @return The ramp rate, in volts/sec.
         */
        @Nullable
        public Double getRampRate() {
            return rampRate;
        }

        /**
         * @return The maximum speed of the motor in this gear, in FPS.
         */
        @Nullable
        public Double getMaxSpeed() {
            return maxSpeed;
        }

        /**
         * @return The proportional PID constant for the motor in this gear.
         */
        public double getkP() {
            return kP;
        }

        /**
         * @return The integral PID constant for the motor in this gear.
         */
        public double getkI() {
            return kI;
        }

        /**
         * @return The derivative PID constant for the motor in this gear.
         */
        public double getkD() {
            return kD;
        }

        /**
         * @return The proportional PID constant for motion profiles in this gear.
         */
        public double getMotionProfilePFwd() {
            return motionProfilePFwd;
        }

        /**
         * @return The integral PID constant for motion profiles in this gear.
         */
        public double getMotionProfileIFwd() {
            return motionProfileIFwd;
        }

        /**
         * @return The derivative PID constant for motion profiles in this gear.
         */
        public double getMotionProfileDFwd() {
            return motionProfileDFwd;
        }

        /**
         * @return The proportional PID constant for reverse motion profiles in this gear.
         */
        public double getMotionProfilePRev() {
            return motionProfilePRev;
        }

        /**
         * @return The integral PID constant for reverse motion profiles in this gear.
         */
        public double getMotionProfileIRev() {
            return motionProfileIRev;
        }

        /**
         * @return The derivative PID constant for reverse motion profiles in this gear.
         */
        public double getMotionProfileDRev() {
            return motionProfileDRev;
        }

        /**
         * @return The component for calculating feedforwards in closed-loop control modes.
         */
        @NotNull
        public FeedForwardComponent getFeedForwardComponent() {
            return feedForwardComponent;
        }

        /**
         * @return The maximum velocity for motion magic mode, in FPS. Can be null to not use motion magic.
         */
        @Nullable
        public Double getMotionMagicMaxVel() {
            return motionMagicMaxVel;
        }

        /**
         * @return The maximum acceleration for motion magic mode, in FPS per second.
         */
        public double getMotionMagicMaxAccel() {
            return motionMagicMaxAccel;
        }
    }
}
