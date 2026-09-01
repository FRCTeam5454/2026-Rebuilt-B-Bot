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

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.wpilibj.XboxController;
import java.util.List;

import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.IntakeCommand;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, comands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  private final Field2d m_Field2d = new Field2d();

  // The robot's subsystems and commands are defined here...
  private final ShootingSubsystem m_shootingSubsystem = new ShootingSubsystem();
  private final IntakeSubsystem m_Intake = new IntakeSubsystem();
  

  //drive...
  private final int translationAxis = XboxController.Axis.kLeftY.value;
  private final int strafeAxis = XboxController.Axis.kLeftX.value;
  private final int rotationAxis = XboxController.Axis.kRightX.value;

  private final CommandXboxController m_xBoxDriver = new CommandXboxController(InputControllers.kXboxDrive);
  private final CommandSwerveDrivetrain m_swerve = TunerConstants.createDrivetrain();
  private final SendableChooser<Command> m_autoChooser;

  private void InitialAutonPathfind(){
    //Magic Comment
    if (m_autoChooser == null) {
      SmartDashboard.putString("Asher's Cool Message:", "No Auto Selected");
     // Don't Run anything
    }
    else {
        // Prefer the pre-built Command selected in the auto chooser
        Command selectedAuto = m_autoChooser.getSelected();
          Command followAuto = new PathPlannerAuto(selectedAuto.getName());

          // go to start pos then call auto
          SmartDashboard.putString("Asher's Cool Message:","should be running sequence");
          //add auto to scheduler
          Pose2d currentPose = m_swerve.getPose2d();
          CommandScheduler.getInstance().schedule(Commands.sequence(followAuto));       
    }
  }

  public RobotContainer() {
    configureBindings();
    resetDefaultCommand();
    m_autoChooser=AutoBuilder.buildAutoChooser();
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

 private void createAutonomousCommandList(){
    try{
      SmartDashboard.putData("Auto Chooser",m_autoChooser);

    }catch(Exception e){
      //System.out.println("Create Autos Failed, Exception: " + e.getMessage());
    }
  }
}
