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
   final ObsidianCANSparkMax m_kicker = new ObsidianCANSparkMax(Constants.ShooterConstants.LowerShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, false, 0, false);
   final ObsidianCANSparkMax m_rightLeader = new ObsidianCANSparkMax(Constants.ShooterConstants.UpperShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, false, 0, false);
   final ObsidianCANSparkMax m_leftFollower = new ObsidianCANSparkMax(Constants.ShooterConstants.AnotherShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, true, Constants.ShooterConstants.UpperShooterMotorPort, true);
  
  /** Creates a new ShootingSubsystem. */

  public void runShooter(double speed){
    // Because the other motors are following this one, you only need to command the leader
    m_rightLeader.set(speed);
    m_kicker.set(0.5);
  }

  public void stopShooter(){
    // Stopping the leader automatically stops the followers
    m_rightLeader.set(0);
    
  }
  

  @Override
  public void periodic() {
 // m_rightFollower.set(m_rightLeader.get()); 

    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}