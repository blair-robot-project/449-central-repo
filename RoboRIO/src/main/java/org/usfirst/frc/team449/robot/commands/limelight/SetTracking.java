package org.usfirst.frc.team449.robot.commands.limelight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.InstantCommand;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetTracking extends InstantCommand {

  private final boolean track;

  @JsonCreator
  public SetTracking(@JsonProperty(required = true) boolean track) {
    this.track = track;
  }

  @Override
  public void execute() {
    if (track) {
      // Turn on LEDs
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1);
      // Switch to vision processing mode
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(0);
    } else {
      // Turn off LEDs
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
      // Switch to driver camera mode to increase exposure
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(1);
    }
  }
}
