package org.bytediff.print;

import java.util.Arrays;
import javax.annotation.Nonnull;
import org.bytediff.engine.DiffInfo;
import org.bytediff.engine.DiffInfo.Diff;
import org.bytediff.engine.DiffInfo.DiffType;
import org.bytediff.print.enc.Encoder;
import org.bytediff.print.enc.IdEncoder;
import org.bytediff.print.fmt.Formatter;
import org.bytediff.print.fmt.SymbolFormatter;


/**
 * Displays representation of {@code DiffInfo}. It is configured by {@code
 * Encoder} and {@code Formatter}
 */
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public final class Printer {

  private final DiffInfo diff;
  private boolean isCompact;
  private Formatter fmt;
  private Encoder enc;
  private int contextLeft;
  private int contextRight;


  private Printer(@Nonnull final DiffInfo diff) {
    this.diff = diff;
    this.isCompact = true;
    this.fmt = new SymbolFormatter();
    this.contextLeft = 5;
    this.contextRight = 5;
    this.enc = new IdEncoder();
  }

  public String print() {
    if (this.diff.getDiff().size() == 1
        && this.diff.getDiff().get(0).getDiffType() == DiffType.MATCH) {
      return "Identical.";
    }

    char[] source = this.diff.getSource();
    char[] target = this.diff.getTarget();

    StringBuilder sb = new StringBuilder();

    for (Diff diffElement : this.diff.getDiff()) {
      if (this.isCompact) {
        int start, end;
        char[] s;
        if (diffElement.getDiffType() == DiffType.REPLACE
            || diffElement.getDiffType() == DiffType.INSERT) {
          start = diffElement.getTargetStart();
          end = diffElement.getTargetEnd() + 1;
          s = target;
        } else {
          start = diffElement.getSourceStart();
          end = diffElement.getSourceEnd() + 1;
          s = source;
        }

        sb.append(
            fmt.format(
                enc.encode(
                    newSlice(start, end, s)),
                diffElement.getDiffType()));
      } else {
        if (diffElement.getDiffType() == DiffType.MATCH) {
          continue;
        }

        int start = diffElement.getSourceStart();
        int end = diffElement.getSourceEnd() + 1;

        int contextLeftStart = Math.max(0, start - this.contextLeft);
        int contextLeftEnd = start;
        int contextRightStart = Math.min(end + 1, source.length);
        int contextRightEnd = Math.min(end + contextRight, source.length);

        String diff;
        if (diffElement.getDiffType() == DiffType.REPLACE
            || diffElement.getDiffType() == DiffType.INSERT) {
          diff = fmt.format(
              enc.encode(newSlice(diffElement.getTargetStart(),
                  diffElement.getTargetEnd() + 1, target)),
              diffElement.getDiffType());
        } else {
          diff = fmt.format(
              enc.encode(
                  newSlice(start, end, source)),
              diffElement.getDiffType());
        }

        String line = "*> "
            + (contextLeftStart == contextLeftEnd
            ? "" : "..." + newSlice(contextLeftStart, contextLeftEnd, source))
            + diff
            + (contextRightStart == contextRightEnd
            ? "" : newSlice(contextRightStart, contextRightEnd, source) + "...")
            + "\n";
        sb.append(line);
      }
    }
    return sb.toString();
  }

  private String newSlice(int start, int end, char[] s) {
    return new String(Arrays.copyOfRange(s, start, end));
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
