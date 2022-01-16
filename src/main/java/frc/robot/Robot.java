// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import javax.management.loading.PrivateClassLoader;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private Joystick leftStick = null;
  private Joystick rightStick = null;
  private XboxController controller = null;
  private final int FRONT_LEFT_MOTOR_PORT = 2;
  private final int BACK_LEFT_MOTOR_PORT = 3;
  private final int FRONT_RIGHT_MOTOR_PORT = 0;
  private final int BACK_RIGHT_MOTOR_PORT = 5;
  private MotorController frontLeft = new PWMSparkMax(FRONT_LEFT_MOTOR_PORT);
  private MotorController backLeft = new PWMSparkMax(BACK_LEFT_MOTOR_PORT);
  private MotorController frontRight = new PWMSparkMax(FRONT_RIGHT_MOTOR_PORT);
  private MotorController backRight = new PWMSparkMax(BACK_RIGHT_MOTOR_PORT);
  private MotorControllerGroup leftGroup = new MotorControllerGroup(frontLeft, backLeft);
  private MotorControllerGroup rightGroup = new MotorControllerGroup(frontRight, backRight);

  private NetworkTableEntry upKey, downKey, leftKey, rightKey = null; // Arrow keys
  private NetworkTableEntry wKey, aKey, sKey, dKey = null;            // WASD

  // hard coded ports for your human input devices
  // unfortunatly we do not have a method for auto-discovery yet, 
  //so you will have to change this to match your setup 
  private final int LEFT_JOYSTICK_PORT = 0;
  private final int RIGHT_JOYSTICK_PORT = 1;
  private final int XBOX_CONTROLLER_PORT = 2;
  
  /**
   * A simple enumeration to represent the side of the controller that we're trying to get.
   */
  private enum Hand {
    LEFT,
    RIGHT
  }

  /**
   * Returns the vertical axis values for each of the joysticks on the Xbox controller.
   * @param controller The Xbox controller object.
   * @param side The side of the joystick one's preferences are for.
   * @return
   */
  private double getControllerYAxisValue(XboxController controller, Hand side) {

    switch (controller.getName()) {
      case "Logitech Dual Action":
      default:
      if (side == Hand.LEFT) {
          return controller.getLeftY();
      } else {
        // This reverses the direction of the right motors.
        // return -controller.getRawAxis(3);
        return -controller.getRightY();
      }
      //default:
      //  return controller.getY(side);
    }
  }

  @Override
  public void robotInit() {
    m_myRobot = new DifferentialDrive(leftGroup, rightGroup);
  }


  /**
   * Initialize our input devices every time the robot is enabled, just to make it clear to the driver what's going on.
   */
  @Override
  public void teleopInit() {

    // If there is no controller plugged in, code will be angry 
    // so make sure you have a controller plugged in when starting the robot
    try {
      System.err.printf("Attempting to initialize dual joysticks.\n");
      leftStick = new Joystick(LEFT_JOYSTICK_PORT);
      rightStick = new Joystick(RIGHT_JOYSTICK_PORT);
      if (leftStick.isConnected() && rightStick.isConnected()) {
        System.out.printf("** Dual joysticks initialized. **\n");
      } else {
        System.out.printf("At least one of the joysticks is disconnected.\n");
      }
    } catch (Exception e) {
      System.out.printf("Could not initialize dual joysticks on ports 0 and 1: %s.\n", e.getMessage());
    }

    try {
      System.out.printf("Attempting to initialize XBoxController.\n");
      controller = new XboxController(XBOX_CONTROLLER_PORT);
      if (controller.isConnected()) {
        System.out.printf("** XBoxController initialized. **\n");

        // unfortunately, control makes it here if we have the ordinary joysticks plugged in.
        // we need something else to distinguish x-box controller to the original joysticks
        System.out.printf("controller name: %s\n", controller.getName());
      } else {
        System.out.printf("XBox controller is not connected.\n");
      }      
    } catch (Exception e) {
      System.out.printf("Could not initialize XBoxController: %s.\n", e.getMessage());
    }

    System.out.printf("Attempting to initialize NetworkTables.\n");
    NetworkTableInstance networkTableInstance = NetworkTableInstance.getDefault();
    if (networkTableInstance.isConnected()) {
      NetworkTable inputTable = networkTableInstance.getTable("inputTable");
      if (inputTable != null) {
        upKey = inputTable.getEntry("Up");
        downKey = inputTable.getEntry("Down");
        leftKey = inputTable.getEntry("Left");
        rightKey = inputTable.getEntry("Right");
        wKey = inputTable.getEntry("w");
        aKey = inputTable.getEntry("a");
        sKey = inputTable.getEntry("s");
        dKey = inputTable.getEntry("d");
        System.out.printf("** NetworkTables initialized. **\n");
      } else {
        System.out.printf("Could not get reference to 'inputTable' network table.\n");  
      }
    } else {
      System.out.printf("NetworkTables are not connected.");
    }

    if ((upKey == null      || !NetworkTableInstance.getDefault().isConnected()) && 
        (controller == null || !controller.isConnected()) && 
        (leftStick == null  || !leftStick.isConnected())  &&
        (rightStick == null || !rightStick.isConnected())) {
      System.out.printf("ERROR: No input methods succeeded.  Your robot cannot respond to human input!\n");  
    }
    super.teleopInit();
  }

  // Returns one of eight possible direction vectors (0, if you count the zero
  // vector) depending on which of the directional keys you press.
  private Translation2d getInputVectorFromKeyboard() {
    double resultX = 0, resultY = 0;

    // Vertical component.
    if (wKey.getBoolean(false) || upKey.getBoolean(false)) {
      resultY += 1.0;
    }
    if (sKey.getBoolean(false) || downKey.getBoolean(false)) {
      resultY -= 1.0;
    }

    // Horizontal component.
    if (aKey.getBoolean(false) || leftKey.getBoolean(false)) {
      resultX -= 1.0;
    } 
    if (dKey.getBoolean(false) || rightKey.getBoolean(false)) {
      resultX += 1.0;
    }
    return new Translation2d(resultX, resultY);
  }

  @Override
  public void teleopPeriodic() {
    double left = 0, right = 0;

    if (leftStick.isConnected() && rightStick.isConnected()) {
      // Get the input vectors from both joysticks.
      //
      // Note that the motors are oriented such that one side has to be 
      // reversed from the sense of the other.  This code is written so that
      // pushing both joysticks forward drives the robot forward, too.
      left += -leftStick.getY();
      right += -rightStick.getY();
      System.out.printf("Joysticks: L = %.2f, R = %.2f \n", leftStick.getY(), rightStick.getY());      
    }
    
    if (controller.isConnected()) {
      // TODO: This seems to trigger even if there's no XBox controller connected.  We may need to test for known controller names.
      // Disabled until those tests can be done.
      //
      // Get the input vectors from both of the controller's joysticks.
      left += -controller.getLeftY();
      right += -controller.getRightY();
    }
    
    if (upKey != null) {
      // Get the input vectors from the keyboard using an arcade drive scheme.
      Translation2d inputVector = getInputVectorFromKeyboard();      
      final double speed = inputVector.getY();
      final double rotation = inputVector.getX();

      if (speed != 0 || rotation != 0) {
        final double tankLeftRaw = speed + rotation;
        final double tankRightRaw = speed - rotation;
  
        if (tankLeftRaw != 0 || tankRightRaw != 0) {
          final double tankLeftNormalized = tankLeftRaw   / Math.max(Math.abs(tankLeftRaw), Math.abs(tankRightRaw));
          final double tankRightNormalized = tankRightRaw / Math.max(Math.abs(tankLeftRaw), Math.abs(tankRightRaw));
          left += tankLeftNormalized;
          right += tankRightNormalized;
        }
        System.out.printf("Keys: V = (%.2f, %.2f); L = %.2f, R = %.2f \n", inputVector.getX(), inputVector.getY(), left, right);
      }
    }
    System.out.printf("Final: L = %.2f, R = %.2f \n", left, right);      
    m_myRobot.tankDrive(left, -right, false);
  }
}
