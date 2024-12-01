package net.smok.koval.forging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AssembleRecipe(@NotNull AbstractParameter<Boolean> condition, @NotNull Item result) {

    public static final String PARAMETER = "parameter";
    public static final String RESULT = "result";

    public boolean test(ParameterPlace place) {
        Optional<Boolean> o = condition.get(place);
        return o.isPresent() && o.get();
    }

    public @NotNull JsonObject toJson() {
        JsonObject json = new JsonObject();
        Identifier id = Registry.ITEM.getId(result);
        json.add(PARAMETER, condition.toJson(id));
        json.addProperty(RESULT, id.toString());
        return json;
    }

    @Contract("_ -> new")
    public static @NotNull AssembleRecipe fromJson(@NotNull JsonElement json) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            String name = json.getAsJsonPrimitive().getAsString();
            Optional<Item> orEmpty = Registry.ITEM.getOrEmpty(new Identifier(name));

            if (orEmpty.isEmpty()) throw new JsonParseException("Unknown item with id "+name);
            return new AssembleRecipe(PrimitiveParameter.of(true), orEmpty.get());

        } else if (json.isJsonObject() && json.getAsJsonObject().has(RESULT) && json.getAsJsonObject().has(PARAMETER)) {
            JsonElement resultElement = json.getAsJsonObject().get(RESULT);
            JsonElement conditionElement = json.getAsJsonObject().get(PARAMETER);

            if (resultElement.isJsonPrimitive() && resultElement.getAsJsonPrimitive().isString()) {
                String name = resultElement.getAsJsonPrimitive().getAsString();
                Identifier id = new Identifier(name);
                Optional<Item> orEmpty = Registry.ITEM.getOrEmpty(id);

                if (orEmpty.isEmpty()) throw new JsonParseException("Unknown item with id "+name);

                ParameterDeserializer deserializer = new ParameterDeserializer();
                AbstractParameter<Boolean> parameter = deserializer.deserialize(conditionElement, id, Boolean.class);

                return new AssembleRecipe(parameter, orEmpty.get());
            }
        }
        throw new JsonParseException("Json must be primitive with item result or object with 'parameter' and item 'result'");
    }
}
