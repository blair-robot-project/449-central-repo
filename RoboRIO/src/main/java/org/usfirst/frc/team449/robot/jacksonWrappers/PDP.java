package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.generalInterfaces.updatable.Updatable;

/**
 * An object representing the {@link PowerDistributionPanel} that logs power, current, and
 * resistance.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PDP implements Loggable, Updatable {

  /** The WPILib PDP this is a wrapper on. */
  @NotNull private final PowerDistributionPanel PDP;

  /** The component for doing linear regression to find the resistance. */
  @Nullable private final RunningLinRegComponent voltagePerCurrentLinReg;

  /** The cached values from the PDP object this wraps. */
  private double voltage, totalCurrent, temperature, resistance, unloadedVoltage;

  /**
   * Default constructor.
   *
   * @param canID CAN ID of the PDP. Defaults to 0.
   */
  @JsonCreator
  public PDP(final int canID, @Nullable final RunningLinRegComponent voltagePerCurrentLinReg) {
    this.PDP = new PowerDistributionPanel(canID);
    this.voltagePerCurrentLinReg = voltagePerCurrentLinReg;
    this.voltage = 0;
    this.totalCurrent = 0;
    this.temperature = 0;
    this.resistance = 0;
    this.unloadedVoltage = 0;
  }

  /**
   * Query the input voltage of the PDP.
   *
   * @return The voltage of the PDP in volts
   */
  @Log
  public double getVoltage() {
    return voltage;
  }

  /**
   * Query the current of all monitored PDP channels (0-15).
   *
   * @return The current of all the channels in Amperes
   */
  @Log
  public double getTotalCurrent() {
    return totalCurrent;
  }

  /**
   * Query the temperature of the PDP.
   *
   * @return The temperature of the PDP in degrees Celsius.
   */
  @Log
  public double getTemperature() {
    return temperature;
  }

  /**
   * Get the resistance of the wires leading to the PDP.
   *
   * @return Resistance in ohms, or null if not calculating resistance.
   */
  @Nullable
  @Log
  public Double getResistance() {
    return voltagePerCurrentLinReg == null ? null : resistance;
  }

  /**
   * Get the voltage at the PDP when there's no load on the battery.
   *
   * @return Voltage in volts when there's 0 amps of current draw, or null if not calculating
   *     resistance.
   */
  @Nullable
  @Log
  public Double getUnloadedVoltage() {
    return voltagePerCurrentLinReg == null ? null : unloadedVoltage;
  }

  /** Updates all cached values with current ones. */
  @Override
  public void update() {
    this.totalCurrent = PDP.getTotalCurrent();
    this.voltage = PDP.getVoltage();
    this.temperature = PDP.getTemperature();
    if (voltagePerCurrentLinReg != null) {
      voltagePerCurrentLinReg.addPoint(totalCurrent, voltage);
      this.unloadedVoltage = voltagePerCurrentLinReg.getIntercept();
      this.resistance = -voltagePerCurrentLinReg.getSlope();
    }
  }
}
