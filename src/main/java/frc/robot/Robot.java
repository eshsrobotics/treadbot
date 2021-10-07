// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically it contains the code
 * necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private Joystick m_leftStick;
  private Joystick m_rightStick;
  private final int FRONT_LEFT_MOTOR_PORT = 2;
  private final int BACK_LEFT_MOTOR_PORT = 2;
  private final int FRONT_RIGHT_MOTOR_PORT = 2;
  private final int BACK_RIGHT_MOTOR_PORT = 2;
  private SpeedController frontLeft = new PWMSparkMax(FRONT_LEFT_MOTOR_PORT);
  private SpeedController backLeft = new PWMSparkMax(BACK_LEFT_MOTOR_PORT);
  private SpeedController frontRight = new PWMSparkMax(FRONT_RIGHT_MOTOR_PORT);
  private SpeedController backRight = new PWMSparkMax(BACK_RIGHT_MOTOR_PORT);
  private SpeedControllerGroup leftGroup = new SpeedControllerGroup(frontLeft, backLeft);
  private SpeedControllerGroup rightGroup = new SpeedControllerGroup(frontRight, backRight);



  @Override
  public void robotInit() {
    m_myRobot = new DifferentialDrive(leftGroup, rightGroup);
    m_leftStick = new Joystick(0);
    m_rightStick = new Joystick(1);
  }

  @Override
  public void teleopPeriodic() {
    m_myRobot.tankDrive(m_leftStick.getY(), m_rightStick.getY(), true);
  }
}
