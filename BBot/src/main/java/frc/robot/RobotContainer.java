// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.InputControllers;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.ShooterCommand;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.ShootingSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.IntakeCommand;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ShootingSubsystem m_shootingSubsystem = new ShootingSubsystem();
  private final IntakeSubsystem m_Intake = new IntakeSubsystem();
  

  //drive...
  private final int translationAxis = XboxController.Axis.kLeftY.value;
  private final int strafeAxis = XboxController.Axis.kLeftX.value;
  private final int rotationAxis = XboxController.Axis.kRightX.value;

  private final CommandXboxController m_xBoxDriver = new CommandXboxController(InputControllers.kXboxDrive);

  private final CommandSwerveDrivetrain m_swerve = TunerConstants.createDrivetrain();

  public RobotContainer() {
    configureBindings();
    resetDefaultCommand();
  }

  private void configureBindings() {
    Command shootCommand = new ShooterCommand(m_shootingSubsystem, Constants.ShooterConstants.ShooterSpeed);
    m_xBoxDriver.rightTrigger().whileTrue(shootCommand);

   Command highIntake = new IntakeCommand(m_Intake,Constants.IntakeConstants.kIntakeHighSpeed);
   m_xBoxDriver.a().whileTrue(highIntake);

   Command lowIntake = new IntakeCommand(m_Intake,Constants.IntakeConstants.kIntakeLowSpeed);
   m_xBoxDriver.b().whileTrue(lowIntake);

   Command outIntake = new IntakeCommand(m_Intake,Constants.IntakeConstants.kIntakeOutSpeed);
   m_xBoxDriver.x().whileTrue(outIntake);

   Command runShooter = new IntakeCommand(m_Intake,Constants.IntakeConstants.kIntakeOutSpeed);
   m_xBoxDriver.y().whileTrue(runShooter);
  }


  public Command getAutonomousCommand() {
    return null;
  }

  private void resetDefaultCommand(){
    m_swerve.setDefaultCommand(m_swerve.applyRequestDrive(m_xBoxDriver, translationAxis, strafeAxis, rotationAxis));
  }
}
