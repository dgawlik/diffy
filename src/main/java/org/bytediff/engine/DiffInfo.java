package org.bytediff.engine;

import lombok.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DiffInfo {

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

    @Getter private final List<Info> info;
    @Getter private final char[] source;
    @Getter private final char[] target;

    DiffInfo(char[] source, char[] target, List<Info> info) {
        this.source = source;
        this.target = target;
        this.info = info;
    }

    public List<String> getInserts() {
        return info.stream().filter(byType(InfoType.INSERT))
                .map(o -> constructString(target, o.getTargetStart(), o.getTargetEnd()))
                .collect(Collectors.toList());
    }

    public List<String> getDeletions() {
        return info.stream().filter(byType(InfoType.DELETE))
                .map(o -> constructString(source, o.getSourceStart(), o.getSourceEnd()))
                .collect(Collectors.toList());
    }

    public List<String> getReplacements() {
        return info.stream().filter(byType(InfoType.REPLACE))
                .map(o -> constructString(target, o.getTargetStart(), o.getTargetEnd()))
                .collect(Collectors.toList());
    }

    public List<String> getMatches() {
        return info.stream().filter(byType(InfoType.MATCH))
                .map(o -> constructString(source, o.getSourceStart(), o.getSourceEnd()))
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

    void addInsertion(int start, int end, int targetStart, int targetEnd) {
        info.add(
                new Info(InfoType.INSERT, start, end, targetStart, targetEnd));
    }

    void addDeletion(int start, int end, int before) {
        info.add(
                new Info(InfoType.DELETE, start, end, before, before));
    }

    void addReplacement(int start, int end, int targetStart, int targetEnd) {
        info.add(
                new Info(InfoType.REPLACE, start, end, targetStart, targetEnd));
    }

    void addMatch(int start, int end, int targetStart, int targetEnd) {
        info.add(
                new Info(InfoType.MATCH, start, end, targetStart, targetEnd));
    }

    private Predicate<Info> byType(InfoType type) {
        return e -> e.getInfoType() == type;
    }

    private String constructString(char[] arr, int start, int end) {
        return new String(Arrays.copyOfRange(arr, start, end + 1));
    }
}
