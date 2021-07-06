package org.usfirst.frc.team449.robot.javamaps;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.CommandContainer;
import org.usfirst.frc.team449.robot.RobotMap;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectionalWithGyroShiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.Polynomial;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.jacksonWrappers.FeedForwardCalculators.MappedFeedForwardCalculator;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;
import org.usfirst.frc.team449.robot.jacksonWrappers.PDP;
import org.usfirst.frc.team449.robot.jacksonWrappers.SlaveSparkMax;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;
import org.usfirst.frc.team449.robot.oi.throttles.ThrottlePolynomial;
import org.usfirst.frc.team449.robot.oi.throttles.ThrottleSum;
import org.usfirst.frc.team449.robot.oi.unidirectional.arcade.OIArcadeWithDPad;
import org.usfirst.frc.team449.robot.other.DefaultCommand;
import org.usfirst.frc.team449.robot.other.Updater;

import java.util.List;
import java.util.Map;

public class MapTemplate {

  // Drive system
  public static final int leftMasterPort = 1,
      leftMasterSlave1Port = 3,
      leftMasterSlave2Port = 5,
      rightMasterPort = 2,
      rightMasterSlave1Port = 4,
      rightMasterSlave2Port = 6;
  // Intake system
  public static final int intakeMotorPort = 7;

  // Solenoid ports
  public static final int intakeSolenoidForwardPort = 2, intakeSolenoidReversePort = 3;
  public static final int mechanismsJoystickPort = 0, driveJoystickPort = 1;

  @NotNull
  public static RobotMap createRobotMap() {
    var useCameraServer = false;
    var pdp = new PDP(0, new RunningLinRegComponent(250, 0.75));

    var mechanismsJoystick = new MappedJoystick(mechanismsJoystickPort);
    var driveJoystick = new MappedJoystick(driveJoystickPort);
    var joysticks = List.of(mechanismsJoystick, driveJoystick);

    var navx = new MappedAHRS(SerialPort.Port.kMXP, true);

    var enableBrakeMode = true;
    var unitPerRotation = 0.47877872; // meters per rotation
    var currentLimit = 50;
    var enableVoltageComp = true;
    var encoderCPR = 256;
    var startingGear = Shiftable.Gear.LOW;

    double lowGearMaxSpeed = 2.3, highGearMaxSpeed = 5.2;
    double lowGearKP = 0, highGearKP = 0.000001;

    var leftMaster =
        SmartMotor.create(
            SmartMotor.Type.SPARK,
            leftMasterPort,
            enableBrakeMode,
            "left",
            true,
            pdp,
            null,
            null,
            null,
            null,
            null,
            null, // 0.13333333333
            unitPerRotation,
            currentLimit,
            enableVoltageComp,
            List.of(
                new Shiftable.PerGearSettings(
                    -1,
                    Shiftable.Gear.LOW,
                    null,
                    null,
                    null,
                    null,
                    new MappedFeedForwardCalculator(0.128, 5.23, 0.0698),
                    null,
                    lowGearMaxSpeed,
                    0.0488998,
                    lowGearKP,
                    -0,
                    -0,
                    -0,
                    -0,
                    -0),
                new Shiftable.PerGearSettings(
                    -1,
                    Shiftable.Gear.HIGH,
                    null,
                    null,
                    null,
                    null,
                    new MappedFeedForwardCalculator(0.156, 2.01, 0.154),
                    null,
                    highGearMaxSpeed,
                    0.12936611,
                    highGearKP,
                    -0,
                    -0,
                    -0,
                    -0,
                    -0)),
            startingGear,
            null,
            null,
            null,
            null,
            null,
            null,
            encoderCPR,
            null,
            null,
            null,
            null,
            List.of(
                new SlaveSparkMax(leftMasterSlave1Port, false, pdp),
                new SlaveSparkMax(leftMasterSlave2Port, false, pdp)),
            null);
    var rightMaster =
        SmartMotor.create(
            SmartMotor.Type.SPARK,
            leftMasterPort,
            enableBrakeMode,
            "left",
            true,
            pdp,
            null,
            null,
            null,
            null,
            null,
            null, // 0.13333333333
            unitPerRotation,
            currentLimit,
            enableVoltageComp,
            List.of(
                new Shiftable.PerGearSettings(
                    -1,
                    Shiftable.Gear.LOW,
                    null,
                    null,
                    null,
                    null,
                    new MappedFeedForwardCalculator(0.139, 5.17, 0.0554),
                    null,
                    lowGearMaxSpeed,
                    0.0488998,
                    lowGearKP,
                    -0,
                    -0,
                    -0,
                    -0,
                    -0),
                new Shiftable.PerGearSettings(
                    -1,
                    Shiftable.Gear.HIGH,
                    null,
                    null,
                    null,
                    null,
                    new MappedFeedForwardCalculator(0.165, 2.01, 0.155),
                    null,
                    highGearMaxSpeed,
                    0.12936611,
                    highGearKP,
                    -0,
                    -0,
                    -0,
                    -0,
                    -0)),
            startingGear,
            null,
            null,
            null,
            null,
            null,
            null,
            encoderCPR,
            null,
            null,
            null,
            null,
            List.of(
                new SlaveSparkMax(rightMasterSlave1Port, false, pdp),
                new SlaveSparkMax(rightMasterSlave2Port, false, pdp)),
            null);
    var drive = new DriveUnidirectionalWithGyroShiftable(
        leftMaster,
        rightMaster,
        navx,
        0.61755,
        null, //TODO
        false);

    var subsystems = List.<Subsystem>of(drive);

    var smoothingTimeSecs = 0.04;
    var scale = 0.7;
    var rotThrottle =
        new ThrottlePolynomial(
            driveJoystick,
            0,
            0.08,
            smoothingTimeSecs,
            false,
            new Polynomial(Map.of(1., 0.5), null),
            scale);
    var fwdThrottle =
        new ThrottleSum(
            new Throttle[] {
              new ThrottlePolynomial(
                  driveJoystick,
                  3,
                  0.05,
                  smoothingTimeSecs,
                  false,
                  new Polynomial(
                      Map.of(
                          1., 2.,
                          2., 1.),
                      null),
                  scale),
              new ThrottlePolynomial(
                  driveJoystick,
                  2,
                  0.05,
                  smoothingTimeSecs,
                  true,
                  new Polynomial(
                      Map.of(
                          1., 2.,
                          2., 1.),
                      null),
                  scale)
            });
    var oi =
        new OIArcadeWithDPad(
            rotThrottle,
            fwdThrottle,
            0.1,
            false,
            driveJoystick,
            new Polynomial(
                Map.of(
                    0.5, 0.4,
                    0., 0.2),
                null),
            1.0,
            true);

    var updater = new Updater(List.of(pdp, drive, oi, navx));

    var defaultCommands = List.<DefaultCommand>of();
    var buttons = List.<CommandButton>of();
    var robotStartupCommands = List.<Command>of();
    var autoStartupCommands = List.<Command>of();
    var teleopStartupCommands = List.<Command>of();
    var testStartupCommands = List.<Command>of();
    var allCommands =
        new CommandContainer(
            defaultCommands,
            buttons,
            robotStartupCommands,
            autoStartupCommands,
            teleopStartupCommands,
            testStartupCommands);

    return new RobotMap(subsystems, pdp, updater, allCommands, joysticks, useCameraServer);
  }
}
