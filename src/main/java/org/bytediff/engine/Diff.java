package org.bytediff.engine;

import lombok.Data;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Class that compares character arrays by {@code Diff.compute(sourceCP, targetCP)}
 * method. It implements Meyer's algorithm of O(ND) complexity, where N stands for
 * {@code sourceCP.length + targetCP.length} and D is a number of character insertions
 * or deletions taking sourceCP to targetCP.
 */
@UtilityClass
@SuppressWarnings({
        "PMD.ShortClassName",
        "PMD.UseVarargs"
})
public class Diff {

    /**
     * Comparison of char arrays produces linked list of transformations.
     * {@code Op} describes the character of link.
     */
    private enum Op {
        INSERT, //addition of character to source
        DELETE, //deletion of character from source
        MATCH,  //no insertion or deletion is required
        SOURCE //special type, linked list always starts with it
    }

    /**
     * Element of linked list containing all operations transforming
     * source to target.
     */
    @Data
    @SuppressWarnings("PMD.CommentRequired")
    private static class EditNode {
        private int sourceIndex;    //operation happening on source from this index inclusive

        private int targetIndex;    //operation happening on source from this index inclusive

        private EditNode parent;    //predecessor in linked list

        private Op operation;       //operation type see Diff.Op

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
     * Calculates insert,delete,replace,match ranges and returns them
     * wrapped in a structure.
     *
     * @param source array compared against target
     * @param target source of truth array
     * @return {@see DiffInfo}
     */
    public DiffInfo compute(final char[] source, final char[] target) {
        final List<EditNode> stage1Result = computeEditPath(source, target);
        final List<DiffInfo.Info> stage2Result = computeInfo(stage1Result);
        enforceSurrogatePairs(source, stage2Result);
        return new DiffInfo(source, target, stage2Result);
    }

    private void enforceSurrogatePairs(char[] source, List<DiffInfo.Info> lst) {
        for (int i = 0; i < lst.size() - 1; i++) {

            final DiffInfo.Info curr = lst.get(i);
            final DiffInfo.Info next = lst.get(i + 1);

            final int idx = curr.getSourceEnd();
            final char candidate = idx >= 0 ? source[curr.getSourceEnd()] : (char) -1;
            if (Character.isHighSurrogate(candidate)) {
                if (curr.getInfoType() == DiffInfo.InfoType.MATCH
                        && next.getInfoType() == DiffInfo.InfoType.REPLACE) {
                    curr.sourceEnd = curr.sourceEnd - 1;
                    next.sourceStart = next.sourceStart - 1;
                    curr.targetEnd = curr.targetEnd - 1;
                    next.targetStart = next.targetStart - 1;
                }
                if (curr.getInfoType() == DiffInfo.InfoType.REPLACE
                        && next.getInfoType() == DiffInfo.InfoType.MATCH) {
                    curr.sourceEnd = curr.sourceEnd + 1;
                    next.sourceStart = next.sourceStart + 1;
                    curr.targetEnd = curr.targetEnd + 1;
                    next.targetStart = next.targetStart + 1;
                }
            }
        }
    }


    private List<DiffInfo.Info> computeInfo(List<EditNode> path) {

        final List<EditNode> inserts = new ArrayList<>();
        final List<EditNode> deletes = new ArrayList<>();
        final List<EditNode> matches = new ArrayList<>();

        final List<DiffInfo.Info> result = new ArrayList<>();

        final List<EditNode> trimmedPath =  path.subList(2, path.size());

        for (final EditNode node : trimmedPath) {
            Op op = node.getOperation();

            if (op == Op.INSERT || op == Op.DELETE) {
                if (op == Op.INSERT) {
                    inserts.add(node);
                }
                if (op == Op.DELETE) {
                    deletes.add(node);
                }
                onInsertionOrDeletion(matches, result);
            }

            if (op == Op.MATCH) {
                matches.add(node);
                onMatch(inserts, deletes, result);
            }
        }
        onInsertionOrDeletion(matches, result);
        onMatch(inserts, deletes, result);

        return result;
    }

    private void onMatch(List<EditNode> inserts, List<EditNode> deletes, List<DiffInfo.Info> lst) {
        if (!inserts.isEmpty() && !deletes.isEmpty()
                && inserts.size() == deletes.size()) {

            int start = deletes.get(0).getSourceIndex();
            int end = deletes.get(deletes.size() - 1).getSourceIndex();
            int startTarget = inserts.get(0).getTargetIndex();
            int endTarget = inserts.get(inserts.size() - 1).getTargetIndex();

            DiffInfo.Info info = new DiffInfo.Info(DiffInfo.InfoType.REPLACE, start, end,
                    startTarget, endTarget);
            lst.add(info);

            inserts.clear();
            deletes.clear();
        } else if (!inserts.isEmpty()) {
            int start = inserts.get(0).getSourceIndex();
            int end = inserts.get(inserts.size() - 1).getSourceIndex();
            int startTarget = inserts.get(0).getTargetIndex();
            int endTarget = inserts.get(inserts.size() - 1).getTargetIndex();

            DiffInfo.Info info = new DiffInfo.Info(DiffInfo.InfoType.INSERT, start, end,
                    startTarget, endTarget);
            lst.add(info);
            inserts.clear();
        } else if (!deletes.isEmpty()) {
            int start = deletes.get(0).getSourceIndex();
            int end = deletes.get(deletes.size() - 1).getSourceIndex();
            int before = deletes.get(0).getTargetIndex();

            DiffInfo.Info info = new DiffInfo.Info(DiffInfo.InfoType.DELETE, start,
                    end, before, before);
            lst.add(info);
            deletes.clear();
        }
    }

    private void onInsertionOrDeletion(List<EditNode> matches, List<DiffInfo.Info> lst) {
        if (!matches.isEmpty()) {
            int start = matches.get(0).getSourceIndex();
            int end = matches.get(matches.size() - 1).getSourceIndex();
            int startTarget = matches.get(0).getTargetIndex();
            int endTarget = matches.get(matches.size() - 1).getTargetIndex();

            DiffInfo.Info info = new DiffInfo.Info(DiffInfo.InfoType.MATCH, start,
                    end, startTarget, endTarget);
            lst.add(info);
            matches.clear();
        }
    }

    private List<EditNode> computeEditPath(char[] sourceCP, char[] targetCP) {

        int N = sourceCP.length;
        int M = targetCP.length;
        int maxD = N + M;
        int middleV = maxD;

        int[] V = new int[2 * maxD + 2];
        Arrays.fill(V, -1);
        V[middleV + 1] = 0;

        EditNode[] Vnodes = new EditNode[2 * maxD + 2];
        EditNode sourceN = new EditNode();
        sourceN.setSourceIndex(-1);
        sourceN.setTargetIndex(-1);
        sourceN.setOperation(Op.SOURCE);
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
                    currentN.setOperation(Op.INSERT);
                } else {
                    x = V[middleV + k - 1] + 1;
                    prevN = Vnodes[middleV + k - 1];
                    currentN.setOperation(Op.DELETE);
                }
                int y = x - k;
                currentN.setSourceIndex(x - 1);
                currentN.setParent(prevN);
                currentN.setTargetIndex(y - 1);
                while (x < N && y < M && sourceCP[x] == targetCP[y]) {

                    x++;
                    y++;
                    EditNode nextN = new EditNode();
                    nextN.setSourceIndex(x - 1);
                    nextN.setTargetIndex(y - 1);
                    nextN.setParent(currentN);
                    nextN.setOperation(Op.MATCH);
                    currentN = nextN;
                }

                V[middleV + k] = x;
                Vnodes[middleV + k] = currentN;

                if (x >= N && y >= M) {
                    LinkedList<EditNode> path = new LinkedList<>();
                    EditNode it = currentN;
                    while (it != null) {
                        path.offerFirst(it);
                        it = it.getParent();
                    }
                    return path;
                }
            }
        }
        throw new IllegalStateException("Algorithm implemented incorrectly");
    }

}
