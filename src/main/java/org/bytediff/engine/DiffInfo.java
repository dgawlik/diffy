package org.bytediff.engine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bytediff.meta.Internal;

/**
 * Wrapper around insert, delete, match and replace ranges along with target and
 * source arrays.
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class DiffInfo {

  /**
   * Diff ranges
   */
  @Getter
  private final List<Diff> diff;

  /**
   * Copy of source array
   */
  @Getter
  private final char[] source;

  /**
   * Copy of target array
   */
  @Getter
  private final char[] target;


  /**
   * Diff element type
   */
  public enum DiffType {
    INSERT, DELETE, REPLACE, MATCH;
  }

  /**
   * It holds information type with relevant source and target ranges
   */
  @AllArgsConstructor
  public static class Diff {

    @Getter
    /* internal */ DiffType diffType;

    @Getter
    /* internal */ Integer sourceStart;

    @Getter
    /* internal */ Integer sourceEnd;

    @Getter
    /* internal */ Integer targetStart;

    @Getter
    /* internal */ Integer targetEnd;
  }

  DiffInfo(@Nonnull final char[] source,
      @Nonnull final char[] target,
      @Nonnull final List<Diff> diff) {
    this.source = Arrays.copyOf(source, source.length);
    this.target = Arrays.copyOf(target, target.length);
    this.diff = diff;
  }

  public List<String> getInserts() {
    return diff.stream().filter(byType(DiffType.INSERT))
        .map(o -> constructString(target, o.targetStart, o.targetEnd))
        .collect(Collectors.toList());
  }

  public List<String> getDeletions() {
    return diff.stream().filter(byType(DiffType.DELETE))
        .map(o -> constructString(source, o.sourceStart, o.sourceEnd))
        .collect(Collectors.toList());
  }

  public List<String> getReplacements() {
    return diff.stream().filter(byType(DiffType.REPLACE))
        .map(o -> constructString(target, o.targetStart, o.targetEnd))
        .collect(Collectors.toList());
  }

  public List<String> getMatches() {
    return diff.stream().filter(byType(DiffType.MATCH))
        .map(o -> constructString(source, o.sourceStart, o.sourceEnd))
        .collect(Collectors.toList());
  }

  public List<Integer> getInsertIndexes() {
    return diff.stream().filter(byType(DiffType.INSERT))
        .map(Diff::getSourceStart)
        .collect(Collectors.toList());
  }

  public List<Integer> getDeletionIndexes() {
    return diff.stream().filter(byType(DiffType.DELETE))
        .map(Diff::getSourceStart)
        .collect(Collectors.toList());
  }

  public List<Integer> getReplacementIndexes() {
    return diff.stream().filter(byType(DiffType.REPLACE))
        .map(Diff::getSourceStart)
        .collect(Collectors.toList());
  }

  public List<Integer> getMatchIndexes() {
    return diff.stream().filter(byType(DiffType.MATCH))
        .map(Diff::getSourceStart)
        .collect(Collectors.toList());
  }

  private Predicate<Diff> byType(final DiffType type) {
    return e -> e.getDiffType() == type;
  }

  private String constructString(final char[] arr, final int start,
      final int end) {
    return new String(Arrays.copyOfRange(arr, start, end + 1));
  }
}
