package net.smok.koval.forging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Optional;

public class PointerParameter<T> extends AbstractParameter<T> {

    public final Class<T> valueType;
    private final Vec2Int pointer;
    private final @Nullable Identifier identifier;

    @Contract("_, _ -> new")
    public static @NotNull <T> PointerParameter<T> of(Class<T> valueType, Vec2Int vec2Int) {
        return new PointerParameter<>(valueType, vec2Int, null);
    }

    @Contract("_ -> new")
    public static @NotNull PointerParameter<?> of(Vec2Int vec2Int) {
        return new PointerParameter<>(Object.class, vec2Int, null);
    }

    @Contract("_, _ -> new")
    public static @NotNull PointerParameter<?> of(Vec2Int vec2Int, @Nullable Identifier identifier) {
        return new PointerParameter<>(Object.class, vec2Int, identifier);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull <T> PointerParameter<T> of(Class<T> type, Vec2Int vec2Int, @Nullable Identifier identifier) {
        return new PointerParameter<>(type, vec2Int, identifier);
    }

    protected PointerParameter(Class<T> valueType, Vec2Int pointer, @Nullable Identifier identifier) {
        this.valueType = valueType;
        this.pointer = pointer;
        this.identifier = identifier;
    }

    @Override
    public Optional<T> get(ParameterPlace context) {
        Identifier id = identifier != null ? identifier : context.parameterId();

        Optional<ParameterPlace> kovalContext = context.moveTo(pointer);

        if (kovalContext.isPresent()) {
            Optional<AbstractParameter<T>> parameter = kovalContext.get().getParameter(id,  valueType);
            if (parameter.isPresent()) {
                return parameter.get().get(kovalContext.get());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean canAssemble(ParameterPlace context) {
        Identifier id = identifier != null ? identifier : context.parameterId();

        Optional<ParameterPlace> kovalContext = context.moveTo(pointer);

        if (kovalContext.isPresent()) {
            Optional<AbstractParameter<T>> parameter = kovalContext.get().getParameter(id,  valueType);
            return parameter.isPresent();
        }
        return false;
    }

    @Override
    public Class<T> getValueType() {
        return valueType;
    }

    @Override
    public Text getText(ParameterPlace place) {
        Optional<T> value = get(place);
        Identifier id = identifier != null ? identifier : place.parameterId();
        return value.isPresent() ? toText(id, value) :
                toText(new Identifier("base:arrow"), pointer);
    }


    @Override
    public String toString() {
        return "Pointer{" + identifier + "=" + pointer + " as " +valueType.getTypeName() + "}";
    }


    @Override
    public Vec2Int[] getPointers() {
        return new Vec2Int[] {pointer};
    }

    @Override
    public JsonElement toJson(Identifier identifier) {
        JsonObject object = new JsonObject();

        object.addProperty("x", pointer.x());
        object.addProperty("y", pointer.y());
        if (this.identifier != null && this.identifier != identifier) object.addProperty(Values.Json.ID, this.identifier.toString());

        return object;
    }


    @Override
    public PointerParameter<T> clone() {
        return new PointerParameter<>(valueType, pointer, identifier);
    }
}
