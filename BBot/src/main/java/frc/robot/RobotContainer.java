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
import frc.robot.subsystems.CandleSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
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
import edu.wpi.first.wpilibj2.command.InstantCommand;
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
  // CANdle LEDs. Its periodic() drives color from robot state: purple Larson while
  // disabled, solid purple in auto, alliance color in teleop.
  private final CandleSubsystem m_candle = new CandleSubsystem(Constants.LEDConstants.kCandleId,Constants.kCanivoreBus);
  

  //drive
  private final int translationAxis = XboxController.Axis.kLeftY.value;
  private final int strafeAxis = XboxController.Axis.kLeftX.value;
  private final int rotationAxis = XboxController.Axis.kRightX.value;

  private final CommandXboxController m_xBoxDriver = new CommandXboxController(InputControllers.kXboxDrive);

  private final CommandSwerveDrivetrain m_swerve = TunerConstants.createDrivetrain();

  //Setting up auto choosing and then...
  private final SendableChooser<String> m_pathChooser = new SendableChooser<>();
  private final SendableChooser<Command> m_autoChooser;

  // ...Scheduling
  private void InitialAuton() {
    //Magic Comment
    try {
      if (m_autoChooser == null) {
        SmartDashboard.putString("Asher's Cool Message:", "No Auto Selected");
      // Don't run anything if nothing's there
      }
      else {
          Command selectedAuto = m_autoChooser.getSelected();
          Command followAuto = new PathPlannerAuto(selectedAuto.getName());
          //add auto to scheduler
          CommandScheduler.getInstance().schedule(Commands.sequence(followAuto));       
      }
    } catch (Exception e) {
        SmartDashboard.putString("Asher's Cool Message:",e.getMessage());
    }
  }

  public RobotContainer() {
      configureBindings();
      resetDefaultCommand();
      configureNamedCommands();
      m_autoChooser=AutoBuilder.buildAutoChooser();
      createAutonomousCommandList();
  }

  private void configureBindings() {
    Command shootCommand = new ShooterCommand(m_shootingSubsystem, Constants.ShooterConstants.ShooterTargetRPM, Constants.ShooterConstants.KickerSpeed);
    m_xBoxDriver.rightTrigger().whileTrue(shootCommand);

   Command highIntake = new IntakeCommand(m_Intake,Constants.IntakeConstants.kIntakeHighSpeed);
   m_xBoxDriver.a().whileTrue(highIntake);

   Command outIntake = new IntakeCommand(m_Intake,Constants.IntakeConstants.kIntakeOutSpeed);
   m_xBoxDriver.x().whileTrue(outIntake);
  }

  public void configureNamedCommands() {
    //Auto Commands (AKA the stuff that robot should do on its own other than driving)
  }

  private void refreshSmartDashboard(){
    //Outputting general things we want to see on the SmartDashboard, try to make this the over-arching important stuff
    try{
    }catch(Exception e){}
  }

  private void createAutonomousCommandList(){
    try{
      SmartDashboard.putData("Auto Chooser",m_autoChooser);

    }catch(Exception e){
      SmartDashboard.putString("Asher's Cool Message:",e.getMessage());
    }
  }

  public void setLimelightThrottles(int time){
  }

  public void DisabledInit(){
  }

  public void DisabledPeriodic(){
  }

  public void AutoPeriodic(){
  }

  public void AutonMode(){
  }

  public void TeleopMode(){
  }

  public void TeleopPeriodic(){
  }

  public void AllPeriodic(){    
  }

  private void resetDefaultCommand(){
    m_swerve.setDefaultCommand(m_swerve.applyRequestDrive(m_xBoxDriver, translationAxis, strafeAxis, rotationAxis));
  }
}
