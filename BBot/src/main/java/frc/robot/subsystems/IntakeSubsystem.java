// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.utilities.ObsidianCANSparkMax;
import static frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  private final ObsidianCANSparkMax m_intakeMotor = new ObsidianCANSparkMax(0, MotorType.kBrushless,true);
  
  public IntakeSubsystem() {
  }
  
  public void IntakeMotorSpeed(double speed) {
    m_intakeMotor.set(Constants.IntakeConstants.kIntakeLowSpeed);
  } 
  public void IntakeMotorStop() {
    m_intakeMotor.stopMotor();
  }
  public Command exampleMethodCommand() {
    return runOnce(
        () -> {
        });
  }
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
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
