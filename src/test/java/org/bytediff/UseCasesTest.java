package org.bytediff;


import org.bytediff.display.PrettyDisplay;
import org.bytediff.engine.Diff;
import org.bytediff.engine.DiffInfo;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class UseCasesTest {

    @Test
    public void mismatch_1() {
        byte[] source = "aJohnDoe".getBytes(StandardCharsets.UTF_8);
        byte[] target = "aBBBBDoe".getBytes(StandardCharsets.UTF_8);

        Diff d = new Diff();
        DiffInfo info = d.compute(source, target, StandardCharsets.UTF_8);
        System.out.println(PrettyDisplay.from(info).display());
    }

    @Test
    public void mismatch_2() {
        byte[] source = "JohnDoe😉".getBytes(StandardCharsets.UTF_16);
        byte[] target = "JohnDoe😊".getBytes(StandardCharsets.UTF_16);

        Diff d = new Diff();
        DiffInfo info = d.compute(source, target, StandardCharsets.UTF_16);
        System.out.println(PrettyDisplay.from(info).display());
    }

}
