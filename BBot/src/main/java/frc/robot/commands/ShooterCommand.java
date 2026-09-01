// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ShootingSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class ShooterCommand extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final ShootingSubsystem m_subsystem;
  private double m_speed;
  private double m_kickerspeed;
  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   * @param speed The speed to set the shooter to.
   * @param kickerspeed
   */
  public ShooterCommand(ShootingSubsystem subsystem, double speed, double kickerspeed) {
    m_subsystem = subsystem;
    m_speed = speed;
    m_kickerspeed = kickerspeed;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }


  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_subsystem.runShooter(m_speed, m_kickerspeed);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
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
