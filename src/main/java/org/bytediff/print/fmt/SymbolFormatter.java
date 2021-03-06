package org.bytediff.print.fmt;

import org.bytediff.engine.DiffInfo.DiffType;

public class SymbolFormatter implements Formatter {

  @Override
  public String format(String value, DiffType type) {
    switch (type) {
      case INSERT:
        return "++[" + value + "]";
      case DELETE:
        return "--[" + value + "]";
      case REPLACE:
        return "~~[" + value + "]";
      default:
        return value;
    }
  }

}
