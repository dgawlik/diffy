package org.bytediff.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DiffInfo {

  @Getter
  private final List<Info> info;
  @Getter
  private final char[] source;
  @Getter
  private final char[] target;

  public enum InfoType {
    INSERT, DELETE, REPLACE, MATCH;
  }

  @AllArgsConstructor
  public static class Info {

    @Getter
    InfoType infoType;

    @Getter
    Integer sourceStart;

    @Getter
    Integer sourceEnd;

    @Getter
    Integer targetStart;

    @Getter
    Integer targetEnd;
  }

  DiffInfo(final char[] source, final char[] target, final List<Info> info) {
    this.source = source;
    this.target = target;
    this.info = info;
  }

  public List<String> getInserts() {
    return info.stream().filter(byType(InfoType.INSERT))
        .map(o -> constructString(target, o.targetStart, o.targetEnd))
        .collect(Collectors.toList());
  }

  public List<String> getDeletions() {
    return info.stream().filter(byType(InfoType.DELETE))
        .map(o -> constructString(source, o.sourceStart, o.sourceEnd))
        .collect(Collectors.toList());
  }

  public List<String> getReplacements() {
    return info.stream().filter(byType(InfoType.REPLACE))
        .map(o -> constructString(target, o.targetStart, o.targetEnd))
        .collect(Collectors.toList());
  }

  public List<String> getMatches() {
    return info.stream().filter(byType(InfoType.MATCH))
        .map(o -> constructString(source, o.sourceStart, o.sourceEnd))
        .collect(Collectors.toList());
  }

  public List<Integer> getInsertIndexes() {
    return info.stream().filter(byType(InfoType.INSERT))
        .map(Info::getSourceStart)
        .collect(Collectors.toList());
  }

  public List<Integer> getDeletionIndexes() {
    return info.stream().filter(byType(InfoType.DELETE))
        .map(Info::getSourceStart)
        .collect(Collectors.toList());
  }

  public List<Integer> getReplacementIndexes() {
    return info.stream().filter(byType(InfoType.REPLACE))
        .map(Info::getSourceStart)
        .collect(Collectors.toList());
  }

  public List<Integer> getMatchIndexes() {
    return info.stream().filter(byType(InfoType.MATCH))
        .map(Info::getSourceStart)
        .collect(Collectors.toList());
  }

  private Predicate<Info> byType(InfoType type) {
    return e -> e.getInfoType() == type;
  }

  private String constructString(char[] arr, int start, int end) {
    return new String(Arrays.copyOfRange(arr, start, end + 1));
  }
}
