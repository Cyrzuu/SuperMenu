package me.cyrzu.git.supermenu;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Range {

    @Getter
    private final int start;

    private final int end;

    public Range(int start, int end) {
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
    }

    public int getEnd() {
        return end;
    }

    public List<@NotNull Integer> get() {
        return this.getStream().boxed().toList();
    }

    public IntStream getStream() {
        return IntStream.rangeClosed(start, end);
    }

    public static Range of(int start, int end) {
        return new Range(start, end);
    }

}
