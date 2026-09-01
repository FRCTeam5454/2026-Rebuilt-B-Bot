// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.utilities.ObsidianCANSparkMax;

public class ShootingSubsystem extends SubsystemBase {
   final ObsidianCANSparkMax m_kicker = new ObsidianCANSparkMax(Constants.ShooterConstants.LowerShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, Constants.ShooterConstants.kCurrentLimit);
   final ObsidianCANSparkMax m_rightLeader = new ObsidianCANSparkMax(Constants.ShooterConstants.UpperShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, Constants.ShooterConstants.kCurrentLimit);
   final ObsidianCANSparkMax m_leftFollower = new ObsidianCANSparkMax(Constants.ShooterConstants.AnotherShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, Constants.ShooterConstants.kCurrentLimit, true, Constants.ShooterConstants.UpperShooterMotorPort, true);
  
  /** Creates a new ShootingSubsystem. */

  public void runShooter(double speed, double kickerspeed) {
    // Because the other motors are following this one, you only need to command the leader
    m_rightLeader.set(speed);
    m_kicker.set(kickerspeed);
  }

  public void stopShooter(){
    // Stopping the leader automatically stops the followers
    m_rightLeader.stopMotor();
    m_kicker.stopMotor();
    
  }
}