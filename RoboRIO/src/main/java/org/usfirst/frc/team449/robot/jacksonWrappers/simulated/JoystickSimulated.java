package org.usfirst.frc.team449.robot.jacksonWrappers.simulated;

import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that extends {@link MappedJoystick} that does not rely on the existence of actual hardware.
 * This class is automatically instantiated by the MappedJoystick factory method when the robot is
 * running in a simulation and should not be otherwise referenced in code.
 */
public class JoystickSimulated extends MappedJoystick {
  @NotNull private final Map<String, Boolean> keyStates = new ConcurrentHashMap<>();
  @NotNull private final String logName;
  @NotNull private final String logPrefix;

  /**
   * Default constructor
   *
   * @param port The USB port of this joystick, on [0, 5].
   */
  public JoystickSimulated(final int port) {
    super(port);

    this.logName = "SIMJOY " + this.getPort();
    this.logPrefix = "[" + this.logName + "] ";

    // The virtual joystick user interface.
    new SimulatedJoystickUI(this.logName) {};
  }

  /**
   * Get the button value (starting at button 1).
   *
   * <p>The buttons are returned in a single 16 bit value with one bit representing the state of
   * each button. The appropriate button is returned as a boolean value.
   *
   * @param button The button number to be read (starting at 1)
   * @return The state of the button.
   */
  @Override
  public boolean getRawButton(final int button) {
    return this.keyStates.getOrDefault(String.valueOf(button), false);
  }

  /**
   * Whether the button was pressed since the last check. Button indexes begin at 1.
   *
   * @param button The button index, beginning at 1.
   * @return Whether the button was pressed since the last check.
   */
  @Override
  public boolean getRawButtonPressed(final int button) {
    return false;
  }

  /**
   * Whether the button was released since the last check. Button indexes begin at 1.
   *
   * @param button The button index, beginning at 1.
   * @return Whether the button was released since the last check.
   */
  @Override
  public boolean getRawButtonReleased(final int button) {
    return false;
  }

  /**
   * Get the value of the axis.
   *
   * @param axis The axis to read, starting at 0.
   * @return The value of the axis.
   */
  @Override
  public double getRawAxis(final int axis) {
    return this.keyStates.getOrDefault("0", false) ? 10 : 0;
  }

  /**
   * Get the angle in degrees of a POV on the HID.
   *
   * <p>The POV angles start at 0 in the up direction, and increase clockwise (eg right is 90,
   * upper-left is 315).
   *
   * @param pov The index of the POV to read (starting at 0)
   * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
   */
  @Override
  public int getPOV(final int pov) {
    return 0;
  }

  @Override
  public int getPOV() {
    return 0;
  }

  /**
   * Get the number of axes for the HID.
   *
   * @return the number of axis for the current HID
   */
  @Override
  public int getAxisCount() {
    return 1;
  }

  /** For the current HID, return the number of POVs. */
  @Override
  public int getPOVCount() {
    return 1;
  }

  /** For the current HID, return the number of buttons. */
  @Override
  public int getButtonCount() {
    return 5;
  }

  /**
   * Get the type of the HID.
   *
   * @return the type of the HID.
   */
  @Override
  public HIDType getType() {
    return HIDType.kXInputArcadePad;
  }

  /**
   * Get the name of the HID.
   *
   * @return the name of the HID.
   */
  @Override
  public String getName() {
    return "Simulated joystick on port " + this.getPort();
  }

  /**
   * Get the axis type of a joystick axis.
   *
   * @return the axis type of a joystick axis.
   */
  @Override
  public int getAxisType(final int axis) {
    return 0;
  }

  /**
   * Set a single HID output value for the HID.
   *
   * @param outputNumber The index of the output to set (1-32)
   * @param value The value to set the output to
   */
  @Override
  public void setOutput(final int outputNumber, final boolean value) {}

  /**
   * Set all HID output values for the HID.
   *
   * @param value The 32 bit output value (1 bit for each output)
   */
  @Override
  public void setOutputs(final int value) {}

  /**
   * Set the rumble output for the HID. The DS currently supports 2 rumble values, left rumble and
   * right rumble.
   *
   * @param type Which rumble value to set
   * @param value The normalized value (0 to 1) to set the rumble to
   */
  @Override
  public void setRumble(final RumbleType type, final double value) {}

  /**
   * Get the channel currently associated with the X axis.
   *
   * @return The channel for the axis.
   */
  @Override
  public int getXChannel() {
    return 0;
  }

  /**
   * Set the channel associated with the X axis.
   *
   * @param channel The channel to set the axis to.
   */
  @Override
  public void setXChannel(final int channel) {}

  /**
   * Get the channel currently associated with the Y axis.
   *
   * @return The channel for the axis.
   */
  @Override
  public int getYChannel() {
    return 0;
  }

  /**
   * Set the channel associated with the Y axis.
   *
   * @param channel The channel to set the axis to.
   */
  @Override
  public void setYChannel(final int channel) {}

  /**
   * Get the channel currently associated with the Z axis.
   *
   * @return The channel for the axis.
   */
  @Override
  public int getZChannel() {
    return 0;
  }

  /**
   * Set the channel associated with the Z axis.
   *
   * @param channel The channel to set the axis to.
   */
  @Override
  public void setZChannel(final int channel) {}

  /**
   * Get the channel currently associated with the twist axis.
   *
   * @return The channel for the axis.
   */
  @Override
  public int getTwistChannel() {
    return 0;
  }

  /**
   * Set the channel associated with the twist axis.
   *
   * @param channel The channel to set the axis to.
   */
  @Override
  public void setTwistChannel(final int channel) {}

  /**
   * Get the channel currently associated with the throttle axis.
   *
   * @return The channel for the axis.
   */
  @Override
  public int getThrottleChannel() {
    return 0;
  }

  /**
   * Set the channel associated with the throttle axis.
   *
   * @param channel The channel to set the axis to.
   */
  @Override
  public void setThrottleChannel(final int channel) {}

  /**
   * Get the z position of the HID.
   *
   * @return the z position
   */
  @Override
  public double getZ() {
    return 0;
  }

  /**
   * Get the twist value of the current joystick. This depends on the mapping of the joystick
   * connected to the current port.
   *
   * @return The Twist value of the joystick.
   */
  @Override
  public double getTwist() {
    return 0;
  }

  /**
   * Get the throttle value of the current joystick. This depends on the mapping of the joystick
   * connected to the current port.
   *
   * @return The Throttle value of the joystick.
   */
  @Override
  public double getThrottle() {
    return 0;
  }

  /**
   * Read the state of the trigger on the joystick.
   *
   * @return The state of the trigger.
   */
  @Override
  public boolean getTrigger() {
    return false;
  }

  /**
   * Whether the trigger was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  @Override
  public boolean getTriggerPressed() {
    return false;
  }

  /**
   * Whether the trigger was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  @Override
  public boolean getTriggerReleased() {
    return false;
  }

  /**
   * Read the state of the top button on the joystick.
   *
   * @return The state of the top button.
   */
  @Override
  public boolean getTop() {
    return false;
  }

  /**
   * Whether the top button was pressed since the last check.
   *
   * @return Whether the button was pressed since the last check.
   */
  @Override
  public boolean getTopPressed() {
    return false;
  }

  /**
   * Whether the top button was released since the last check.
   *
   * @return Whether the button was released since the last check.
   */
  @Override
  public boolean getTopReleased() {
    return false;
  }

  /**
   * Get the magnitude of the direction vector formed by the joystick's current position relative to
   * its origin.
   *
   * @return The magnitude of the direction vector
   */
  @Override
  public double getMagnitude() {
    return 0;
  }

  /**
   * Get the direction of the vector formed by the joystick and its origin in radians.
   *
   * @return The direction of the vector in radians
   */
  @Override
  public double getDirectionRadians() {
    return 0;
  }

  /**
   * Get the direction of the vector formed by the joystick and its origin in degrees.
   *
   * @return The direction of the vector in degrees
   */
  @Override
  public double getDirectionDegrees() {
    return 0;
  }

  /**
   * Rumble at a given strength on each side of the device.
   *
   * @param left The strength to rumble the left side, on [-1, 1]
   * @param right The strength to rumble the right side, on [-1, 1]
   */
  @Override
  public void rumble(final double left, final double right) {}

  // Janky awt for now.
  private class SimulatedJoystickUI extends JFrame {
    private final JLabel[][] buttonStateLayout = new JLabel[3][3];
    private final Map<String, JLabel> buttonStateLabels = new HashMap<>();

    {
      this.setLayout(new GridLayout(3, 3));
      for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
          final var label = new JLabel();
          label.setOpaque(true);
          label.setHorizontalAlignment(SwingConstants.CENTER);
          this.add(String.format("r%dc%d", r, c), this.buttonStateLayout[r][c] = label);
        }
      }

      this.buttonStateLabels.put("1", this.buttonStateLayout[0][1]);
      this.buttonStateLabels.put("2", this.buttonStateLayout[1][2]);
      this.buttonStateLabels.put("3", this.buttonStateLayout[2][1]);
      this.buttonStateLabels.put("4", this.buttonStateLayout[1][0]);

      this.buttonStateLabels.forEach(
          (name, label) -> {
            label.setBackground(Color.LIGHT_GRAY);
            label.setText(name);
          });

      this.doLayout();

      if (!Robot.isUnitTesting()) this.setVisible(true);
    }

    public SimulatedJoystickUI(final String logName) {
      super(logName);
    }

    @Override
    protected void processKeyEvent(final KeyEvent e) {
      final String keyName = KeyEvent.getKeyText(e.getKeyCode());

      final boolean newState;
      switch (e.getID()) {
        case KeyEvent.KEY_PRESSED:
          newState = true;
          break;

        case KeyEvent.KEY_RELEASED:
          newState = false;
          break;

        default:
          return;
      }

      if (JoystickSimulated.this.keyStates.getOrDefault(keyName, false) != newState) {
        System.out.println(
            JoystickSimulated.this.logPrefix + keyName + (newState ? " [#]" : " [ ]"));
        if (this.buttonStateLabels.containsKey(keyName))
          this.buttonStateLabels
              .get(keyName)
              .setBackground(newState ? Color.GREEN.darker() : Color.LIGHT_GRAY);

        JoystickSimulated.this.keyStates.put(keyName, newState);
      }
    }
  }
}
