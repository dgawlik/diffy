package org.bytediff.print.fmt;

import org.bytediff.engine.DiffInfo;

import java.util.EnumMap;

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

    EnumMap<DiffInfo.InfoType, ForegroundColor> fgColor;
    EnumMap<DiffInfo.InfoType, BackgroundColor> bgColor;

    public AnsiColorFormatter() {
        fgColor = new EnumMap<>(DiffInfo.InfoType.class);
        bgColor = new EnumMap<>(DiffInfo.InfoType.class);

        bgColor.put(DiffInfo.InfoType.INSERT, BackgroundColor.GREEN);
        fgColor.put(DiffInfo.InfoType.INSERT, ForegroundColor.BLACK);

        bgColor.put(DiffInfo.InfoType.DELETE, BackgroundColor.RED);
        fgColor.put(DiffInfo.InfoType.DELETE, ForegroundColor.BLACK);

        bgColor.put(DiffInfo.InfoType.REPLACE, BackgroundColor.YELLOW);
        fgColor.put(DiffInfo.InfoType.REPLACE, ForegroundColor.BLACK);
    }

    public AnsiColorFormatter withInsertionColors(ForegroundColor fgColor, BackgroundColor bgColor) {
        this.fgColor.put(DiffInfo.InfoType.INSERT, fgColor);
        this.bgColor.put(DiffInfo.InfoType.INSERT, bgColor);
        return this;
    }

    public AnsiColorFormatter withDeletionColors(ForegroundColor fgColor, BackgroundColor bgColor) {
        this.fgColor.put(DiffInfo.InfoType.DELETE, fgColor);
        this.bgColor.put(DiffInfo.InfoType.DELETE, bgColor);
        return this;
    }

    public AnsiColorFormatter withReplacementColors(ForegroundColor fgColor, BackgroundColor bgColor) {
        this.fgColor.put(DiffInfo.InfoType.REPLACE, fgColor);
        this.bgColor.put(DiffInfo.InfoType.REPLACE, bgColor);
        return this;
    }

    @Override
    public String format(String value, DiffInfo.InfoType type) {
        return ansiColor(type) + value + ansiResetColor();
    }

    private String ansiColor(DiffInfo.InfoType type) {
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
