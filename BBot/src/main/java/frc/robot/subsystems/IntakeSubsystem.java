// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.utilities.ObsidianCANSparkMax;
import static frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  private final ObsidianCANSparkMax m_intakeMotor = new ObsidianCANSparkMax(IntakeConstants.kIntakeMotorID, MotorType.kBrushless,true);
  
  public IntakeSubsystem() {
  }
  
  public void runIntake(double speed) {
    m_intakeMotor.set(speed);
  }
  public void intakeMotorStop() {
    m_intakeMotor.stopMotor();
  }
  public Command intakeOnCommand(){
    return Commands.runOnce(    ()->runIntake(Constants.IntakeConstants.kIntakeHighSpeed),this);
  }
  public Command intakeOffCommand(){
    return Commands.runOnce(    ()->intakeMotorStop(),this);
  }
}
