package org.bytediff;


import com.sun.tools.javac.util.List;
import org.bytediff.display.PrettyDisplay;
import org.bytediff.engine.Diff;
import org.bytediff.engine.DiffInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class UseCasesTest {

    @Test
    public void mismatch_1() {
        char[] source = "aJohnDoe".toCharArray();
        char[] target = "aBBBBDoe".toCharArray();

        DiffInfo info = Diff.compute(source, target);
        System.out.println(PrettyDisplay.from(info).display());
    }

    @Test
    public void mismatch_2() {
        char[] source = "abdJohnDoe😉".toCharArray();
        char[] target = "abcaaJohnDoe😊".toCharArray();

        DiffInfo info = Diff.compute(source, target);
        System.out.println(PrettyDisplay.from(info).display());
    }

    @Test
    public void diff_info_api(){
        char[] source = "bbcc".toCharArray();
        char[] target = "aabb".toCharArray();

        DiffInfo info = Diff.compute(source, target);

        Assertions.assertIterableEquals(info.getInserts(), List.of("aa"));
        Assertions.assertIterableEquals(info.getInsertIndexes(), List.of(-1));
        Assertions.assertIterableEquals(info.getDeletions(), List.of("cc"));
        Assertions.assertIterableEquals(info.getDeletionIndexes(), List.of(2));
    }

}
