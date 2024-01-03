package me.cyrzu.supersql.command;

import org.jetbrains.annotations.NotNull;

public interface CommandBuilder {

    @NotNull
    String build();

}
