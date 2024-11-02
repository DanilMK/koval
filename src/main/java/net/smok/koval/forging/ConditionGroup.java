package net.smok.koval.forging;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.smok.Debug;
import net.smok.koval.KovalRegistry;
import net.smok.utility.SavableObject;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.NotNull;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public record ConditionGroup(Logic logic, @NotNull Map<Vec2Int, Condition> conditions) implements SavableObject<ConditionGroup> {

    private static final String KEY_TYPE = "type";
    private static final String KEY_VALUE = "value";
    private static final String KEY_LOGIC = "logic";
    private static final String KEY_CONDITIONS = "conditions";


    public static final ConditionGroup EMPTY = new ConditionGroup(Logic.OR, new HashMap<>());


    public boolean test(Function<Vec2Int, ItemStack> partGetter) {
        if (conditions.isEmpty()) return true;
        return switch (logic) {
            case OR -> conditions.entrySet().stream().anyMatch(entry ->
                    entry.getValue().predicate.test(partGetter.apply(entry.getKey())));

            case AND -> conditions.entrySet().stream().allMatch(entry ->
                            entry.getValue().predicate.test(partGetter.apply(entry.getKey())));
        };
    }

    @Override
    public ConditionGroup createChild(JsonObject json) {
        if (json == null) return new ConditionGroup(Logic.OR, new HashMap<>(conditions));

        ConditionGroup.Logic logic =
                ConditionGroup.Logic.valueOf(JsonHelper.getString(json, KEY_LOGIC, "or").toUpperCase());
        if (!json.has(KEY_CONDITIONS)) return new ConditionGroup(logic, conditions);

        HashMap<Vec2Int, ConditionGroup.Condition> map =
                parseConditionMap(json.get(KEY_CONDITIONS).getAsJsonObject(), new HashMap<>(conditions));
        return new ConditionGroup(logic, map);

    }

    public enum Logic {
        AND, OR
    }

    public record Condition(Predicate<ItemStack> predicate) {}




    private HashMap<Vec2Int, ConditionGroup.Condition> parseConditionMap(JsonObject map, HashMap<Vec2Int, Condition> fill) {
        for (String key : map.keySet()) {
            Vec2Int pointer = Vec2Int.fromString(key);
            ConditionGroup.Condition condition;
            try {
                condition = parseOnlyCondition(map.getAsJsonObject(key));
            } catch (InvalidTargetObjectTypeException e) {
                Debug.err(e.toString());
                continue;
            }
            fill.put(pointer, condition);
        }
        return fill;
    }

    private ConditionGroup.Condition parseOnlyCondition(JsonObject jsonObject) throws InvalidTargetObjectTypeException {
        if (!jsonObject.has(KEY_TYPE) || !jsonObject.has(KEY_VALUE)) return null;

        Identifier type = new Identifier(jsonObject.get(KEY_TYPE).getAsString());
        ConditionFactory factory = KovalRegistry.CONDITIONS.get(type);
        if (factory == null) return null;

        JsonPrimitive primitiveValue = jsonObject.getAsJsonPrimitive(KEY_VALUE);
        if (primitiveValue.isBoolean()) return factory.build(primitiveValue.getAsBoolean());
        if (primitiveValue.isNumber()) return factory.build(primitiveValue.getAsNumber());
        if (primitiveValue.isString()) {
            String stringValue = primitiveValue.getAsString();
            return factory.build(stringValue);
        }

        return null;
    }

    @Override
    public String toString() {
        return "ConditionGroup{" +
                "logic=" + logic +
                ", conditions = [" + String.join(", ", conditions.entrySet().stream().map(entry -> entry.getKey()+": "+entry.getValue()).toList()) +
                "]}";
    }
}
