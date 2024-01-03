package me.cyrzu.supersql;

import lombok.Getter;
import me.cyrzu.supersql.column.AbstractColumn;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class SQLTable {

    @Getter
    @NotNull
    private final String name;

    @NotNull
    private final Map<String, AbstractColumn> columns;

    private SQLTable(@NotNull String name, @NotNull Map<String, AbstractColumn> columns) {
        this.name = name;
        this.columns = columns;
    }


    @NotNull
    public PreparedStatement getCreateStatement(@NotNull Connection connection) {
        try {
            return connection.prepareStatement(getCreateCommand());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public String getCreateCommand() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + " (");

        final List<AbstractColumn> values = List.copyOf(columns.values());
        for (int i = 0; i < values.size(); i++) {
            final AbstractColumn column = values.get(i);
            builder.append(column.create());

            if(i != values.size() - 1) {
                builder.append(", ");
            }
        }

        builder.append(");");
        return builder.toString();
    }

    public static Builder builder(@NotNull String name) {
        return new Builder(name);
    }

    public static class Builder {

        @NotNull
        private final String name;

        @NotNull
        private final Map<String, AbstractColumn> columns;

        private Builder(@NotNull String name) {
            this.name = name;
            this.columns = new LinkedHashMap<>();
        }

        public Builder add(@NotNull AbstractColumn... columns) {
            Arrays.stream(columns).forEach(this::add);
            return this;
        }

        public Builder add(@NotNull AbstractColumn column) {
            columns.put(column.getName(), column);
            return this;
        }

        public @NotNull SQLTable build() {
            return new SQLTable(name, columns);
        }

    }

}
