package net.smok.koval.forging;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.koval.Part;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Optional;

public class PrimitiveParameter<T> extends AbstractParameter<T> {

    protected final @NotNull T value;

    @Contract("_ -> new")
    public static @NotNull PrimitiveParameter<Integer> of(int value) { return new PrimitiveParameter<>(value); }
    @Contract("_ -> new")
    public static @NotNull PrimitiveParameter<Float> of(float value) { return new PrimitiveParameter<>(value); }
    @Contract("_ -> new")
    public static @NotNull PrimitiveParameter<Number> of(Number value) { return new PrimitiveParameter<>(value); }
    @Contract("_ -> new")
    public static @NotNull PrimitiveParameter<String> of(String value) { return new PrimitiveParameter<>(value); }
    @Contract("_ -> new")
    public static @NotNull PrimitiveParameter<Boolean> of(boolean value) { return new PrimitiveParameter<>(value); }

    protected PrimitiveParameter(@NotNull T value) {
        this.value = value;
    }

    @Override
    public Optional<T> get(ParameterPlace context) {
        return Optional.of(value);
    }

    @Override
    public Class<T> getValueType() {
        //noinspection unchecked
        return (Class<T>) value.getClass();
    }

    @Override
    public JsonElement toJson(Identifier identifier) {
        if (value.getClass().isAssignableFrom(Number.class)) return new JsonPrimitive((Number) value);
        if (value.getClass().isAssignableFrom(String.class)) return new JsonPrimitive((String) value);
        if (value.getClass().isAssignableFrom(Boolean.class)) return new JsonPrimitive((Boolean) value);
        return JsonNull.INSTANCE;
    }

    public int toColor() {
        if (value instanceof String hexString) return Integer.decode(hexString);
        if (value instanceof Number number) return number.intValue();
        return 0;
    }


    @Override
    public String toString() {
        return value.toString();
    }


    @Override
    public AbstractParameter<T> clone() {
        return new PrimitiveParameter<>(value);
    }
}
