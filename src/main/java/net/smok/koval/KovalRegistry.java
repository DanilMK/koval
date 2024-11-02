package net.smok.koval;

import net.minecraft.util.Identifier;
import net.smok.koval.forging.ConditionFactory;
import net.smok.koval.forging.KovalFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class KovalRegistry<T> {

    public static final KovalRegistry<KovalFunction<?>> FUNCTIONS = new KovalRegistry<>();
    public static final KovalRegistry<ConditionFactory> CONDITIONS = new KovalRegistry<>();

    public static final KovalRegistry<Material> MATERIALS = new KovalRegistry<>(Material.BASE_MATERIAL);
    public static final KovalRegistry<Shape> SHAPES = new KovalRegistry<>(Shape.BASE_SHAPE);
    public static final KovalRegistry<Part> PARTS = new KovalRegistry<>();



    private final HashMap<Identifier, T> map = new HashMap<>();
    private @Nullable final T defaultItem;

    public KovalRegistry() {
        defaultItem = null;
    }

    public KovalRegistry(@Nullable T defaultItem) {
        this.defaultItem = defaultItem;
    }

    public <V extends T> V register(Identifier identifier, V value) {
        map.put(identifier, value);
        return value;
    }

    public T get(@NotNull Identifier identifier) {
        return map.getOrDefault(identifier, defaultItem);
    }

    public boolean has(@NotNull Identifier identifier) {
        return map.containsKey(identifier);
    }

    public static void clearRegistrations() {
        MATERIALS.map.clear();
        SHAPES.map.clear();
        PARTS.map.clear();
    }

    public HashMap<Identifier, T> getAll() {
        return map;
    }
}
