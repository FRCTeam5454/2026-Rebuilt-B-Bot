// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.Matrix;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.pathplanner.lib.config.PIDConstants;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final PPHolonomicDriveController pathPlanDriveController = new PPHolonomicDriveController(
    new PIDConstants(3.0, 0, 0.25), // Translation constants 
    new PIDConstants(25.0, 0, 1) // Rotation constants
  );
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }
    public static final Matrix<N3, N1> kPoseEstimatorStandardDeviations = VecBuilder.fill(0.1, 0.1, 10);
    public static final Matrix<N3, N1> kVisionStandardDeviations = VecBuilder.fill(5, 5, 500);
    
  public static final class ShooterConstants {
    public static final int LowerShooterMotorPort = 1;
    public static final int UpperShooterMotorPort = 2;
    public static final int AnotherShooterMotorPort = 3;

    public static final double ShooterSpeed = 0.5;
  }
  public static final class DriveConstants{
    // Joystick axis deadband for the swerve drive
    public static final double swerveDeadband = 0.1;
    //Rotate Joystick axis deadband - bigger deadband to avoid rotational drift
    public static final double swerveRotateDeadband = 0.17; //0.17
    public static final double MinGasPedalSpeed=0.20;
    //Support    6328 DriveConstants Class
    public static final double trackWidthX = edu.wpi.first.math.util.Units.inchesToMeters(27.5);
    public static final double  trackWidthY= edu.wpi.first.math.util.Units.inchesToMeters(27.5);
 
    public static final Translation2d[] moduleTranslations = {
    new Translation2d(trackWidthX / 2, trackWidthY / 2),
    new Translation2d(trackWidthX / 2, -trackWidthY / 2),
    new Translation2d(-trackWidthX / 2, trackWidthY / 2),
    new Translation2d(-trackWidthX / 2, -trackWidthY / 2)
    };

  };
  

}
