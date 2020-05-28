package org.usfirst.frc.team449.robot.generalInterfaces;

import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.Logger;
import io.github.oblarg.oblog.annotations.Log;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MotorContainer implements Loggable {
  @NotNull @Log.Exclude private static final MotorContainer instance = new MotorContainer();

  @NotNull private final List<SmartMotor> motors = new ArrayList<>();

  private MotorContainer() {
    Logger.setCycleWarningsEnabled(false);
  }

  /**
   * Registers a motor to be included in the container.
   *
   * @param motor the motor to be registered
   */
  public static void register(@NotNull final SmartMotor motor) {
    instance.motors.add(motor);
  }

  /**
   * Gets the singleton instance.
   *
   * @return the single instantiated instance of the class
   */
  @NotNull
  public static MotorContainer getInstance() {
    return instance;
  }

  @Override
  public String configureLogName() {
    return "Motors";
  }
}
