package org.bytediff.engine;

import lombok.Value;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DiffInfo {
    public enum InfoType {
        INSERT, DELETE, REPLACE, MATCH;
    }

    @Value
    public static class Info {
        InfoType infoType;
        Integer sourceStart;
        Integer sourceEnd;
        Integer targetStart;
        Integer targetEnd;
    }

    private final List<Info> info;
    private final Charset charset;
    private final byte[] source;
    private final byte[] target;

    public DiffInfo(Charset charset, byte[] source, byte[] target) {
        this.charset = charset;
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

    public Charset getCharset() {
        return this.charset;
    }

    public byte[] getSource() {
        return this.source;
    }

    public byte[] getTarget() {
        return this.target;
    }
}
