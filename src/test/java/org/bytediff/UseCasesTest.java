package org.bytediff;


import org.bytediff.display.PrettyDisplay;
import org.bytediff.engine.Diff;
import org.bytediff.engine.DiffInfo;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class UseCasesTest {

    @Test
    public void mismatch_1() {
        char[] source = "aJohnDoe".toCharArray();
        char[] target = "aBBBBDoe".toCharArray();

        Diff d = new Diff();
        DiffInfo info = d.compute(source, target);
        System.out.println(PrettyDisplay.from(info).display());
    }

    @Test
    public void mismatch_2() {
        char[] source = "abdJohnDoe😉".toCharArray();
        char[] target = "abcaaJohnDoe😊".toCharArray();

        Diff d = new Diff();
        DiffInfo info = d.compute(source, target);
        System.out.println(PrettyDisplay.from(info).display());
    }

}
