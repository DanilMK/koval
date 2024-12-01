package net.smok.koval.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.koval.forging.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MaterialData {

    @Nullable private final Identifier parent;
    @NotNull private final ParametersGroup parameters = new ParametersGroup(new HashMap<>());
    @NotNull private final HashMap<String, JsonPrimitive> properties = new HashMap<>();

    public MaterialData() {
        this.parent = null;
    }

    public MaterialData(@Nullable Identifier parent) {
        this.parent = parent;
    }

    public <T, U, R> MaterialData addParameter(Identifier identifier, BiKovalFunction<T, U, R> function, AbstractParameter fist, AbstractParameter second) {
        parameters.add(identifier, FunctionParameter.of(function, fist, second));
        return this;
    }

    public <T, R> MaterialData addParameter(Identifier identifier, MonoKovalFunction<T, R> function, AbstractParameter parameter) {
        parameters.add(identifier, FunctionParameter.of(function, parameter));
        return this;
    }

    public <R> MaterialData addParameter(Identifier identifier, KovalFunction<R> function, AbstractParameter... params) {
        parameters.add(identifier, FunctionParameter.of(function, params));
        return this;
    }
    
    public MaterialData addParameter(Identifier identifier, AbstractParameter parameter) {
        parameters.add(identifier, parameter);
        return this;
    }




    public MaterialData addColor(int color) {
        return addProperty(Values.Parameters.COLOR, "0x" + Integer.toHexString(color).toUpperCase());
    }

    public MaterialData addProperty(Identifier id, boolean property) {
        return addProperty(id.toString(), new JsonPrimitive(property));
    }

    public MaterialData addProperty(Identifier id, Number property) {
        return addProperty(id.toString(), new JsonPrimitive(property));
    }

    public MaterialData addProperty(Identifier id, String property) {
        return addProperty(id.toString(), new JsonPrimitive(property));
    }

    protected MaterialData addProperty(String id, JsonPrimitive property) {
        properties.put(id, property);
        return this;
    }

    public JsonObject toJson() {
        JsonObject material = new JsonObject();
        JsonObject propertiesJson = new JsonObject();
        if (parent != null) material.add(Values.Json.PARENT, new JsonPrimitive(parent.toString()));

        properties.forEach(propertiesJson::add);

        if (!properties.isEmpty()) material.add(Values.Json.PROPERTIES, propertiesJson);
        if (!parameters.isEmpty()) material.add(Values.Json.PARAMETERS, parameters.toJson());

        return material;
    }

}
