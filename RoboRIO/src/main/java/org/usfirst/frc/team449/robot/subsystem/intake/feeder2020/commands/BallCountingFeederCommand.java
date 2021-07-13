package org.usfirst.frc.team449.robot.subsystem.intake.feeder2020.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.subsystem.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.intake.SubsystemIntake.IntakeMode;

import java.util.function.BooleanSupplier;

/** A feeder that counts balls */
@JsonIdentityInfo(generator = StringIdGenerator.class)
public class BallCountingFeederCommand extends CommandBase implements Loggable {

  private final SubsystemIntake feeder;
  private final BooleanSupplier sensor1, sensor2;
  private final int ballThreshold;
  private final IntakeMode defaultMode;
  private final Command flywheelStopCommand;
  // private final double updateTimeMillis;

  /** The previous values from the IR sensors */
  private boolean sensor1Cached, sensor2Cached, flywheelStoppedCached;
  /** The current number of balls inside the robot */
  @Log private int numBalls;

  private long lastTime;

  /**
   * @param feeder the feeder feeder to operate on
   * @param sensor1 the first sensor of the transition from intake to feeder
   * @param sensor2 the second sensor of the transition from intake to feeder
   * @param flywheelStopCommand to know if the shooter's stopped shooting
   * @param defaultMode The default intake mode
   * @param ballThreshold The number of balls that it will shoot at once before waiting /@param
   *     updateTimeMillis The time, in millisecs, to wait to update
   */
  @JsonCreator
  public BallCountingFeederCommand(
      @NotNull SubsystemIntake feeder,
      @NotNull BooleanSupplier sensor1,
      @NotNull BooleanSupplier sensor2,
      @NotNull @JsonProperty(required = true) Command flywheelStopCommand,
      @NotNull @JsonProperty(required = true) IntakeMode defaultMode,
      @JsonProperty(required = true) int ballThreshold
      /*@JsonProperty(required = true) double updateTimeMillis*/ ) {
    // todo consider using generics
    addRequirements((Subsystem) feeder);

    this.numBalls = 0;
    this.feeder = feeder;
    this.sensor1 = sensor1;
    this.sensor2 = sensor2;
    this.ballThreshold = ballThreshold;
    this.defaultMode = defaultMode;
    this.flywheelStopCommand = flywheelStopCommand;

    sensor1Cached = sensor1.getAsBoolean();
    sensor2Cached = sensor2.getAsBoolean();
    flywheelStoppedCached = flywheelStopCommand.isFinished();
    // this.updateTimeMillis = updateTimeMillis;
  }

  @Override
  public synchronized void execute() {
    long now = System.currentTimeMillis();
    System.out.println("Executed at " + now);
    System.out.println("Time to update = " + (now - lastTime));
    boolean sensor1Now = sensor1.getAsBoolean(),
        sensor2Now = sensor2.getAsBoolean(),
        flywheelStoppedNow = flywheelStopCommand.isFinished();
    // var mode = feeder.getMode();
    if (!flywheelStoppedCached && flywheelStopCommand.isFinished()) {
      if (!sensor2Now) {
        numBalls = 0;
      } else {
        // Having a ball still there wouldn't make sense. Maybe set numBalls to 1?
        // numBalls --;
      }
    }

    // todo check if sensor2 should also be checked
    if (!sensor1Now && sensor1Cached) {
      // A ball has moved in
      numBalls++;
    }

    //     If it's taking in balls and it's over the limit, stop
    //     If it's not supposed to be running, stop
    if ((numBalls > ballThreshold /*&& feeder.getMode() == IntakeMode.IN_FAST*/)
        || !shouldBeRunning()) {
      feeder.setMode(IntakeMode.OFF);
    } else {
      // keep going otherwise
      feeder.setMode(defaultMode);
    }

    // Update sensor values
    sensor1Cached = sensor1Now;
    sensor2Cached = sensor2Now;
    flywheelStoppedCached = flywheelStoppedNow;
  }

  /**
   * 2021 code to determine whether the balls were placed in the red or blue formations
   *
   * @return true if the system has a ball, false otherwise
   */
  public boolean hasBall() {
    return numBalls != 0;
  }

  public boolean shouldBeRunning() {
    return sensor1.getAsBoolean() || sensor2.getAsBoolean();
  }
}
