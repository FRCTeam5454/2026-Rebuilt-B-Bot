// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.InputControllers;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.ScoreHopper;
import frc.robot.commands.ShooterCommand;
import frc.robot.commands.AlignToHubCommand;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.ShootingSubsystem;
import frc.robot.utilities.Limelight;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.CandleSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import java.util.List;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
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
  private final Limelight m_Limelight = new Limelight(Constants.LimelightConstants.kheight,Constants.LimelightConstants.kMountingAngle,
                            Constants.LimelightConstants.xOffset,Constants.LimelightConstants.limelightName);


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
    try {
      Command selectedAuto = m_autoChooser.getSelected();
      if (selectedAuto == null) {
        SmartDashboard.putString("Asher's Cool Message:", "No Auto Selected");
      }
      else {
        CommandScheduler.getInstance().schedule(selectedAuto);
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
    // Left trigger: fixed-speed shot
    Command shootCommand = new ShooterCommand(m_shootingSubsystem, m_Intake,Constants.ShooterConstants.ShooterTargetRPM, Constants.ShooterConstants.KickerSpeed);
    m_xBoxDriver.leftTrigger().whileTrue(shootCommand);

    // right trigger: distance-based shot - RPM from the Limelight distance lookup table
    Command lookupShotCommand = new ShooterCommand(m_shootingSubsystem, m_Intake, this::getLookupShotRPM, Constants.ShooterConstants.KickerSpeed);
    m_xBoxDriver.rightTrigger().whileTrue(lookupShotCommand);

    m_xBoxDriver.rightBumper().whileTrue(new AlignToHubCommand(m_swerve, m_Limelight));

   Command highIntake = new IntakeCommand(m_Intake,m_shootingSubsystem,Constants.IntakeConstants.kIntakeHighSpeed,Constants.ShooterConstants.KickerIntakeSpeed);
   
   m_xBoxDriver.a().whileTrue(highIntake);

   Command outIntake = new IntakeCommand(m_Intake,m_shootingSubsystem, Constants.IntakeConstants.kIntakeOutSpeed, Constants.ShooterConstants.KickerSpeed);
   m_xBoxDriver.x().whileTrue(outIntake);
  }

  // Last valid Limelight distance for the lookup shot, and when it was seen
  private double m_lastShotDistance = 0;
  private double m_lastShotDistanceTime = Double.NEGATIVE_INFINITY;

  /**
   * Flywheel RPM for the left-trigger lookup shot. Uses the live Limelight distance; if the
   * target drops out briefly mid-shot it keeps the last distance for kShotDistanceHoldSeconds,
   * then falls back to the fixed ShooterTargetRPM.
   */
  private double getLookupShotRPM() {
    double now = Timer.getFPGATimestamp();
    if (m_Limelight.isAnyTargetAvailable()) {
      double distance = m_Limelight.getDistance();
      if (distance > 0) {
        m_lastShotDistance = distance;
        m_lastShotDistanceTime = now;
      }
    }
    double rpm = (now - m_lastShotDistanceTime <= Constants.ShooterConstants.kShotDistanceHoldSeconds)
        ? ShootingSubsystem.getRPMForDistance(m_lastShotDistance)
        : Constants.ShooterConstants.ShooterTargetRPM;
    SmartDashboard.putNumber("Lookup Shot RPM", rpm);
    return rpm;
  }

  public void configureNamedCommands() {
    //Auto Commands (AKA the stuff that robot should do on its own other than driving)
    // Must be registered before AutoBuilder.buildAutoChooser() so the autos can find them.
    // Names must match the Named Command names used in the PathPlanner GUI exactly.
    NamedCommands.registerCommand("ShootQuick",
        new ScoreHopper(m_shootingSubsystem, m_Intake,
            Constants.ShooterConstants.ShooterTargetRPM, Constants.ShooterConstants.kShootQuickSeconds));
    NamedCommands.registerCommand("ShootLong",
        new ScoreHopper(m_shootingSubsystem, m_Intake,
            Constants.ShooterConstants.ShooterTargetRPM, Constants.ShooterConstants.kShootLongSeconds));
    // Instant commands: IntakeOn starts the intake and finishes immediately, leaving it running
    // while the auto continues (e.g. driving a path); IntakeOff stops it.
    NamedCommands.registerCommand("IntakeOn", m_Intake.intakeOnCommand());
    NamedCommands.registerCommand("IntakeOff", m_Intake.intakeOffCommand());
  }

  public void refreshSmartDashboard(){
    //Outputting general things we want to see on the SmartDashboard, try to make this the over-arching important stuff
    try{
      double distance=m_Limelight.getDistance();
      boolean inShotRange=false;
      if ((distance < Constants.ShooterConstants.kDistanceHigh) && (distance > Constants.ShooterConstants.kDistanceLow)) {
        inShotRange=true;
      }
      SmartDashboard.putBoolean("In Shot Range",inShotRange);
      SmartDashboard.putNumber("DistancetoHub",distance);
    }catch(Exception e){}
  }

  private void createAutonomousCommandList(){
    try{
      // Every .auto in deploy/pathplanner/autos is listed (default "None").
      // Shows in Shuffleboard's SmartDashboard tab / Elastic, and on its own "Autonomous" tab.
      SmartDashboard.putData("Auto Chooser",m_autoChooser);
      Shuffleboard.getTab("Autonomous")
          .add("Auto Chooser", m_autoChooser)
          .withWidget(BuiltInWidgets.kComboBoxChooser)
          .withSize(3, 1);

    }catch(Exception e){
      SmartDashboard.putString("Asher's Cool Message:",e.getMessage());
    }
  }

  public void setLimelightThrottles(int time){
  }

  public void DisabledInit(){
    // An auto can leave the intake on (IntakeOn). Phoenix 6 keeps re-sending a motor's last
    // request, so without this the intake would start up again when teleop enables.
    m_Intake.intakeMotorStop();
  }

  public void DisabledPeriodic(){
  }

  public void AutoPeriodic(){
  }

  public void AutonMode(){
    InitialAuton();
  }

  public void TeleopMode(){
  }

  public void TeleopPeriodic(){
  }

  public void AllPeriodic()
  {refreshSmartDashboard();}    
  

  private void resetDefaultCommand(){
    m_swerve.setDefaultCommand(m_swerve.applyRequestDrive(m_xBoxDriver, translationAxis, strafeAxis, rotationAxis));
  }
}
