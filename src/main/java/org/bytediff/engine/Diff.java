package org.bytediff.engine;

import lombok.Data;
import sun.nio.cs.Surrogate;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.*;

public class Diff {

    private enum Op {
        INSERT, DELETE, MATCH, SOURCE
    }

    @Data
    private static class EditNode {
        private int sourceIndex;
        private int targetIndex;
        private EditNode parent;
        private Op operation;

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


    public static DiffInfo compute(char[] sourceCP, char[] targetCP) {
        List<EditNode> path = computeEditPath(sourceCP, targetCP);
        DiffInfo info = computeInfo(path, sourceCP, targetCP);
        enforceSurrogatePairs(info);
        return info;
    }

    private static void enforceSurrogatePairs(DiffInfo info) {
        List<DiffInfo.Info> infos = info.getInfo();
        for (int i = 0; i < infos.size() - 1; i++) {

            DiffInfo.Info curr = infos.get(i);
            DiffInfo.Info next = infos.get(i + 1);

            int idx = curr.getSourceEnd();
            char candidate = idx >= 0 ? info.getSource()[curr.getSourceEnd()] : (char) -1;
            if (Surrogate.isHigh(candidate)) {
                if (curr.getInfoType() == DiffInfo.InfoType.MATCH
                        && next.getInfoType() == DiffInfo.InfoType.REPLACE) {
                    curr.setSourceEnd(curr.getSourceEnd() - 1);
                    next.setSourceStart(next.getSourceStart() - 1);
                    curr.setTargetEnd(curr.getTargetStart() - 1);
                    next.setTargetStart(next.getTargetEnd() - 1);
                }
                if (curr.getInfoType() == DiffInfo.InfoType.REPLACE
                        && next.getInfoType() == DiffInfo.InfoType.MATCH) {
                    curr.setSourceEnd(curr.getSourceEnd() + 1);
                    next.setSourceStart(next.getSourceStart() + 1);
                    curr.setTargetEnd(curr.getTargetStart() + 1);
                    next.setTargetStart(next.getTargetEnd() + 1);
                }
            }
        }
    }


    private static DiffInfo computeInfo(List<EditNode> path, char[] source, char[] target) {

        List<EditNode> inserts = new ArrayList<>();
        List<EditNode> deletes = new ArrayList<>();
        List<EditNode> matches = new ArrayList<>();

        path = path.subList(2, path.size());

        DiffInfo info = new DiffInfo(source, target);

        for (EditNode node : path) {
            Op op = node.getOperation();

            if (op == Op.INSERT || op == Op.DELETE) {
                if (op == Op.INSERT) {
                    inserts.add(node);
                }
                if (op == Op.DELETE) {
                    deletes.add(node);
                }
                onInsertionOrDeletion(matches, info);
            }

            if (op == Op.MATCH) {
                matches.add(node);
                onMatch(inserts, deletes, info);
            }
        }
        onInsertionOrDeletion(matches, info);
        onMatch(inserts, deletes, info);

        return info;
    }

    private static void onMatch(List<EditNode> inserts, List<EditNode> deletes, DiffInfo info) {
        if (!inserts.isEmpty() && !deletes.isEmpty()
                && inserts.size() == deletes.size()) {

            int start = deletes.get(0).getSourceIndex();
            int end = deletes.get(deletes.size() - 1).getSourceIndex();
            int startTarget = inserts.get(0).getTargetIndex();
            int endTarget = inserts.get(inserts.size() - 1).getTargetIndex();

            info.addReplacement(start, end, startTarget, endTarget);
            inserts.clear();
            deletes.clear();
        } else if (!inserts.isEmpty()) {
            int start = inserts.get(0).getSourceIndex();
            int end = inserts.get(inserts.size() - 1).getSourceIndex();
            int startTarget = inserts.get(0).getTargetIndex();
            int endTarget = inserts.get(inserts.size() - 1).getTargetIndex();

            info.addInsertion(start, end, startTarget, endTarget);
            inserts.clear();
        } else if (!deletes.isEmpty()) {
            int start = deletes.get(0).getSourceIndex();
            int end = deletes.get(deletes.size() - 1).getSourceIndex();
            int before = deletes.get(0).getTargetIndex();

            info.addDeletion(start, end, before);
            deletes.clear();
        }
    }

    private static void onInsertionOrDeletion(List<EditNode> matches, DiffInfo info) {
        if (!matches.isEmpty()) {
            int start = matches.get(0).getSourceIndex();
            int end = matches.get(matches.size() - 1).getSourceIndex();
            int startTarget = matches.get(0).getTargetIndex();
            int endTarget = matches.get(matches.size() - 1).getTargetIndex();

            info.addMatch(start, end, startTarget, endTarget);
            matches.clear();
        }
    }

    private static List<EditNode> computeEditPath(char[] sourceCP, char[] targetCP) {

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
