package net.smok.koval.forging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.koval.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class PropertyParameter<T> extends AbstractParameter<T> {

    private enum PropertyType {
        MATERIAL, SHAPE
    }

    private @Nullable final Identifier identifier;
    private @NotNull final PropertyType type;


    public static <T> PropertyParameter<T> material(Identifier identifier) { return new PropertyParameter<>(identifier, PropertyType.MATERIAL); }
    public static <T> PropertyParameter<T> shape(Identifier identifier) { return new PropertyParameter<>(identifier, PropertyType.SHAPE); }
    @SuppressWarnings("unchecked") public static <T> PropertyParameter<T> shape() { return (PropertyParameter<T>) SHAPE; }
    @SuppressWarnings("unchecked") public static <T> PropertyParameter<T> material() { return (PropertyParameter<T>) MATERIAL; }
    public static PropertyParameter<?> MATERIAL = new PropertyParameter<>(null, PropertyType.MATERIAL);
    public static PropertyParameter<?> SHAPE = new PropertyParameter<>(null, PropertyType.SHAPE);

    public static <T> PropertyParameter<T> fromString(String typeString) {
        return switch (typeString.toLowerCase()) {
            case "#materials", "#material" -> material();
            case "#shapes", "#shape" -> shape();
            default -> throw Values.Json.exceptionInvalidPropertyType(typeString);
        };
    }

    public static <T> PropertyParameter<T> fromString(String typeString, Identifier identifier) {
        return switch (typeString.toLowerCase()) {
            case "#materials", "#material" -> material(identifier);
            case "#shapes", "#shape" -> shape(identifier);
            default -> throw Values.Json.exceptionInvalidPropertyType(typeString);
        };
    }

    private PropertyParameter(@Nullable Identifier identifier, @NotNull PropertyType type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public Optional<T> get(ParameterPlace context) {
        throw Values.Json.exceptionInitializeValue(context.parameterId());
    }

    @Override
    public Class<T> getValueType() {
        throw Values.Json.exceptionInitializeValue(identifier);
    }



    @Override
    public boolean canAssemble(ParameterPlace context) {
        return false;
    }

    @Override
    public JsonElement toJson(Identifier identifier) {
        if (this.identifier == null || identifier == this.identifier) return new JsonPrimitive("#" + type.toString().toLowerCase());
        else {JsonObject result = new JsonObject();
            result.add(Values.Json.PROPERTY_TYPE, new JsonPrimitive("#" + type.toString().toLowerCase()));
            result.add(Values.Json.ID, new JsonPrimitive(this.identifier.toString()));
            return result;
        }
    }

    @Override
    public AbstractParameter<?> initialize(Identifier identifier, Shape shape, Material material) throws NullPointerException {
        Identifier id = this.identifier != null ? this.identifier : identifier;

        return switch (type) {
            case SHAPE -> {
                if (!shape.getProperties().properties.containsKey(id)) throw new NullPointerException("Cannot find "+ id +" in shape properties");
                yield shape.getProperties().properties.get(id).clone();
            }
            case MATERIAL -> {
                if (!material.properties().properties.containsKey(id)) throw new NullPointerException("Cannot find "+ id +" in material properties");
                yield material.properties().properties.get(id).clone();
            }
        };
    }

    @Override
    public PropertyParameter<T> clone() {
        return new PropertyParameter<>(identifier, type);
    }

    @Override
    public String toString() {
        return "PropertyParameter{" +
                " identifier=" + identifier +
                ", type=" + type +
                '}';
    }
}
