package me.cyrzu.supersql.column;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractColumn {

    @Getter
    @NotNull
    protected final String name;

    @Getter
    private boolean primaryKey = false;

    @Getter
    private boolean unique = false;

    @Getter
    private boolean notNull = false;

    public AbstractColumn(@NotNull String name) {
        this.name = name;
    }

    public final AbstractColumn primaryKey() {
        this.primaryKey = true;
        this.unique = false;
        this.notNull = false;
        return this;
    }

    public final AbstractColumn unique() {
        this.unique = true;
        this.primaryKey = false;
        return this;
    }

    public final AbstractColumn notNull() {
        this.notNull = true;
        this.primaryKey = false;
        return this;
    }

    public abstract String create();

}
