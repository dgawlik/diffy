package org.bytediff.print.fmt;

import java.util.EnumMap;
import org.bytediff.engine.DiffInfo.DiffType;

public class AnsiColorFormatter implements Formatter {

  public enum ForegroundColor {
    BLACK(30), RED(31), GREEN(32),
    YELLOW(33), BLUE(34), MAGENTA(35),
    CYAN(36), WHITE(37);

    private int repr;

    private ForegroundColor(int value) {
      this.repr = value;
    }

    public int getRepr() {
      return this.repr;
    }
  }

  public enum BackgroundColor {
    BLACK(40), RED(41), GREEN(42),
    YELLOW(43), BLUE(44), MAGENTA(45),
    CYAN(46), WHITE(47);

    private int repr;

    private BackgroundColor(int value) {
      this.repr = value;
    }

    public int getRepr() {
      return this.repr;
    }
  }

  EnumMap<DiffType, ForegroundColor> fgColor;
  EnumMap<DiffType, BackgroundColor> bgColor;

  public AnsiColorFormatter() {
    fgColor = new EnumMap<>(DiffType.class);
    bgColor = new EnumMap<>(DiffType.class);

    bgColor.put(DiffType.INSERT, BackgroundColor.GREEN);
    fgColor.put(DiffType.INSERT, ForegroundColor.BLACK);

    bgColor.put(DiffType.DELETE, BackgroundColor.RED);
    fgColor.put(DiffType.DELETE, ForegroundColor.BLACK);

    bgColor.put(DiffType.REPLACE, BackgroundColor.YELLOW);
    fgColor.put(DiffType.REPLACE, ForegroundColor.BLACK);
  }

  public AnsiColorFormatter withInsertionColors(ForegroundColor fgColor, BackgroundColor bgColor) {
    this.fgColor.put(DiffType.INSERT, fgColor);
    this.bgColor.put(DiffType.INSERT, bgColor);
    return this;
  }

  public AnsiColorFormatter withDeletionColors(ForegroundColor fgColor, BackgroundColor bgColor) {
    this.fgColor.put(DiffType.DELETE, fgColor);
    this.bgColor.put(DiffType.DELETE, bgColor);
    return this;
  }

  public AnsiColorFormatter withReplacementColors(ForegroundColor fgColor,
      BackgroundColor bgColor) {
    this.fgColor.put(DiffType.REPLACE, fgColor);
    this.bgColor.put(DiffType.REPLACE, bgColor);
    return this;
  }

  @Override
  public String format(String value, DiffType type) {
    return ansiColor(type) + value + ansiResetColor();
  }

  private String ansiColor(DiffType type) {
    ForegroundColor fgColor = this.fgColor.get(type);
    BackgroundColor bgColor = this.bgColor.get(type);

    return "\033[2;"
        + (fgColor != null ? fgColor.getRepr() : 0)
        + ";"
        + (bgColor != null ? bgColor.getRepr() : 0)
        + "m";
  }

  private String ansiResetColor() {
    return "\033[0m";
  }
}
