package me.cyrzu.supersql.column;

import org.jetbrains.annotations.NotNull;

public class VarcharColumn extends AbstractColumn {

    private final int max;

    public VarcharColumn(@NotNull String name, int max) {
        super(name);
        this.max = Math.max(1, max);
    }

    @Override
    public String create() {
        StringBuilder builder = new StringBuilder(name + " VARCHAR(%s) CHARSET utf8".formatted(max));
        if(isPrimaryKey()) {
            builder.append(" PRIMARY KEY");
        } else {
            if(isUnique()) {
                builder.append(" UNIQUE");
            }
            if(isNotNull()) {
                builder.append(" NOT NULL");
            }
        }

        return builder.toString();
    }


}
