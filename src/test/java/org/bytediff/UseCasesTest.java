package org.bytediff;


import org.bytediff.engine.Diff;
import org.bytediff.engine.DiffInfo;
import org.bytediff.print.Printer;
import org.bytediff.print.enc.RawValueEncoder;
import org.bytediff.print.fmt.AnsiColorFormatter;
import org.bytediff.util.Raw;
import org.junit.jupiter.api.Test;

public class UseCasesTest {

  @Test
  public void mismatch_1() {
    char[] source = "quickbrownfoxjumpingoverlazydown".toCharArray();
    char[] target = "quickfoxjumpingoverlazydown".toCharArray();

    DiffInfo info = Diff.compute(source, target);
    String result = Printer
        .from(info)
        .withFormatter(new AnsiColorFormatter())
        .print();
    System.out.println(result);
  }

  @Test
  public void mismatch_2() {
    char[] source = "abdJohnDoe😉".toCharArray();
    char[] target = "abcaaJohnDoe😊".toCharArray();

    DiffInfo info = Diff.compute(source, target);
    System.out.println(Printer.from(info).verbose().print());
  }


  @Test
  public void verbose() {
    char[] source = "jooohnbb".toCharArray();
    char[] target = "johnaa".toCharArray();

    DiffInfo info = Diff.compute(source, target);
    Printer p = Printer.from(info).withFormatter(new AnsiColorFormatter());
    System.out.println(p.print());
  }

  @Test
  public void raw_bytes() {
    byte[] source = new byte[]{1, 2, 3};
    byte[] target = new byte[]{4, 2, 3};

    char[] sourceC = Raw.bytesToChars(source);
    char[] targetC = Raw.bytesToChars(target);

    DiffInfo info = Diff.compute(sourceC, targetC);
    Printer p = Printer
        .from(info)
        .withEncoding(new RawValueEncoder(10));
    System.out.println(p.print());
  }


}
