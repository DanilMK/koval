package net.smok.koval.forging;

import com.google.gson.*;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.koval.KovalRegistry;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParameterDeserializer {


    public AbstractParameter<?> deserialize(JsonElement json, Identifier identifier) throws JsonParseException {

        if (isFunctionParameter(json)) {
            return toFunction(json.getAsJsonObject(), identifier);
        } else if (isPointerParameter(json)) {
            return deserializePointer(json.getAsJsonObject());
        } else if (isPropertyParameter(json)) {
            if (json.isJsonObject()) return deserializeProperty(json.getAsJsonObject());
            else return deserializeProperty(json.getAsJsonPrimitive());
        } else if (isPrimitiveParameter(json)) {
            return deserializePrimitive(json.getAsJsonPrimitive());
        }

        throw Values.Json.exceptionUnexpectedParameter(identifier, json, null);
    }

    public <T> AbstractParameter<T> deserialize(JsonElement json, Identifier identifier, @NotNull Class<T> expectedType) throws JsonParseException {
        if (isFunctionParameter(json)) {
            return toFunction(json.getAsJsonObject(), identifier, expectedType);
        } else if (isPointerParameter(json)) {
            return deserializePointer(json.getAsJsonObject(), expectedType);
        } else if (isPropertyParameter(json)) {
            if (json.isJsonObject()) return (AbstractParameter<T>) deserializeProperty(json.getAsJsonObject());
            else return (AbstractParameter<T>) deserializeProperty(json.getAsJsonPrimitive());
        } else if (isPrimitiveParameter(json)) {
            return deserializePrimitive(json.getAsJsonPrimitive(), expectedType);
        }

        throw Values.Json.exceptionUnexpectedParameter(identifier, json, expectedType);
    }

    public boolean isPrimitiveParameter(JsonElement json) {
        return json.isJsonPrimitive();
    }

    public boolean isPropertyParameter(JsonElement json) {
        if (json.isJsonObject() && json.getAsJsonObject().has(Values.Json.PROPERTY_TYPE)) return true;
        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            return primitive.isString() && primitive.getAsString().startsWith("#");
        }
        return false;
    }

    public boolean isPointerParameter(JsonElement json) {
        if (!json.isJsonObject()) return false;
        JsonObject jsonObject = json.getAsJsonObject();
        return  (jsonObject.has("x") && jsonObject.has("y")
                && jsonObject.get("x").isJsonPrimitive() && jsonObject.get("y").isJsonPrimitive());

    }

    public boolean isFunctionParameter(JsonElement json) {
        return json.isJsonObject() && json.getAsJsonObject().has(Values.Json.FUNCTION_ID) &&
                json.getAsJsonObject().get(Values.Json.FUNCTION_ID).isJsonPrimitive();
    }


    @Contract("_ -> new")
    public @NotNull PrimitiveParameter<?> deserializePrimitive(JsonPrimitive json) {

        if (json.isBoolean()) return PrimitiveParameter.of(json.getAsBoolean());
        if (json.isNumber()) return PrimitiveParameter.of(json.getAsNumber());
        if (json.isString()) return PrimitiveParameter.of(json.getAsString());

        throw Values.Json.exceptionInvalidPrimitive(json);
    }


    @SuppressWarnings("unchecked")
    @Contract("_, _ -> new")
    public <T> @NotNull PrimitiveParameter<T> deserializePrimitive(JsonPrimitive json, @NotNull Class<T> type) {
        if (Boolean.TYPE.isAssignableFrom(type) && json.isBoolean()) return (PrimitiveParameter<T>) PrimitiveParameter.of(json.getAsBoolean());
        if (Number.class.isAssignableFrom(type) && json.isNumber()) return (PrimitiveParameter<T>) PrimitiveParameter.of(json.getAsNumber());
        if (String.class.isAssignableFrom(type) && json.isString()) return (PrimitiveParameter<T>) PrimitiveParameter.of(json.getAsString());
        throw Values.Json.exceptionInvalidParameterType(type, json.toString());
    }



    @Contract("_ -> new")
    public @NotNull PropertyParameter<?> deserializeProperty(@NotNull JsonObject json) {

        Identifier id = getIdentifier(json);
        String typeString = json.get(Values.Json.PROPERTY_TYPE).getAsJsonPrimitive().getAsString();

        if (id != null) return PropertyParameter.fromString(typeString, id);
        return PropertyParameter.fromString(typeString);
    }


    @Contract("_ -> new")
    public @NotNull PropertyParameter<?> deserializeProperty(@NotNull JsonPrimitive json) {
        return PropertyParameter.fromString(json.getAsString());
    }


    @Contract("_ -> new")
    public @NotNull PointerParameter<?> deserializePointer(@NotNull JsonObject json) {
        return PointerParameter.of(new Vec2Int(json.get("x").getAsInt(), json.get("y").getAsInt()), getIdentifier(json));
    }

    @Contract("_, _ -> new")
    public <T> @NotNull PointerParameter<T> deserializePointer(@NotNull JsonObject json, @NotNull Class<T> expectedType) {
        return PointerParameter.of(expectedType, new Vec2Int(json.get("x").getAsInt(), json.get("y").getAsInt()), getIdentifier(json));
    }


    @Contract("_, _ -> new")
    public @NotNull FunctionParameter<?> toFunction(@NotNull JsonObject json, Identifier identifier) {

        Identifier functionId = new Identifier(json.get(Values.Json.FUNCTION_ID).getAsString());
        KovalFunction<?> function = KovalRegistry.FUNCTIONS.get(functionId);

        // Check function
        if (function == null) throw Values.Json.exceptionUnknownFunctionType(functionId);
        int innerParametersCount = function.innerParametersCount();


        // Function without params
        if (innerParametersCount == 0)
            return FunctionParameter.of(function);


        // Function with params
        AbstractParameter<?>[] params = collectFunctionParams(json, identifier, innerParametersCount, function);
        return FunctionParameter.of(function, params);
    }

    @Contract("_, _, _ -> new")
    public <T> @NotNull FunctionParameter<T> toFunction(@NotNull JsonObject json, Identifier identifier, @NotNull Class<T> expectedType) {

        Identifier functionId = new Identifier(json.get(Values.Json.FUNCTION_ID).getAsString());
        KovalFunction<?> function = KovalRegistry.FUNCTIONS.get(functionId);

        // Check function
        if (function == null) throw Values.Json.exceptionUnknownFunctionType(functionId);
        int innerParametersCount = function.innerParametersCount();
        if (function.returnType() != expectedType) throw Values.Json.exceptionInvalidFunctionType(expectedType, function.returnType());
        @SuppressWarnings("unchecked")
        KovalFunction<T> typedFunction = (KovalFunction<T>) function;

        // Function without params
        if (innerParametersCount == 0)
            return FunctionParameter.of(typedFunction);// (new FunctionParameter<>(function, new AbstractParameter[0]));

        // Function with params
        AbstractParameter<?>[] params = collectFunctionParams(json, identifier, innerParametersCount, typedFunction);
        return FunctionParameter.of(typedFunction, params);
    }

    private static AbstractParameter<?> @NotNull [] collectFunctionParams(@NotNull JsonObject json, Identifier identifier, int innerParametersCount, KovalFunction<?> function) {
        JsonArray paramsArray = json.get(Values.Json.PARAMETERS).getAsJsonArray();

        if (innerParametersCount != paramsArray.size()) throw Values.Json.exceptionInvalidParametersAmount(innerParametersCount, paramsArray.size());
        AbstractParameter<?>[] params = new AbstractParameter[innerParametersCount];

        Class<?>[] parametersTypes = function.getParametersTypes();
        for (int i = 0; i < parametersTypes.length; i++) {

            ParameterDeserializer parameterDeserializer = new ParameterDeserializer();
            params[i] = parameterDeserializer.deserialize(paramsArray.get(i), identifier, parametersTypes[i]);
        }
        return params;
    }



    private static @Nullable Identifier getIdentifier(@NotNull JsonObject json) {
        if (json.has(Values.Json.ID) && json.get(Values.Json.ID).isJsonPrimitive())
            return new Identifier(json.getAsJsonPrimitive(Values.Json.ID).getAsString());

        if (json.has(Values.Json.ID1) && json.get(Values.Json.ID1).isJsonPrimitive())
            return new Identifier(json.getAsJsonPrimitive(Values.Json.ID1).getAsString());
        return null;
    }
}
