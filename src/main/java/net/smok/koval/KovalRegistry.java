package net.smok.koval;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.smok.Values;
import net.smok.koval.forging.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class KovalRegistry<T> {


    public static final RegistryKey<Registry<KovalFunction<?>>> FUNCTION_KEY = RegistryKey.ofRegistry(new Identifier(Values.MOD_ID, "function"));

    public static final RegistryKey<Registry<Material>> MATERIAL_KEY = RegistryKey.ofRegistry(new Identifier(Values.MOD_ID, "material"));
    public static final RegistryKey<Registry<Shape>> SHAPE_KEY = RegistryKey.ofRegistry(new Identifier(Values.MOD_ID, "shape"));
    public static final RegistryKey<Registry<Part>> PART_KEY = RegistryKey.ofRegistry(new Identifier(Values.MOD_ID, "part"));


    public static final Registry<KovalFunction<?>> FUNCTIONS = new SimpleRegistry<>(FUNCTION_KEY, Lifecycle.stable(), null);

    public static final DefaultedRegistry<Material> MATERIALS = createDefaultedRegistry(MATERIAL_KEY, Material.BASE_MATERIAL);
    public static final DefaultedRegistry<Shape> SHAPES = createDefaultedRegistry(SHAPE_KEY, Shape.BASE_SHAPE);
    public static final Registry<Part> PARTS = new SimpleRegistry<>(PART_KEY, Lifecycle.stable(), null);


    private static <T> DefaultedRegistry<T> createDefaultedRegistry(RegistryKey<Registry<T>> key, T defaultValue) {
        DefaultedRegistry<T> r = new DefaultedRegistry<>("base", key, Lifecycle.stable(), null);
        r.set(0, RegistryKey.of(key, new Identifier("base")), defaultValue, Lifecycle.stable());
        return r;
    }


    private final HashMap<Identifier, T> map = new HashMap<>();
    private @Nullable final T defaultItem;

    public KovalRegistry() {
        defaultItem = null;
    }

    public KovalRegistry(@Nullable T defaultItem) {
        this.defaultItem = defaultItem;
    }

    public <V extends T> V register(KovalRegistry<T> registry, Identifier identifier, V value) {

        map.put(identifier, value);
        return value;
    }

    public <V extends T> Pair<Identifier, V> registerAndGetId(Identifier identifier, V value) {
        map.put(identifier, value);
        return new Pair<>(identifier, value);
    }

    public T get(@NotNull Identifier identifier) {
        return map.getOrDefault(identifier, defaultItem);
    }

    public boolean has(@NotNull Identifier identifier) {
        return map.containsKey(identifier);
    }


    public HashMap<Identifier, T> getAll() {
        return map;
    }
}
