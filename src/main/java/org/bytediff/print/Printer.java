package org.bytediff.print;

import org.bytediff.engine.DiffInfo;
import org.bytediff.print.fmt.Formatter;
import org.bytediff.print.fmt.SymbolFormatter;

import java.text.Format;

public class Printer {

    private DiffInfo info;
    private boolean isCompact;
    private Formatter fmt;


    private Printer(DiffInfo info) {
        this.info = info;
        this.isCompact = true;
        this.fmt = new SymbolFormatter();
    }

    public String print() {
        String sourceS = new String(this.info.getSource());
        String targetS = new String(this.info.getTarget());

        StringBuilder sb = new StringBuilder();

        for (DiffInfo.Info el : this.info.getInfo()) {
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

            sb.append(fmt.format(s.substring(start, end + 1), el.getInfoType()));
        }
        return sb.toString();
    }

    public static Printer from(DiffInfo info) {
        return new Printer(info);
    }

    public Printer verbose() {
        this.isCompact = false;
        return this;
    }

    public Printer withFormatter(Formatter fmt) {
        this.fmt = fmt;
        return this;
    }

}
