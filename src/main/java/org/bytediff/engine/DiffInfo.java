package org.bytediff.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DiffInfo {
    public enum InfoType {
        INSERT, DELETE, REPLACE, MATCH;
    }

    @Data
    @AllArgsConstructor
    public static class Info {
        private InfoType infoType;
        private Integer sourceStart;
        private Integer sourceEnd;
        private Integer targetStart;
        private Integer targetEnd;
    }

    private final List<Info> info;
    private final char[] source;
    private final char[] target;

    public DiffInfo(char[] source, char[] target) {
        this.source = source;
        this.target = target;
        this.info = new ArrayList<>();
    }

    public void addInsertion(int start, int end, int targetStart, int targetEnd) {
        info.add(
                new Info(InfoType.INSERT, start, end, targetStart, targetEnd));
    }

    public void addDeletion(int start, int end, int before) {
        info.add(
                new Info(InfoType.DELETE, start, end, before, before));
    }

    public void addReplacement(int start, int end, int targetStart, int targetEnd) {
        info.add(
                new Info(InfoType.REPLACE, start, end, targetStart, targetEnd));
    }

    public void addMatch(int start, int end, int targetStart, int targetEnd) {
        info.add(
                new Info(InfoType.MATCH, start, end, targetStart, targetEnd));
    }

    public List<Info> getInfo() {
        return this.info;
    }


    public char[] getSource() {
        return this.source;
    }

    public char[] getTarget() {
        return this.target;
    }
}
