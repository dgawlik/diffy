package org.bytediff.util;


import org.bytediff.engine.Diff;
import org.bytediff.engine.DiffInfo;
import org.bytediff.engine.DiffInfo.DiffType;
import org.bytediff.print.Printer;
import org.bytediff.print.enc.RawValueEncoder;
import org.bytediff.print.fmt.AnsiColorFormatter;

public class DiffyShortcuts {

  public static void log(String source, String target) {
    DiffInfo info = Diff.compute(source.toCharArray(), target.toCharArray());
    Printer p = Printer.from(info);
    System.out.println(p.print());
  }

  public static void logVerbose(String source, String target) {
    DiffInfo info = Diff.compute(source.toCharArray(), target.toCharArray());
    Printer p = Printer.from(info).verbose();
    System.out.println(p.print());
  }

  public static void logColors(String source, String target) {
    DiffInfo info = Diff.compute(source.toCharArray(), target.toCharArray());
    Printer p = Printer.from(info).withFormatter(new AnsiColorFormatter());
    System.out.println(p.print());
  }

  public static void log(byte[] source, byte[] target, int radix) {
    char[] sourceC = Raw.bytesToChars(source);
    char[] targetC = Raw.bytesToChars(target);

    DiffInfo info = Diff.compute(sourceC, targetC);
    Printer p = Printer.from(info).withEncoding(new RawValueEncoder(radix));
    System.out.println(p.print());
  }

  public static void logVerbose(byte[] source, byte[] target, int radix) {
    char[] sourceC = Raw.bytesToChars(source);
    char[] targetC = Raw.bytesToChars(target);

    DiffInfo info = Diff.compute(sourceC, targetC);
    Printer p = Printer.from(info)
        .withEncoding(new RawValueEncoder(radix))
        .verbose();
    System.out.println(p.print());
  }

  public static void logColor(byte[] source, byte[] target, int radix) {
    char[] sourceC = Raw.bytesToChars(source);
    char[] targetC = Raw.bytesToChars(target);

    DiffInfo info = Diff.compute(sourceC, targetC);
    Printer p = Printer.from(info)
        .withEncoding(new RawValueEncoder(radix))
        .withFormatter(new AnsiColorFormatter())
        .verbose();
    System.out.println(p.print());
  }

  public static void assertEquals(String source, String target) {
    DiffInfo info = Diff.compute(source.toCharArray(), target.toCharArray());

    if (info.getDiff().size() == 1
        && info.getDiff().get(0).getDiffType() == DiffType.MATCH) {
      return;
    }

    Printer p = Printer.from(info);
    throw new AssertionError("Diffy match failed\n" + p.print());
  }

  public static void assertEquals(byte[] source, byte[] target, int radix) {
    char[] sourceC = Raw.bytesToChars(source);
    char[] targetC = Raw.bytesToChars(target);
    DiffInfo info = Diff.compute(sourceC, targetC);

    if (info.getDiff().size() == 1
        && info.getDiff().get(0).getDiffType() == DiffType.MATCH) {
      return;
    }

    Printer p = Printer.from(info)
        .withEncoding(new RawValueEncoder(radix));
    throw new AssertionError("Diffy match failed\n" + p.print());
  }
}
