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
   final ObsidianCANSparkMax m_rightLeader = new ObsidianCANSparkMax(Constants.ShooterConstants.LowerShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false);
   final ObsidianCANSparkMax m_rightFollower = new ObsidianCANSparkMax(Constants.ShooterConstants.UpperShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false);
   final ObsidianCANSparkMax m_leftFollower = new ObsidianCANSparkMax(Constants.ShooterConstants.AnotherShooterMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false);
  
  /** Creates a new ShootingSubsystem. */
  public ShootingSubsystem() {
    
  }

  public void setShooterSpeed(double speed){
    m_rightLeader.set(speed);
    m_leftFollower.set(-speed);
  }

  public void stopShooter(){
    m_rightLeader.set(0);
    m_leftFollower.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
