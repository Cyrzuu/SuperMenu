package me.cyrzu.supermenu;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Range {

    private final int start;

    private final int end;

    public Range(int start, int end) {
        this.start = Math.max(0, start);
        this.end = Math.max(end, start);
    }

    public int getStart() {
        return Math.max(start, end);
    }

    public int getEnd() {
        return Math.min(start, end);
    }

    public Set<@NotNull Integer> get() {
        return IntStream.rangeClosed(getStart(), getEnd()).boxed().collect(Collectors.toSet());
    }

}
