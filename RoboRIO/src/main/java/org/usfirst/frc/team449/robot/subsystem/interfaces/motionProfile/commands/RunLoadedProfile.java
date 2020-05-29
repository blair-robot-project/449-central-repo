package org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.SubsystemMP;

/** Runs the command that is currently loaded in the given subsystem. */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunLoadedProfile<T extends Subsystem & SubsystemMP> extends CommandBase {

  /** The amount of time this command is allowed to run for, in milliseconds. */
  private final long timeout;

  /** The subsystem to execute this command on. */
  @NotNull private final SubsystemMP subsystem;

  /** The time this command started running at. */
  private long startTime;

  /** Whether we're running a profile or waiting for the bottom-level buffer to fill. */
  private boolean runningProfile;

  private boolean startingFinished;

  /**
   * Default constructor.
   *
   * @param subsystem The subsystem to execute this command on.
   * @param timeout The max amount of time this subsystem is allowed to run for, in seconds.
   */
  @JsonCreator
  public RunLoadedProfile(
      @NotNull @JsonProperty(required = true) T subsystem,
      @JsonProperty(required = true) double timeout) {
    this.subsystem = subsystem;
    addRequirements(subsystem);

    // Convert to milliseconds.
    this.timeout = (long) (timeout * 1000.);

    runningProfile = false;
  }

  /** Record the start time. */
  @Override
  public void initialize() {
    // Record the start time.
    startTime = Clock.currentTimeMillis();
    Logger.addEvent("RunLoadedProfile init", this.getClass());
    runningProfile = false;
    startingFinished = subsystem.profileFinished();
  }

  /**
   * If the subsystem is ready to start running the profile and it's not running yet, start running
   * it.
   */
  @Override
  public void execute() {
    if (!runningProfile && startingFinished) {
      startingFinished = subsystem.profileFinished();
    }
    if (subsystem.readyToRunProfile() && !runningProfile && !startingFinished) {
      subsystem.startRunningLoadedProfile();
      runningProfile = true;
    }
  }

  /**
   * Finish when the profile finishes or the timeout is reached.
   *
   * @return true if the profile is finished or the timeout has been exceeded, false otherwise.
   */
  @Override
  public boolean isFinished() {
    if (Clock.currentTimeMillis() - startTime > timeout) {
      Logger.addEvent("Command timed out", this.getClass());
      System.out.println("RunLoadedProfile timed out!");
      return true;
    }
    return runningProfile && subsystem.profileFinished();
  }

  /** Hold position and log on exit. */
  @Override
  public void end(boolean interrupted) {
    subsystem.holdPosition();
    Logger.addEvent("RunLoadedProfile end.", this.getClass());
  }

  /** Disable and log if interrupted. */
  // TODO Remove this! @Override
  protected void interrupted() {
    subsystem.disable();
    Logger.addEvent("RunLoadedProfile interrupted!", this.getClass());
  }
}
