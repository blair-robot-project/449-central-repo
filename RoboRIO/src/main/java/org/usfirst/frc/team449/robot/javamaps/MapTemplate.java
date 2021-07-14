package org.usfirst.frc.team449.robot.javamaps;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.CommandContainer;
import org.usfirst.frc.team449.robot.RobotMap;
import org.usfirst.frc.team449.robot.components.RunningLinRegComponent;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectionalWithGyro;
import org.usfirst.frc.team449.robot.generalInterfaces.doubleUnaryOperator.Polynomial;
import org.usfirst.frc.team449.robot.generalInterfaces.motors.smart.SmartMotor;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.jacksonWrappers.FeedForwardCalculators.MappedFeedForwardCalculator;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;
import org.usfirst.frc.team449.robot.jacksonWrappers.PDP;
import org.usfirst.frc.team449.robot.jacksonWrappers.SlaveSparkMax;
import org.usfirst.frc.team449.robot.javamaps.builders.PerGearSettingsBuilder;
import org.usfirst.frc.team449.robot.javamaps.builders.SmartMotorBuilder;
import org.usfirst.frc.team449.robot.javamaps.builders.ThrottlePolynomialBuilder;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;
import org.usfirst.frc.team449.robot.oi.throttles.ThrottleSum;
import org.usfirst.frc.team449.robot.oi.unidirectional.arcade.OIArcadeWithDPad;
import org.usfirst.frc.team449.robot.other.DefaultCommand;
import org.usfirst.frc.team449.robot.other.Updater;

import java.util.HashMap;
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

    var driveMasterPrototype =
        new SmartMotorBuilder()
            .type(SmartMotor.Type.SPARK)
            .enableBrakeMode(true)
            .pdp(pdp)
            .unitPerRotation(0.47877872)
            .currentLimit(50)
            .enableVoltageComp(true)
            .startingGear(Shiftable.Gear.LOW)
            .encoderCPR(256);
    var lowGear =
        new PerGearSettingsBuilder()
            .gear(Shiftable.Gear.LOW)
            .postEncoderGearing(0.0488998)
            .maxSpeed(2.3)
            .kP(0);
    var highGear =
        new PerGearSettingsBuilder()
            .gear(Shiftable.Gear.HIGH)
            .postEncoderGearing(0.12936611)
            .maxSpeed(5.2)
            .kP(0.000001);

    // TODO call .copy() to make it clearer this and rightMaster make different instances?
    var leftMaster =
        driveMasterPrototype
            .port(leftMasterPort)
            .name("left")
            .reverseOutput(true)
            .slaveSparks(
                List.of(
                    new SlaveSparkMax(leftMasterSlave1Port, false, pdp),
                    new SlaveSparkMax(leftMasterSlave2Port, false, pdp)))
            .perGearSettings(
                List.of(
                    lowGear
                        .feedForwardCalculator(new MappedFeedForwardCalculator(0.128, 5.23, 0.0698))
                        .build(),
                    highGear
                        .feedForwardCalculator(new MappedFeedForwardCalculator(0.156, 2.01, 0.154))
                        .build()))
            .build();
    var rightMaster =
        driveMasterPrototype
            .name("right")
            .port(rightMasterPort)
            .reverseOutput(false)
            .slaveSparks(
                List.of(
                    new SlaveSparkMax(rightMasterSlave1Port, false, pdp),
                    new SlaveSparkMax(rightMasterSlave2Port, false, pdp)))
            .perGearSettings(
                List.of(
                    lowGear
                        .feedForwardCalculator(new MappedFeedForwardCalculator(0.139, 5.17, 0.0554))
                        .build(),
                    highGear
                        .feedForwardCalculator(new MappedFeedForwardCalculator(0.165, 2.01, 0.155))
                        .build()))
            .build();
    var drive = new DriveUnidirectionalWithGyro(leftMaster, rightMaster, navx, 0.61755);

    var subsystems = List.<Subsystem>of(drive);

    var throttlePrototype =
        new ThrottlePolynomialBuilder().stick(driveJoystick).smoothingTimeSecs(0.04).scale(0.7);
    var rotThrottle =
        throttlePrototype
            .axis(0)
            .deadband(0.08)
            .inverted(false)
            .polynomial(
                new Polynomial(
                    // We can't use just Map.of because we a mutable Map is needed
                    new HashMap<>(Map.of(1., 0.5)), null))
            .build();
    var fwdThrottle =
        new ThrottleSum(
            new Throttle[] {
              throttlePrototype
                  .axis(3)
                  .deadband(0.05)
                  .inverted(false)
                  .polynomial(
                      new Polynomial(
                          new HashMap<>(
                              Map.of(
                                  1., 2.,
                                  2., 1.)),
                          null))
                  .build(),
              throttlePrototype
                  .axis(2)
                  .inverted(true)
                  .build()
            });
    var oi =
        new OIArcadeWithDPad(
            rotThrottle,
            fwdThrottle,
            0.1,
            false,
            driveJoystick,
            new Polynomial(
                new HashMap<>(
                    Map.of(
                        0.5, 0.4,
                        0., 0.2)),
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
