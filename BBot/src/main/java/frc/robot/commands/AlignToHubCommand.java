package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Timer;
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
  private final Timer m_noTargetTimer = new Timer();
  private boolean m_aligned = false;

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
    m_noTargetTimer.restart();
    m_aligned = false;
  }

  @Override
  public void execute() {
    if (!m_limelight.hasAprilTagTarget()) {
      m_drivetrain.drive(0, 0, 0);
      return;
    }
    m_noTargetTimer.restart();

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
    // Give up if no tag has been seen for a while so the drivetrain goes back to driver control
    if (m_noTargetTimer.hasElapsed(Constants.LimelightConstants.kHubAlignNoTargetTimeout)) {
      return true;
    }
    m_aligned = m_limelight.hasAprilTagTarget() && m_headingController.atSetpoint();
    return m_aligned;
  }

  /** True if the last run ended because the robot was aligned (not because the target was lost). */
  public boolean isAligned() {
    return m_aligned;
  }
}
