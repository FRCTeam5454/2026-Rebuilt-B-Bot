// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.EmptyAnimation;
import com.ctre.phoenix6.controls.FireAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.RgbFadeAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.controls.StrobeAnimation;
import com.ctre.phoenix6.controls.TwinkleAnimation;
import com.ctre.phoenix6.controls.TwinkleOffAnimation;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.LarsonBounceValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import com.ctre.phoenix6.signals.StripTypeValue;
import com.ctre.phoenix6.signals.VBatOutputModeValue;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * Wraps a CTRE CANdle (Phoenix 6) LED controller.
 *
 * <p>Public API:
 * <ul>
 *   <li>{@link #setColor} - solid color, either from the {@link CandleColor} palette or raw RGB.</li>
 *   <li>The animation helpers ({@link #rainbow()}, {@link #larson}, {@link #colorFlow}, {@link #fire()},
 *       {@link #rgbFade()}, {@link #singleFade}, {@link #strobe}, {@link #twinkle}, {@link #twinkleOff})
 *       start the matching standard CANdle animation.</li>
 *   <li>{@link #clearAnimation()} stops the running animation.</li>
 * </ul>
 *
 * <p>While {@link #isAutoStateEnabled()} is true (the default) {@link #periodic()} drives the LEDs
 * from the robot state: purple Larson while disabled, solid purple in autonomous, and the alliance
 * color during teleop. Call {@link #setAutoStateEnabled(boolean)} with {@code false} to take manual
 * control from a command, and {@code true} to hand it back.
 */
public class CandleSubsystem extends SubsystemBase {
   private final CANdle m_candle;


  /** Named colors supported by {@link #setColor(CandleColor)}. RGB, 0 - 255 per channel. */
  public enum CandleColor {
    OFF(0, 0, 0),
    WHITE(255, 255, 255),
    RED(255, 0, 0),
    ORANGE(255, 80, 0),
    YELLOW(255, 150, 0),
    GREEN(0, 255, 0),
    CYAN(0, 255, 255),
    BLUE(0, 0, 255),
    PURPLE(128, 0, 128),
    PINK(255, 20, 90),
    MAGENTA(255, 0, 255);

    public final int r;
    public final int g;
    public final int b;

    CandleColor(int r, int g, int b) {
      this.r = r;
      this.g = g;
      this.b = b;
    }

    public RGBWColor toRGBW() {
      return new RGBWColor(r, g, b);
    }
  }

  /** Robot-state driven LED presentation. */
  private enum LedMode {
    DISABLED,
    AUTONOMOUS,
    TELEOP_RED,
    TELEOP_BLUE,
    TELEOP_NEUTRAL
  }

  /** Animation slot (0 - 7) used for every animation this class starts. */
  private static final int kAnimSlot = 0;
  private final int m_startIdx = 0;
  private final int m_endIdx = Math.max(0, Constants.LEDConstants.kLedCount - 1);

  // Re-used control requests (Phoenix 6 recommends caching these rather than
  // allocating one per loop).
  private final SolidColor m_solid = new SolidColor(m_startIdx, m_endIdx);
  private final EmptyAnimation m_clear = new EmptyAnimation(kAnimSlot);
  private final RainbowAnimation m_rainbow = new RainbowAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);
  private final LarsonAnimation m_larson =
      new LarsonAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot).withBounceMode(LarsonBounceValue.Front).withSize(3);
  private final ColorFlowAnimation m_colorFlow = new ColorFlowAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);
  private final FireAnimation m_fire = new FireAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);
  private final RgbFadeAnimation m_rgbFade = new RgbFadeAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);
  private final SingleFadeAnimation m_singleFade = new SingleFadeAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);
  private final StrobeAnimation m_strobe = new StrobeAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);
  private final TwinkleAnimation m_twinkle = new TwinkleAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);
  private final TwinkleOffAnimation m_twinkleOff = new TwinkleOffAnimation(m_startIdx, m_endIdx).withSlot(kAnimSlot);

  private boolean m_autoStateEnabled = true;
  private LedMode m_appliedMode = null;

  public CandleSubsystem(int candleID) {
   
    m_candle = new CANdle(candleID);
   
    CANdleConfiguration config = new CANdleConfiguration();
    config.LED.StripType = StripTypeValue.GRB; // change to match your LED strip wiring
    config.LED.BrightnessScalar = Constants.LEDConstants.kBrightness;
    config.CANdleFeatures.VBatOutputMode = VBatOutputModeValue.Modulated;
    config.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Disabled;

    m_candle.getConfigurator().apply(config);
  }

  // ---------------------------------------------------------------------------
  // Solid color
  // ---------------------------------------------------------------------------

  /** Sets every LED to a raw RGB value, clearing any running animation. */
  public void setColor(int r, int g, int b) {
    clearAnimation();
    m_candle.setControl(m_solid.withColor(new RGBWColor(r, g, b)));
  }

  /** Sets every LED to one of the supported {@link CandleColor}s, clearing any running animation. */
  public void setColor(CandleColor color) {
    setColor(color.r, color.g, color.b);
  }

  /** Turns the LEDs off. */
  public void off() {
    setColor(CandleColor.OFF);
  }

  /** Stops the animation running in this class's slot. */
  public void clearAnimation() {
    m_candle.setControl(m_clear);
  }

  // ---------------------------------------------------------------------------
  // Standard CANdle animations
  // ---------------------------------------------------------------------------

  /** Rolling rainbow across the whole strip. */
  public void rainbow() {
    m_candle.setControl(m_rainbow);
  }

  /** "Cylon" / Larson scanner in the given color. */
  public void larson(CandleColor color) {
    m_candle.setControl(m_larson.withColor(color.toRGBW()));
  }

  /** Color flowing from one end of the strip to the other. */
  public void colorFlow(CandleColor color) {
    m_candle.setControl(m_colorFlow.withColor(color.toRGBW()));
  }

  /** Flickering fire effect. */
  public void fire() {
    m_candle.setControl(m_fire);
  }

  /** Continuous fade through red, green and blue. */
  public void rgbFade() {
    m_candle.setControl(m_rgbFade);
  }

  /** Single color fading in and out. */
  public void singleFade(CandleColor color) {
    m_candle.setControl(m_singleFade.withColor(color.toRGBW()));
  }

  /** Strobe / blink in the given color. */
  public void strobe(CandleColor color) {
    m_candle.setControl(m_strobe.withColor(color.toRGBW()));
  }

  /** Random LEDs twinkling on in the given color. */
  public void twinkle(CandleColor color) {
    m_candle.setControl(m_twinkle.withColor(color.toRGBW()));
  }

  /** All LEDs on in the given color, randomly twinkling off. */
  public void twinkleOff(CandleColor color) {
    m_candle.setControl(m_twinkleOff.withColor(color.toRGBW()));
  }

  // ---------------------------------------------------------------------------
  // Robot-state driven behavior
  // ---------------------------------------------------------------------------

  /** @return whether {@link #periodic()} is currently driving the LEDs from robot state */
  public boolean isAutoStateEnabled() {
    return m_autoStateEnabled;
  }

  /**
   * Enables or disables the automatic robot-state LED behavior. Disable it before a command drives
   * the LEDs manually; re-enable it to hand control back.
   */
  public void setAutoStateEnabled(boolean enabled) {
    m_autoStateEnabled = enabled;
    m_appliedMode = null; // force a re-apply on the next periodic
  }

  @Override
  public void periodic() {
    if (!m_autoStateEnabled) {
      return;
    }

    LedMode desired = resolveMode();
    if (desired == m_appliedMode) {
      return; // already showing this state - don't spam the CAN bus
    }

    switch (desired) {
      case DISABLED:
        larson(CandleColor.PURPLE);
        break;
      case AUTONOMOUS:
        setColor(CandleColor.PURPLE);
        break;
      case TELEOP_RED:
        setColor(CandleColor.RED);
        break;
      case TELEOP_BLUE:
        setColor(CandleColor.BLUE);
        break;
      case TELEOP_NEUTRAL:
      default:
        setColor(CandleColor.PURPLE);
        break;
    }
    m_appliedMode = desired;
  }

  private LedMode resolveMode() {
    if (DriverStation.isDisabled()) {
      return LedMode.DISABLED;
    }
    if (DriverStation.isAutonomous()) {
      return LedMode.AUTONOMOUS;
    }
    // Teleop (and test) - follow the alliance color once the DS/FMS reports it.
    var alliance = DriverStation.getAlliance();
    if (alliance.isEmpty()) {
      return LedMode.TELEOP_NEUTRAL;
    }
    return alliance.get() == Alliance.Red ? LedMode.TELEOP_RED : LedMode.TELEOP_BLUE;
  }
}
