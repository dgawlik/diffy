package org.bytediff.print;

import org.bytediff.engine.DiffInfo;
import org.bytediff.print.enc.Encoder;
import org.bytediff.print.enc.IdEncoder;
import org.bytediff.print.fmt.Formatter;
import org.bytediff.print.fmt.SymbolFormatter;


public class Printer {

  private DiffInfo info;
  private boolean isCompact;
  private Formatter fmt;
  private Encoder enc;
  private int contextLeft;
  private int contextRight;


  private Printer(DiffInfo info) {
    this.info = info;
    this.isCompact = true;
    this.fmt = new SymbolFormatter();
    this.contextLeft = 5;
    this.contextRight = 5;
    this.enc = new IdEncoder();
  }

  public String print() {
    String sourceS = new String(this.info.getSource());
    String targetS = new String(this.info.getTarget());

    StringBuilder sb = new StringBuilder();

    if (this.info.getInfo().size() == 1
        && this.info.getInfo().get(0).getInfoType() == DiffInfo.InfoType.MATCH) {
      return "Identical.";
    }

    for (DiffInfo.Info el : this.info.getInfo()) {
      if (this.isCompact) {
        int start, end;
        String s;
        if (el.getInfoType() == DiffInfo.InfoType.REPLACE
            || el.getInfoType() == DiffInfo.InfoType.INSERT) {
          start = el.getTargetStart();
          end = el.getTargetEnd() + 1;
          s = targetS;
        } else {
          start = el.getSourceStart();
          end = el.getSourceEnd() + 1;
          s = sourceS;
        }

        sb.append(fmt.format(enc.encode(s.substring(start, end)), el.getInfoType()));
      } else {
        if (el.getInfoType() == DiffInfo.InfoType.MATCH) {
          continue;
        }

        int start = el.getSourceStart();
        int end = el.getSourceEnd() + 1;

        int contextLeftStart = Math.max(0, start - this.contextLeft);
        int contextLeftEnd = start;
        int contextRightStart = Math.min(end + 1, sourceS.length());
        int contextRightEnd = Math.min(end + contextRight, sourceS.length());

        String diff;
        if (el.getInfoType() == DiffInfo.InfoType.REPLACE
            || el.getInfoType() == DiffInfo.InfoType.INSERT) {
          diff = fmt.format(enc.encode(targetS.substring(
              el.getTargetStart(), el.getTargetEnd() + 1)), el.getInfoType());
        } else {
          diff = fmt.format(enc.encode(sourceS.substring(start, end)), el.getInfoType());
        }

        String line = "*> "
            + (contextLeftStart == contextLeftEnd
            ? "" : "..." + sourceS.substring(contextLeftStart, contextLeftEnd))
            + diff
            + (contextRightStart == contextRightEnd
            ? "" : sourceS.substring(contextRightStart, contextRightEnd) + "...")
            + "\n";
        sb.append(line);
      }
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

  public Printer withLeftContext(int value) {
    this.contextLeft = value;
    return this;
  }

  public Printer withRightContext(int value) {
    this.contextRight = value;
    return this;
  }

  public Printer withFormatter(Formatter fmt) {
    this.fmt = fmt;
    return this;
  }

  public Printer withEncoding(Encoder enc) {
    this.enc = enc;
    return this;
  }

}
