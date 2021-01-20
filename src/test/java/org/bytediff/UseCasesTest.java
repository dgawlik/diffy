package org.bytediff;


import com.sun.tools.javac.util.List;
import org.bytediff.print.Printer;
import org.bytediff.engine.Diff;
import org.bytediff.engine.DiffInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UseCasesTest {

    @Test
    public void mismatch_1() {
        char[] source = "aJohnDoe".toCharArray();
        char[] target = "aBBBBDoe".toCharArray();

        DiffInfo info = Diff.compute(source, target);
        System.out.println(Printer.from(info).print());
    }

    @Test
    public void mismatch_2() {
        char[] source = "abdJohnDoe😉".toCharArray();
        char[] target = "abcaaJohnDoe😊".toCharArray();

        DiffInfo info = Diff.compute(source, target);
        System.out.println(Printer.from(info).print());
    }


    @Test
    public void verbose(){
        char[] source = "jooohnbb".toCharArray();
        char[] target = "johnaa".toCharArray();

        DiffInfo info = Diff.compute(source, target);
        System.out.println(Printer.from(info).verbose().print());
    }


}
