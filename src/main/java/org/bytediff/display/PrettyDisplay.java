package org.bytediff.display;

import org.bytediff.engine.Diff;
import org.bytediff.engine.DiffInfo;

import java.util.EnumMap;

public class PrettyDisplay {
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

    private DiffInfo info;
    private boolean isCompact;
    EnumMap<DiffInfo.InfoType, ForegroundColor> fgColor;
    EnumMap<DiffInfo.InfoType, BackgroundColor> bgColor;

    private PrettyDisplay(DiffInfo info) {
        this.info = info;
        this.isCompact = true;
        fgColor = new EnumMap<>(DiffInfo.InfoType.class);
        bgColor = new EnumMap<>(DiffInfo.InfoType.class);

        bgColor.put(DiffInfo.InfoType.INSERT, BackgroundColor.GREEN);
        fgColor.put(DiffInfo.InfoType.INSERT, ForegroundColor.BLACK);

        bgColor.put(DiffInfo.InfoType.DELETE, BackgroundColor.RED);
        fgColor.put(DiffInfo.InfoType.DELETE, ForegroundColor.BLACK);

        bgColor.put(DiffInfo.InfoType.REPLACE, BackgroundColor.YELLOW);
        fgColor.put(DiffInfo.InfoType.REPLACE, ForegroundColor.BLACK);
    }

    public String display() {
        String sourceS = new String(this.info.getSource());
        String targetS = new String(this.info.getTarget());

        StringBuilder sb = new StringBuilder();

        for (DiffInfo.Info el : this.info.getInfo()) {
            ForegroundColor fgColor = this.fgColor.get(el.getInfoType());
            BackgroundColor bgColor = this.bgColor.get(el.getInfoType());

            int start, end;
            String s;
            if (el.getInfoType() == DiffInfo.InfoType.REPLACE
                    || el.getInfoType() == DiffInfo.InfoType.INSERT) {
                start = el.getTargetStart();
                end = el.getTargetEnd();
                s = targetS;
            } else {
                start = el.getSourceStart();
                end = el.getSourceEnd();
                s = sourceS;
            }

            sb.append(ansiColor(fgColor, bgColor))
                    .append(s, start, end+1)
                    .append(ansiResetColor());
        }
        return sb.toString();
    }

    public static PrettyDisplay from(DiffInfo info) {
        return new PrettyDisplay(info);
    }

    public PrettyDisplay verbose() {
        this.isCompact = false;
        return this;
    }

    public PrettyDisplay withInsertionColors(ForegroundColor fgColor, BackgroundColor bgColor) {
        this.fgColor.put(DiffInfo.InfoType.INSERT, fgColor);
        this.bgColor.put(DiffInfo.InfoType.INSERT, bgColor);
        return this;
    }

    public PrettyDisplay withDeletionColors(ForegroundColor fgColor, BackgroundColor bgColor) {
        this.fgColor.put(DiffInfo.InfoType.DELETE, fgColor);
        this.bgColor.put(DiffInfo.InfoType.DELETE, bgColor);
        return this;
    }

    public PrettyDisplay withReplacementColors(ForegroundColor fgColor, BackgroundColor bgColor) {
        this.fgColor.put(DiffInfo.InfoType.REPLACE, fgColor);
        this.bgColor.put(DiffInfo.InfoType.REPLACE, bgColor);
        return this;
    }

    private String ansiColor(ForegroundColor fgColor, BackgroundColor bgColor) {
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
