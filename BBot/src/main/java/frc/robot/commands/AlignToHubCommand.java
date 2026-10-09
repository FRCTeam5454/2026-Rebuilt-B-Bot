package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.utilities.Limelight;

public class AlignToHubCommand extends Command {
  private final CommandSwerveDrivetrain m_drivetrain;
  private final Limelight m_limelight;
  private final PIDController m_headingController = new PIDController(
      Constants.LimelightConstants.kHubAlignP,
      0,
      Constants.LimelightConstants.kHubAlignD);

  public AlignToHubCommand(
      CommandSwerveDrivetrain drivetrain,
      Limelight limelight) {
    m_drivetrain = drivetrain;
    m_limelight = limelight;
    m_headingController.setTolerance(Constants.LimelightConstants.kHubAlignToleranceDegrees);
    addRequirements(drivetrain);
  }

  @Override
  public void initialize() {
    m_headingController.reset();
  }

  @Override
  public void execute() {
    if (!m_limelight.hasAprilTagTarget()) {
      m_drivetrain.drive(0, 0, 0);
      return;
    }

    double rotation = m_headingController.calculate(
        m_limelight.getAprilTagHorizontalOffset(), 0);
    rotation = MathUtil.clamp(
        rotation,
        -Constants.LimelightConstants.kHubAlignMaxRotation,
        Constants.LimelightConstants.kHubAlignMaxRotation);
    m_drivetrain.drive(0, 0, rotation);
  }

  @Override
  public void end(boolean interrupted) {
    m_drivetrain.drive(0, 0, 0);
  }

  @Override
  public boolean isFinished() {
    return m_limelight.hasAprilTagTarget() && m_headingController.atSetpoint();
  }
}
