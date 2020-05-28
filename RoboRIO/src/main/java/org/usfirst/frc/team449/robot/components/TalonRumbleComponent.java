package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.annotation.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.rumbleable.Rumbleable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedTalon;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TalonRumbleComponent implements Runnable {

  MappedTalon talon;
  Rumbleable joystick;
  boolean inverted;
  Double rumbleAmount;

  @JsonCreator
  public TalonRumbleComponent(
      @JsonProperty(required = true) MappedTalon talon,
      @JsonProperty(required = true) Rumbleable joystick,
      @Nullable Double rumbleAmount,
      boolean inverted) {
    this.talon = talon;
    this.joystick = joystick;
    this.inverted = inverted;
    this.rumbleAmount = rumbleAmount != null ? rumbleAmount : 1;
  }

  @Override
  public void run() {
    if (this.talon.isInhibitedForward() ^ this.inverted) {
      this.joystick.rumble(0, this.rumbleAmount);
    } else if (this.talon.isInhibitedReverse() ^ this.inverted) {
      this.joystick.rumble(this.rumbleAmount, 0);
    } else {
      this.joystick.rumble(0, 0);
    }
  }
}
