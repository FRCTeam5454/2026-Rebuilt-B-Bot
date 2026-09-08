// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.PersistMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.utilities.ObsidianCANSparkMax;

public class ShootingSubsystem extends SubsystemBase {
   final ObsidianCANSparkMax m_kicker = new ObsidianCANSparkMax(Constants.ShooterConstants.KickerMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, Constants.ShooterConstants.kCurrentLimit);
   final ObsidianCANSparkMax m_rightLeader = new ObsidianCANSparkMax(Constants.ShooterConstants.ShooterLeaderMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, Constants.ShooterConstants.kCurrentLimit);
   final ObsidianCANSparkMax m_leftFollower = new ObsidianCANSparkMax(Constants.ShooterConstants.ShooterFollowerMotorPort, ObsidianCANSparkMax.MotorType.kBrushless, false, Constants.ShooterConstants.kCurrentLimit, true, Constants.ShooterConstants.ShooterLeaderMotorPort, true);

  // Closed-loop velocity control lives on the leader; the follower mirrors its
  // output, so commanding the leader spins both flywheel motors.
  private final SparkClosedLoopController m_shooterPID = m_rightLeader.getClosedLoopController();
  private final RelativeEncoder m_shooterEncoder = m_rightLeader.getEncoder();

  /** Last commanded closed-loop setpoint (RPM). 0 means open-loop / stopped. */
  private double m_targetRPM = 0.0;

  /** Creates a new ShootingSubsystem. */
  @SuppressWarnings("removal") // velocityFF is the documented shooter FF recipe; still valid in REVLib 2026
  public ShootingSubsystem() {
    // Layer the velocity PIDF gains onto the leader without disturbing the
    // current-limit and status-frame config the wrapper already applied
    // (kNoResetSafeParameters keeps those in place).
    SparkMaxConfig closedLoopConfig = new SparkMaxConfig();
    closedLoopConfig.closedLoop
        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        .pid(
            Constants.ShooterConstants.kShooterP,
            Constants.ShooterConstants.kShooterI,
            Constants.ShooterConstants.kShooterD,
            ClosedLoopSlot.kSlot0)
        .velocityFF(Constants.ShooterConstants.kShooterFF, ClosedLoopSlot.kSlot0)
        .outputRange(-1.0, 1.0, ClosedLoopSlot.kSlot0);
    m_rightLeader.configure(
        closedLoopConfig,
        ResetMode.kNoResetSafeParameters,
        PersistMode.kNoPersistParameters);
  }

  /**
   * Open-loop shooter control (percent output), unchanged behavior.
   *
   * @param speed leader percent output [-1, 1]
   * @param kickerspeed kicker percent output [-1, 1]
   */
  public void runShooter(double speed, double kickerspeed) {
    m_targetRPM = 0.0;
    m_rightLeader.set(speed);
    m_kicker.set(kickerspeed);
  }

  /**
   * Closed-loop velocity control of the flywheel with an open-loop kicker.
   *
   * @param rpm flywheel velocity setpoint (RPM)
   * @param kickerspeed kicker percent output [-1, 1]
   */
  public void runShooterRPM(double rpm, double kickerspeed) {
    m_targetRPM = rpm;
    m_shooterPID.setSetpoint(rpm, ControlType.kVelocity, ClosedLoopSlot.kSlot0);
    m_kicker.set(kickerspeed);
  }

  public void stopShooter() {
    m_targetRPM = 0.0;
    m_rightLeader.stopMotor();
    m_kicker.stopMotor();
  }

  /** @return current flywheel velocity (RPM) from the leader's NEO encoder */
  public double getShooterRPM() {
    return m_shooterEncoder.getVelocity();
  }

  /** @return true once the flywheel is within tolerance of the commanded RPM */
  public boolean isAtTargetRPM() {
    return m_targetRPM > 0.0
        && Math.abs(getShooterRPM() - m_targetRPM) <= Constants.ShooterConstants.ShooterRPMTolerance;
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Shooter/RPM", getShooterRPM());
    SmartDashboard.putNumber("Shooter/TargetRPM", m_targetRPM);
    SmartDashboard.putBoolean("Shooter/AtSpeed", isAtTargetRPM());
  }
}
