// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.DigitalInput;



public class DriveSubsystem extends SubsystemBase {
  private final WPI_VictorSPX m_leftLeader = new WPI_VictorSPX(Constants.kLeftMotor1Port);
  private final WPI_VictorSPX m_leftFollower = new WPI_VictorSPX(Constants.kLeftMotor2Port);
  private final WPI_VictorSPX m_rightLeader = new WPI_VictorSPX(Constants.kRightMotor1Port);
  private final WPI_VictorSPX m_rightFollower = new WPI_VictorSPX(Constants.kRightMotor2Port);
  private final DigitalInput m_limitSwitch = new DigitalInput(0);
  private final DifferentialDrive m_drive;
  
  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    // The motors on the left side of the drive.
    m_leftFollower.follow(m_leftLeader);
    m_rightFollower.follow(m_rightLeader);
  // The motors on the right side of the drive.
    m_rightLeader.setInverted(true);
    m_rightFollower.setInverted(true);
    m_drive = new DifferentialDrive(m_leftLeader, m_rightLeader);
  }

  public void tankDrive(double LeftSpeed, double RightSpeed){
  m_drive.tankDrive(LeftSpeed, RightSpeed);
  }
  /**
   * Example command factory method.
   *
   * @return a command
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }

  public boolean isLimitPressed() {
    return !m_limitSwitch.get();
  }
  
  @Override
  public void periodic() {
    System.out.println(isLimitPressed());  
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
  

}
