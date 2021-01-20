package org.bytediff.print.fmt;

import org.bytediff.engine.DiffInfo;

public interface Formatter {
    String format(String value, DiffInfo.InfoType type);
}
