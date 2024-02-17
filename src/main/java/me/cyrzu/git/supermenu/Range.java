package me.cyrzu.git.supermenu;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Range {

    private final int start;

    private final int end;

    public Range(int start, int end) {
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
    }

    public int getStart() {
        return Math.min(start, end);
    }

    public int getEnd() {
        return Math.max(start, end);
    }

    public List<@NotNull Integer> get() {
        return IntStream.rangeClosed(getStart(), getEnd()).boxed().toList();
    }

}
