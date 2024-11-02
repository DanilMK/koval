package net.smok.utility;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.zip.ZipEntry;

public record Vec2Int(int x, int y) {

    public static final Vec2Int ZERO = new Vec2Int(0, 0);
    public static final Vec2Int UP = new Vec2Int(0, 1);
    public static final Vec2Int DOWN = new Vec2Int(0, -1);
    public static final Vec2Int LEFT = new Vec2Int(-1, 0);
    public static final Vec2Int RIGHT = new Vec2Int(1, 0);


    public Vec2Int subtract(@NotNull Vec2Int subtract) {
        return new Vec2Int(x - subtract.x, y - subtract.y);
    }

    public Vec2Int add(@NotNull Vec2Int add) {
        return new Vec2Int(x + add.x, y + add.y);
    }


    public boolean equals(Vec2Int other) {
        if (other == null) return false;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return MessageFormat.format("[{0},{1}]", x, y);
    }

    @Contract(pure = true)
    public @NotNull String toStringFormat(@NotNull String format) {
        return MessageFormat.format(format, x, y);
    }

    public static Vec2Int fromString(@NotNull String str) {

        String[] pos = str.replace(" ", "").replace("[", "").replace("]", "").split(",");
        if (pos.length < 2) return ZERO;
        try {
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);
            return new Vec2Int(x, y);
        } catch (NumberFormatException ignored) {
            return ZERO;
        }
    }
}
