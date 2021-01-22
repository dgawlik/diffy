package org.bytediff.engine;

import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bytediff.engine.DiffInfo.DiffType;


/**
 * Class that compares character arrays by {@code Diff.compute(sourceCP, targetCP)} method. It
 * implements Meyer's algorithm of O(ND) complexity, where N stands for {@code sourceCP.length +
 * targetCP.length} and D is a number of character insertions or deletions taking sourceCP to
 * targetCP.
 */
@UtilityClass
@SuppressWarnings({
    "PMD.ShortClassName",
    "PMD.UseVarargs"
})
public class Diff {

  /**
   * Comparison of char arrays produces linked list of transformations. {@code Op} describes the
   * character of link.
   */
  private enum Op {
    INSERT, //addition of character to source
    DELETE, //deletion of character from source
    MATCH,  //no insertion or deletion is required
    SOURCE //special type, linked list always starts with it
  }

  /**
   * Element of linked list containing all operations transforming source to target.
   */
  @SuppressWarnings("PMD.CommentRequired")
  @NoArgsConstructor
  private static class EditNode {

    int sourceIndex;    //operation happening on source from this index inclusive

    int targetIndex;    //operation happening on source from this index inclusive

    EditNode parent;    //predecessor in linked list

    Op operation;       //operation type see Diff.Op

    @Override
    public String toString() {
      return "EditNode[" +
          sourceIndex +
          "," +
          targetIndex +
          "," +
          operation +
          "]";
    }
  }


  /**
   * Calculates insert,delete,replace,match ranges and returns them wrapped in a structure.
   *
   * @param source array compared against target
   * @param target source of truth array
   * @return {@see DiffInfo}
   */
  public DiffInfo compute(final char[] source, final char[] target) {
    final List<EditNode> stage1Result = computeEditPath(source, target);
    final List<DiffInfo.Diff> stage2Result = computeInfo(stage1Result);
    enforceSurrogatePairs(source, stage2Result);
    return new DiffInfo(source, target, stage2Result);
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  private void enforceSurrogatePairs(final char[] source, final List<DiffInfo.Diff> diffs) {
    for (int i = 0; i < diffs.size() - 1; i++) {

      final DiffInfo.Diff curr = diffs.get(i);
      final DiffInfo.Diff next = diffs.get(i + 1);

      final int idx = curr.getSourceEnd();
      final char candidate = idx >= 0 ? source[curr.getSourceEnd()] : (char) -1;
      if (Character.isHighSurrogate(candidate)) {
        if (curr.getDiffType() == DiffType.MATCH
            && next.getDiffType() == DiffType.REPLACE) {
          curr.sourceEnd = curr.sourceEnd - 1;
          next.sourceStart = next.sourceStart - 1;
          curr.targetEnd = curr.targetEnd - 1;
          next.targetStart = next.targetStart - 1;
        } else if (curr.getDiffType() == DiffType.REPLACE
            && next.getDiffType() == DiffType.MATCH) {
          curr.sourceEnd = curr.sourceEnd + 1;
          next.sourceStart = next.sourceStart + 1;
          curr.targetEnd = curr.targetEnd + 1;
          next.targetStart = next.targetStart + 1;
        }
      }
    }
  }


  private List<DiffInfo.Diff> computeInfo(final List<EditNode> editPath) {

    final List<EditNode> inserts = new ArrayList<>();
    final List<EditNode> deletes = new ArrayList<>();
    final List<EditNode> matches = new ArrayList<>();

    final List<DiffInfo.Diff> result = new ArrayList<>();

    final List<EditNode> trimmedPath = editPath.subList(2, editPath.size());

    for (final EditNode node : trimmedPath) {
      Op operation = node.operation;

      if (operation == Op.INSERT || operation == Op.DELETE) {
        if (operation == Op.INSERT) {
          inserts.add(node);
        }
        if (operation == Op.DELETE) {
          deletes.add(node);
        }
        onInsertionOrDeletion(matches, result);
      }

      if (operation == Op.MATCH) {
        matches.add(node);
        onMatch(inserts, deletes, result);
      }
    }
    onInsertionOrDeletion(matches, result);
    onMatch(inserts, deletes, result);

    return result;
  }

  @SuppressWarnings({"PMD.LawOfDemeter", "PMD.LocalVariableCouldBeFinal"})
  private void onMatch(final List<EditNode> inserts,
      final List<EditNode> deletes,
      final List<DiffInfo.Diff> diffs) {

    int isize = inserts.size();
    int dsize = deletes.size();

    if (isize == dsize && isize > 0) {

      int start = deletes.get(0).sourceIndex;
      int end = deletes.get(deletes.size() - 1).sourceIndex;
      int startTarget = inserts.get(0).targetIndex;
      int endTarget = inserts.get(inserts.size() - 1).targetIndex;

      final DiffInfo.Diff diff = new DiffInfo.Diff(DiffType.REPLACE, start, end,
          startTarget, endTarget);
      diffs.add(diff);

      inserts.clear();
      deletes.clear();
    } else if (isize > 0) {
      int start = inserts.get(0).sourceIndex;
      int end = inserts.get(inserts.size() - 1).sourceIndex;
      int startTarget = inserts.get(0).targetIndex;
      int endTarget = inserts.get(inserts.size() - 1).targetIndex;

      final DiffInfo.Diff diff = new DiffInfo.Diff(DiffType.INSERT, start, end,
          startTarget, endTarget);
      diffs.add(diff);
      inserts.clear();
    } else if (dsize > 0) {
      int start = deletes.get(0).sourceIndex;
      int end = deletes.get(deletes.size() - 1).sourceIndex;
      int before = deletes.get(0).targetIndex;

      final DiffInfo.Diff diff = new DiffInfo.Diff(DiffType.DELETE, start,
          end, before, before);
      diffs.add(diff);
      deletes.clear();
    }
  }

  @SuppressWarnings({"PMD.LawOfDemeter", "PMD.LocalVariableCouldBeFinal"})
  private void onInsertionOrDeletion(final List<EditNode> matches,
      final List<DiffInfo.Diff> diffs) {
    if (!matches.isEmpty()) {
      int start = matches.get(0).sourceIndex;
      int end = matches.get(matches.size() - 1).sourceIndex;
      int startTarget = matches.get(0).targetIndex;
      int endTarget = matches.get(matches.size() - 1).targetIndex;

      final DiffInfo.Diff diff = new DiffInfo.Diff(DiffType.MATCH, start,
          end, startTarget, endTarget);
      diffs.add(diff);
      matches.clear();
    }
  }

  @SuppressWarnings({
      "PMD.LocalVariableCouldBeFinal",
      "PMD.ShortVariable",
      "CyclomaticComplexity"
  })
  private List<EditNode> computeEditPath(final char[] source, final char[] target) {

    int N = source.length;
    int M = target.length;
    int maxD = N + M;
    int middleV = maxD;

    int[] V = new int[2 * maxD + 2];
    Arrays.fill(V, -1);
    V[middleV + 1] = 0;

    EditNode[] Vnodes = new EditNode[2 * maxD + 2];
    EditNode sourceN = new EditNode();
    sourceN.sourceIndex = -1;
    sourceN.targetIndex = -1;
    sourceN.operation = Op.SOURCE;
    Vnodes[middleV + 1] = sourceN;

    for (int D = 0; D < maxD; D++) {
      for (int k = -D; k <= D; k += 2) {
        int x;
        EditNode prevN;
        EditNode currentN = new EditNode();
        if (k == -D
            || k != D
            && V[middleV + k - 1] < V[middleV + k + 1]) {

          x = V[middleV + k + 1];
          prevN = Vnodes[middleV + k + 1];
          currentN.operation = Op.INSERT;
        } else {
          x = V[middleV + k - 1] + 1;
          prevN = Vnodes[middleV + k - 1];
          currentN.operation = Op.DELETE;
        }
        int y = x - k;
        currentN.sourceIndex = x - 1;
        currentN.parent = prevN;
        currentN.targetIndex = y - 1;
        while (x < N && y < M && source[x] == target[y]) {

          x++;
          y++;
          EditNode nextN = new EditNode();
          nextN.sourceIndex = x - 1;
          nextN.targetIndex = y - 1;
          nextN.parent = currentN;
          nextN.operation = Op.MATCH;
          currentN = nextN;
        }

        V[middleV + k] = x;
        Vnodes[middleV + k] = currentN;

        if (x >= N && y >= M) {
          LinkedList<EditNode> path = new LinkedList<>();
          EditNode it = currentN;
          while (it != null) {
            path.offerFirst(it);
            it = it.parent;
          }
          return path;
        }
      }
    }
    throw new IllegalStateException("Algorithm implemented incorrectly");
  }

}
