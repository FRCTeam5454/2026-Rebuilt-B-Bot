// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShootingSubsystem;

/** An example command that uses an example subsystem. */
public class IntakeCommand extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final IntakeSubsystem m_subsystem;
  private final ShootingSubsystem m_shooter;
  private final double m_speed;
  private final double m_kickerspeed;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public IntakeCommand(IntakeSubsystem subsystem, ShootingSubsystem shooter, double speed, double kickerspeed){
    m_subsystem = subsystem;
    m_shooter = shooter;
    m_speed=speed;
    m_kickerspeed=kickerspeed;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
    addRequirements(shooter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}
   
  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
       m_subsystem.runIntake(m_speed);
       m_shooter.runKicker(m_kickerspeed);
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_subsystem.intakeMotorStop();
    m_shooter.stopKicker();;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
