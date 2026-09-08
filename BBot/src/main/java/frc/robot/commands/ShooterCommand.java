// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ShootingSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** Spins the flywheel to a target RPM (closed loop) and only runs the kicker once it is up to speed. */
public class ShooterCommand extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final ShootingSubsystem m_subsystem;
  private final double m_rpm;
  private final double m_kickerspeed;

  /**
   * Creates a new ShooterCommand.
   *
   * @param subsystem The subsystem used by this command.
   * @param rpm The closed-loop flywheel velocity setpoint (RPM).
   * @param kickerspeed The kicker percent output to use once the flywheel is at speed.
   */
  public ShooterCommand(ShootingSubsystem subsystem, double rpm, double kickerspeed) {
    m_subsystem = subsystem;
    m_rpm = rpm;
    m_kickerspeed = kickerspeed;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // Start spinning up; hold the kicker until the flywheel is at speed.
    m_subsystem.runShooterRPM(m_rpm, 0.0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double kicker = m_subsystem.isAtTargetRPM() ? m_kickerspeed : 0.0;
    System.out.println("Shooter Speed- " + m_subsystem.getShooterRPM() + "  Target RPM:" + m_rpm);
    m_subsystem.runShooterRPM(m_rpm, kicker);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_subsystem.stopShooter();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
