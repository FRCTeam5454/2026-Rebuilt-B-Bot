// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShootingSubsystem;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
/** Spins the flywheel to a target RPM (closed loop) and only runs the kicker once it is up to speed. */
public class ShooterCommand extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final ShootingSubsystem m_subsystem;
  private final IntakeSubsystem m_intake;
  private final DoubleSupplier m_rpmSupplier;
  private double m_rpm;
  private double m_oldrpm=0;
  private double m_oldkickerspeed=0;
  private double m_kickerspeed;

  /**
   * Creates a new ShooterCommand.
   *
   * @param subsystem The subsystem used by this command.
   * @param rpm The closed-loop flywheel velocity setpoint (RPM).
   * @param kickerspeed The kicker percent output to use once the flywheel is at speed.
   */
  public ShooterCommand(ShootingSubsystem subsystem, IntakeSubsystem intake,double rpm, double kickerspeed) {
    this(subsystem, intake, () -> rpm, kickerspeed);
  }

  /**
   * Creates a new ShooterCommand whose RPM is re-read every loop (e.g. a distance lookup).
   *
   * @param subsystem The subsystem used by this command.
   * @param rpmSupplier Supplies the closed-loop flywheel velocity setpoint (RPM) each loop.
   * @param kickerspeed The kicker percent output to use once the flywheel is at speed.
   */
  public ShooterCommand(ShootingSubsystem subsystem, IntakeSubsystem intake, DoubleSupplier rpmSupplier, double kickerspeed) {
    m_subsystem = subsystem;
    m_intake = intake;
    m_rpmSupplier = rpmSupplier;
    m_kickerspeed = kickerspeed;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // Forget the last run's values so the first execute() always sends a fresh command.
    m_oldrpm=0;
    m_oldkickerspeed=0;
    m_rpm = m_rpmSupplier.getAsDouble();
    // Start spinning up; hold the kicker until the flywheel is at speed.
    m_subsystem.runShooterRPM(m_rpm, 0.0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_rpm = m_rpmSupplier.getAsDouble();
    m_intake.runIntake(Constants.IntakeConstants.kIntakeHighSpeed);
    double shooterRPM = m_subsystem.getShooterRPM();
    double kicker = m_subsystem.isAtTargetRPM() ? m_kickerspeed : 0.0;
    String rpmStatus = shooterRPM > m_rpm ? "OVER TARGET" : "UNDER TARGET";
    System.out.println("Kicker Speed: " + kicker + " Shooter RPM: " + shooterRPM
        + " Target RPM: " + m_rpm + " (" + rpmStatus + ")");
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
    m_subsystem.primeShooter();
    m_intake.intakeMotorStop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
