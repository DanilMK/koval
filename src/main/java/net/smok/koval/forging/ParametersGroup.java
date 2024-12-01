package net.smok.koval.forging;

import com.google.gson.*;
import net.minecraft.util.Identifier;
import net.smok.Debug;
import net.smok.koval.Material;
import net.smok.koval.Shape;
import net.smok.utility.SavableObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public record ParametersGroup(Map<Identifier, AbstractParameter<?>> parameters) implements SavableObject<ParametersGroup> {

    public static final String WARN_SKIP = "Parameter {0} will be skip.";

    public ParametersGroup(Map<Identifier, AbstractParameter<?>> parameters, Map<Identifier, AbstractParameter<?>> parameters1) {
        this(append(parameters, parameters1));
    }

    public AbstractParameter<?> get(Identifier key) {
        return parameters.get(key);
    }


    @Contract("_, _ -> new")
    public static @NotNull ParametersGroup initialize(@NotNull Shape shape, @NotNull Material material) {
        HashMap<Identifier, AbstractParameter<?>> result = new HashMap<>(shape.getParameters().parameters.size() + material.parameters().parameters.size());

        shape.getParameters().parameters().forEach((identifier, parameter) -> {
            try {

                AbstractParameter<?> initParameter = parameter.initialize(identifier, shape, material);
                result.put(identifier, initParameter);

            } catch (Exception e) {

                Debug.warn(MessageFormat.format(WARN_SKIP, identifier));
                Debug.err(e.toString());
            }
        });
        material.parameters().parameters().forEach((identifier, parameter) -> {
            try {

                Debug.log("Init for " + identifier + " in material");
                AbstractParameter<?> initParameter = parameter.initialize(identifier, shape, material);
                result.put(identifier, initParameter);

            } catch (Exception e) {

                Debug.warn(MessageFormat.format(WARN_SKIP, identifier));
                Debug.err(e.toString());
            }
        });

        return new ParametersGroup(result);
    }

    private static HashMap<Identifier, AbstractParameter<?>> append(Map<Identifier, AbstractParameter<?>> a, Map<Identifier, AbstractParameter<?>> b) {
        HashMap<Identifier, AbstractParameter<?>> result = new HashMap<>();
        a.forEach((identifier, parameter) -> {
            result.put(identifier, parameter.clone());
        });

        b.forEach((identifier, parameter) -> {
            if (result.containsKey(identifier)) result.replace(identifier, parameter.clone());
            else result.put(identifier, parameter.clone());
        });
        return result;
    }


    // Parsing JSON


    @Override
    public ParametersGroup createChild(JsonObject json) {
        return new ParametersGroup(parameters, parse(json));
    }


    private HashMap<Identifier, AbstractParameter<?>> parse(@Nullable JsonObject groupJson) {
        HashMap<Identifier, AbstractParameter<?>> result = new HashMap<>();

        if (groupJson == null) return result;

        for (String key : groupJson.keySet()) {
            Identifier id = new Identifier(key);
            JsonElement element = groupJson.get(key);
            AbstractParameter<?> parameter;
            try {
                ParameterDeserializer deserializer = new ParameterDeserializer();
                parameter = deserializer.deserialize(element, id);
            } catch (JsonParseException e) {
                Debug.warn(MessageFormat.format(WARN_SKIP, id));
                Debug.err(e.toString());
                continue;
            }
            result.put(id, parameter);
        }

        return result;
    }

    @Override
    public String toString() {
        return "ParametersGroup {" +
                "parameters = [" + String.join(", ", parameters.entrySet().stream().map(entry -> entry.getKey()+"="+entry.getValue()).toList()) +
                "]}";
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        parameters.forEach((identifier, parameter) ->
                jsonObject.add(identifier.toString(), parameter.toJson(identifier)));
        return jsonObject;
    }


    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    public void add(Identifier identifier, AbstractParameter<?> parameter) {
        if  (parameters.containsKey(identifier))
             parameters.replace(identifier, parameter);
        else parameters.put(identifier, parameter);
    }
}
