package org.usfirst.frc.team449.robot.subsystem.complex.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.loggable.Loggable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.FPSTalon;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.subsystem.interfaces.flywheel.SubsystemFlywheel;

/**
 * A flywheel multiSubsystem with a single flywheel and a single-motor feeder system.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoggingFlywheel extends YamlSubsystem implements Loggable, SubsystemFlywheel {

    /**
     * The flywheel's Talon
     */
    @NotNull
    private final FPSTalon shooterTalon;

    /**
     * The feeder's motor
     */
    @NotNull
    private final SimpleMotor feederMotor;

    /**
     * How fast to run the feeder, from [-1, 1]
     */
    private final double feederThrottle;

    /**
     * Throttle at which to run the multiSubsystem, from [-1, 1]
     */
    private final double shooterThrottle;

    /**
     * Time from giving the multiSubsystem voltage to being ready to fire, in milliseconds.
     */
    private final long spinUpTime;

    /**
     * Whether the flywheel is currently commanded to spin
     */
    @NotNull
    private FlywheelState state;

    /**
     * Default constructor
     *
     * @param shooterTalon    The TalonSRX controlling the flywheel.
     * @param shooterThrottle The throttle, from [-1, 1], at which to run the multiSubsystem.
     * @param feederMotor     The motor controlling the feeder.
     * @param feederThrottle  The throttle, from [-1, 1], at which to run the feeder.
     * @param spinUpTimeSecs  The amount of time, in seconds, it takes for the multiSubsystem to get up to speed.
     *                        Defaults to 0.
     */
    @JsonCreator
    public LoggingFlywheel(@NotNull @JsonProperty(required = true) FPSTalon shooterTalon,
                           @JsonProperty(required = true) double shooterThrottle,
                           @NotNull @JsonProperty(required = true) SimpleMotor feederMotor,
                           @JsonProperty(required = true) double feederThrottle,
                           double spinUpTimeSecs) {
        this.shooterTalon = shooterTalon;
        this.shooterThrottle = shooterThrottle;
        this.feederMotor = feederMotor;
        this.feederThrottle = feederThrottle;
        state = FlywheelState.OFF;
        spinUpTime = (long) (spinUpTimeSecs * 1000.);
    }

    /**
     * Do nothing
     */
    @Override
    protected void initDefaultCommand() {
        //Do nothing!
    }

    /**
     * Get the headers for the data this subsystem logs every loop.
     *
     * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
     */
    @NotNull
    @Override
    public String[] getHeader() {
        return new String[]{"speed",
                "setpoint",
                "error",
                "voltage",
                "current"};
    }

    /**
     * Get the data this subsystem logs every loop.
     *
     * @return An N-length array of Objects, where N is the number of labels given by getHeader.
     */
    @NotNull
    @Override
    public Object[] getData() {
        return new Object[]{shooterTalon.getVelocity(),
                shooterTalon.getSetpoint(),
                shooterTalon.getError(),
                shooterTalon.getOutputVoltage(),
                shooterTalon.getOutputCurrent()};
    }

    /**
     * Get the name of this object.
     *
     * @return A string that will identify this object in the log file.
     */
    @NotNull
    @Override
    public String getName() {
        return "loggingShooter";
    }

    /**
     * Turn the multiSubsystem on to a map-specified speed.
     */
    @Override
    public void turnFlywheelOn() {
        shooterTalon.enable();
        shooterTalon.setVelocity(shooterThrottle);
    }

    /**
     * Turn the multiSubsystem off.
     */
    @Override
    public void turnFlywheelOff() {
        shooterTalon.disable();
    }

    /**
     * Start feeding balls into the multiSubsystem.
     */
    @Override
    public void turnFeederOn() {
        feederMotor.enable();
        feederMotor.setVelocity(feederThrottle);
    }

    /**
     * Stop feeding balls into the multiSubsystem.
     */
    @Override
    public void turnFeederOff() {
        feederMotor.disable();
    }

    /**
     * @return The current state of the multiSubsystem.
     */
    @NotNull
    @Override
    public FlywheelState getFlywheelState() {
        return state;
    }

    /**
     * @param state The state to switch the multiSubsystem to.
     */
    @Override
    public void setFlywheelState(@NotNull FlywheelState state) {
        this.state = state;
    }

    /**
     * @return Time from giving the multiSubsystem voltage to being ready to fire, in milliseconds.
     */
    @Override
    public long getSpinUpTimeMillis() {
        return spinUpTime;
    }
}
