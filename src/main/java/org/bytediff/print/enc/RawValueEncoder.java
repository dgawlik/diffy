package org.bytediff.print.enc;

import java.util.stream.Collectors;

public class RawValueEncoder implements Encoder {

    private int radix;

    public RawValueEncoder(int radix) {
        this.radix = radix;
    }

    @Override
    public String encode(String value) {
        return value
                .codePoints()
                .mapToObj(i -> "\\"+Integer.toString(i, radix)+" ")
                .collect(Collectors.joining());
    }
}
