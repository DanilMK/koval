package net.smok.koval.forging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;
import net.smok.Debug;
import net.smok.koval.KovalRegistry;
import net.smok.koval.AbstractParameter;
import net.smok.utility.SavableObject;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.Nullable;

import java.rmi.UnexpectedException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public record ParametersGroup(Map<Identifier, AbstractParameter> parameters) implements SavableObject<ParametersGroup> {

    public static final String EXCEPTION_UNEXPECTED_PARAMETER = "Unexpected parameter exception. ID: {0}, Expected type: {1}, provided parameter: {2}.";
    public static final String EXCEPTION_INVALID_PARAMETER_TYPE = "Invalid parameter type. Expected type: {0}, provided parameter: {1}";
    public static final String UNKNOWN_FUNCTION_TYPE = "Unknown function type: {0}.";
    public static final String WARN_SKIP = "Parameter {0} will be skip.";
    public static final String EXCEPTION_INVALID_PARAMETERS_AMOUNT = "Invalid parameters amount. Expected: {0}, provided: {1}";

    public ParametersGroup(Map<Identifier, AbstractParameter> parameters, Map<Identifier, AbstractParameter> parameters1) {
        this(append(parameters, parameters1));
    }

    public ParametersGroup(Map<Identifier, AbstractParameter> parameters, Map<Identifier, AbstractParameter> parameters1, Map<Identifier, AbstractParameter> parameters2) {
        this(append(parameters, parameters1, parameters2));
    }

    public AbstractParameter get(Identifier key) {
        return parameters.get(key);
    }

    private static HashMap<Identifier, AbstractParameter> append(Map<Identifier, AbstractParameter> a, Map<Identifier, AbstractParameter> b) {
        HashMap<Identifier, AbstractParameter> result = new HashMap<>(a);
        b.forEach((identifier, parameter) -> {
            if (result.containsKey(identifier)) result.replace(identifier, parameter);
            else result.put(identifier, parameter);
        });
        return result;
    }

    private static HashMap<Identifier, AbstractParameter> append(Map<Identifier, AbstractParameter> a, Map<Identifier, AbstractParameter> b, Map<Identifier, AbstractParameter> c) {
        HashMap<Identifier, AbstractParameter> result = new HashMap<>(a);
        b.forEach((identifier, parameter) -> {
            if (result.containsKey(identifier)) result.replace(identifier, parameter);
            else result.put(identifier, parameter);
        });
        c.forEach((identifier, parameter) -> {
            if (result.containsKey(identifier)) result.replace(identifier, parameter);
            else result.put(identifier, parameter);
        });
        return result;
    }


    // Parsing JSON


    @Override
    public ParametersGroup createChild(JsonObject json) {
        return new ParametersGroup(parameters, parse(json));
    }


    private HashMap<Identifier, AbstractParameter> parse(@Nullable JsonObject groupJson) {
        HashMap<Identifier, AbstractParameter> result = new HashMap<>();

        if (groupJson == null) return result;

        for (String key : groupJson.keySet()) {
            Identifier id = new Identifier(key);
            JsonElement element = groupJson.get(key);
            AbstractParameter parameter;
            try {
                if (element.isJsonPrimitive()) parameter = parseParameter(element.getAsJsonPrimitive(), id, null);
                else if (element.isJsonObject()) parameter = parseFunction(element.getAsJsonObject(), id);
                else continue;
            } catch (Exception e) {
                Debug.warn(MessageFormat.format(WARN_SKIP, id));
                Debug.err(e.toString());
                continue;
            }
            result.put(id, parameter);
        }

        return result;
    }

    private AbstractParameter parseFunction(JsonObject objectParameter, Identifier identifier) throws Exception {
        if (objectParameter.has("function_id") && objectParameter.has("parameters")) {
            Identifier functionId = new Identifier(objectParameter.get("function_id").getAsString());
            JsonArray paramsJson = objectParameter.getAsJsonArray("parameters");

            if (!KovalRegistry.FUNCTIONS.has(functionId))
                throw new Exception(MessageFormat.format(UNKNOWN_FUNCTION_TYPE, functionId));

            KovalFunction<?> function = KovalRegistry.FUNCTIONS.get(functionId);
            if (function.innerParametersCount() != paramsJson.size())
                throw new Exception(MessageFormat.format(EXCEPTION_INVALID_PARAMETERS_AMOUNT, function.innerParametersCount(), paramsJson.size()));

            AbstractParameter[] params = new AbstractParameter[paramsJson.size()];
            for (int i = 0; i < paramsJson.size(); i++) {
                JsonElement element = paramsJson.get(i);
                if (element.isJsonPrimitive()) params[i] = parseParameter(element.getAsJsonPrimitive(), identifier, function.getParametersTypes()[i]);
                if (element.isJsonObject()) params[i] = parseParameter(element.getAsJsonObject(), identifier, function.getParametersTypes()[i]);
            }

            return new KovalFunctionParameter(function.returnType(), functionId, function, params);

        } else return parseParameter(objectParameter, identifier, null);
    }

    private AbstractParameter parseParameter(JsonObject objectParameter, Identifier identifier, Class<?> expectedType) throws Exception {
        if (objectParameter.has("id")) identifier = new Identifier(objectParameter.get("id").getAsString());
        if (objectParameter.has("identifier")) identifier = new Identifier(objectParameter.get("identifier").getAsString());

        if (objectParameter.has("x") && objectParameter.has("y")) {
            Vec2Int pointer = new Vec2Int(objectParameter.get("x").getAsInt(), objectParameter.get("y").getAsInt());
            return new PointerParameter(expectedType, pointer, identifier);
        }

        if (objectParameter.has("parameter") && objectParameter.get("parameter").isJsonPrimitive())
            return parseParameter(objectParameter.getAsJsonPrimitive("parameter"), identifier, expectedType);

        throw new UnexpectedException(MessageFormat.format(EXCEPTION_UNEXPECTED_PARAMETER, identifier, expectedType, objectParameter));
    }

    private AbstractParameter parseParameter(JsonPrimitive primitiveParameter, Identifier identifier, Class<?> expectedType) throws Exception {
        if (primitiveParameter.isBoolean()) {
            if (expectedType == null || expectedType.isAssignableFrom(Boolean.class)) return new BaseParameter(primitiveParameter.getAsBoolean());
            else throw new Exception(MessageFormat.format(EXCEPTION_INVALID_PARAMETER_TYPE, expectedType, primitiveParameter));
        }

        if (primitiveParameter.isNumber()) {
            if (expectedType == null || expectedType.isAssignableFrom(Number.class)) return new BaseParameter(primitiveParameter.getAsNumber());
            else throw new Exception(MessageFormat.format(EXCEPTION_INVALID_PARAMETER_TYPE, expectedType, primitiveParameter));
        }

        if (primitiveParameter.isString()) {
            if (primitiveParameter.getAsString().startsWith("#")) {
                String str = primitiveParameter.getAsString().toLowerCase();
                switch (str) {
                    case "#materials":
                    case "#material":
                        return new PropertyParameter(expectedType == null ? Object.class : expectedType,
                                identifier, PropertyParameter.PropertyType.MATERIAL);

                    case "#shapes":
                    case "#shape":
                        return new PropertyParameter(expectedType == null ? Object.class : expectedType,
                                identifier, PropertyParameter.PropertyType.SHAPE);
                }
                Vec2Int pointer = Vec2Int.fromString(str);
                if (!pointer.equals(Vec2Int.ZERO)) return new PointerParameter(expectedType, pointer, identifier);
            }
            if (expectedType == null || expectedType.isAssignableFrom(String.class)) return new BaseParameter(primitiveParameter.getAsString());
        }

        throw new UnexpectedException(MessageFormat.format(EXCEPTION_UNEXPECTED_PARAMETER, identifier, expectedType, primitiveParameter));
    }

    @Override
    public String toString() {
        return "ParametersGroup {" +
                "parameters = [" + String.join(", ", parameters.entrySet().stream().map(entry -> entry.getKey()+"="+entry.getValue()).toList()) +
                "]}";
    }
}
