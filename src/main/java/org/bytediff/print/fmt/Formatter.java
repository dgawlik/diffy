package org.bytediff.print.fmt;

import org.bytediff.engine.DiffInfo.DiffType;

public interface Formatter {

  String format(String value, DiffType type);
}
