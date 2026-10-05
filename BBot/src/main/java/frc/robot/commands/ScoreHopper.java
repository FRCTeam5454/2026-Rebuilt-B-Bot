// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShootingSubsystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;

/**
 * Same shooting logic as {@link ShooterCommand} (spin the flywheel to a target RPM, run the
 * intake, and only run the kicker once the flywheel is up to speed), but ends on its own after a
 * set amount of time. Intended for autonomous.
 */
public class ScoreHopper extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final ShootingSubsystem m_subsystem;
  private final IntakeSubsystem m_intake;
  private final Timer m_timer = new Timer();
  private double m_rpm;
  private double m_oldrpm=0;
  private double m_oldkickerspeed=0;
  private double m_kickerspeed;
  private double m_shootTimeSeconds;

  /**
   * Creates a new ScoreHopper.
   *
   * @param subsystem The shooter subsystem used by this command.
   * @param intake The intake subsystem used by this command.
   * @param rpm The closed-loop flywheel velocity setpoint (RPM).
   * @param shootTimeSeconds How long to run before the command ends, in seconds (includes spin-up).
   */
  public ScoreHopper(ShootingSubsystem subsystem, IntakeSubsystem intake, double rpm, double shootTimeSeconds) {
    m_subsystem = subsystem;
    m_intake = intake;
    m_rpm = rpm;
    m_shootTimeSeconds = shootTimeSeconds;
    m_kickerspeed = Constants.ShooterConstants.KickerSpeed;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_oldrpm=0;
    m_oldkickerspeed=0;
    m_timer.restart();
    // Start spinning up; hold the kicker until the flywheel is at speed.
    m_subsystem.runShooterRPM(m_rpm, 0.0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_intake.runIntake(Constants.IntakeConstants.kIntakeHighSpeed);
    double kicker = m_subsystem.isAtTargetRPM() ? m_kickerspeed : 0.0;
    System.out.println("Kicker Speeed" + kicker+ " Shooter Speed- " + m_subsystem.getShooterRPM() + "  Target RPM:" + m_rpm);
    // Re-send whenever the kicker output changes, so the kicker pauses while the flywheel
    // recovers below tolerance after a shot and resumes once it's back at speed.
    if((m_rpm!=m_oldrpm) || (kicker!=m_oldkickerspeed)) {
      m_subsystem.runShooterRPM(m_rpm, kicker);
    }
    m_oldrpm=m_rpm;
    m_oldkickerspeed=kicker;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_timer.stop();
    m_subsystem.stopShooter();
    m_intake.intakeMotorStop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_timer.hasElapsed(m_shootTimeSeconds);
  }
}
