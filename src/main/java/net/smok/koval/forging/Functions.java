package net.smok.koval.forging;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.smok.Debug;
import net.smok.koval.KovalRegistry;
import net.smok.Values;
import net.smok.koval.Properties;
import net.smok.utility.NumberUtils;
import org.apache.commons.lang3.function.TriFunction;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import java.text.MessageFormat;
import java.util.function.BiFunction;

public final class Functions {

    public static final ConditionFactory IS_IN = KovalRegistry.CONDITIONS.register(new Identifier("logic:is_in"), new ConditionFactory() {
        @Override
        public ConditionGroup.Condition build(Object value) throws InvalidTargetObjectTypeException {
            if (value instanceof String string)
                return new ConditionGroup.Condition(item -> item.isIn(TagKey.of(Registry.ITEM_KEY, new Identifier(string))));
            if (value instanceof Identifier identifier)
                return new ConditionGroup.Condition(item -> item.isIn(TagKey.of(Registry.ITEM_KEY, identifier)));
            throw new InvalidTargetObjectTypeException(MessageFormat.format(Values.INVALID_VALUE_TYPE_EXCEPTION, "String or Identifier", value));
        }

        @Override
        public ConditionGroup.Condition build(Identifier identifier, Properties properties) throws InvalidTargetObjectTypeException {
            return build(properties.getOrDefault(identifier, ""));
        }
    });
    
    public static final BiKovalFunction<Number, Number, Number> ADD = register(
            new Identifier("number:add"), Number.class, Number.class, Number.class, (actionParams, a, b) -> NumberUtils.add(a, b));

    public static final BiKovalFunction<Number, Number, Number> SUBTRACT = register(
            new Identifier("number:subtract"), Number.class, Number.class, Number.class, (actionParams, a, b) -> NumberUtils.subtract(a, b));

    public static final BiKovalFunction<Number, Number, Number> MULTIPLY = register(
            new Identifier("number:multiply"), Number.class, Number.class, Number.class, (actionParams, a, b) -> NumberUtils.multiply(a, b));

    public static final BiKovalFunction<Number, Number, Number> DIVIDE = register(
            new Identifier("number:divide"), Number.class, Number.class, Number.class, (actionParams, a, b) -> NumberUtils.divide(a, b));

    public static final BiKovalFunction<Boolean, Boolean, Boolean> AND = register(
            new Identifier("boolean:add"), Boolean.class, Boolean.class, Boolean.class, (actionParams, a, b) -> a && b);

    public static final BiKovalFunction<Boolean, Boolean, Boolean> OR = register(
            new Identifier("boolean:add"), Boolean.class, Boolean.class, Boolean.class, (actionParams, a, b) -> a || b);

    public static final BiKovalFunction<Boolean, Boolean, Boolean> XOR = register(
            new Identifier("boolean:add"), Boolean.class, Boolean.class, Boolean.class, (actionParams, a, b) -> a ^ b);

    public static final MonoKovalFunction<Boolean, Boolean> NOT = register(
            new Identifier("boolean:add"), Boolean.class, Boolean.class, (actionParams, inner) -> !inner);

    public static final MonoKovalFunction<String, Boolean> BLOCK_IS_IN = (MonoKovalFunction<String, Boolean>) register(
            new Identifier("block:is_in"), String.class, Boolean.class, (actionParams, inner) ->
                    actionParams.length > 0 && actionParams[0] instanceof BlockState blockState &&
                    blockState.isIn(TagKey.of(Registry.BLOCK_KEY, new Identifier(inner)))
    ).setCanSumResult(false);

    public static final MonoKovalFunction<String, Boolean> ITEM_IS_IN = (MonoKovalFunction<String, Boolean>) register(
            new Identifier("item:is_in"), String.class, Boolean.class, (actionParams, inner) ->
                    actionParams.length > 0 && actionParams[0] instanceof ItemStack itemStack &&
                            itemStack.isIn(TagKey.of(Registry.ITEM_KEY, new Identifier(inner)))
    ).setCanSumResult(false);

    
    private static <T, R> MonoKovalFunction<T, R> register(Identifier id, Class<T> inner, Class<R> r, BiFunction<Object[], T, R> function) {
        MonoKovalFunction<T, R> result = new MonoKovalFunction<>(inner, r) {
            @Override
            public R apply(Object[] actionParams, T inner) {
                return function.apply(actionParams, inner);
            }
        };
        return KovalRegistry.FUNCTIONS.register(id, result);
    }

    private static <T, U, R> BiKovalFunction<T, U, R> register(Identifier id, Class<T> a, Class<U> b, Class<R> r, TriFunction<Object[], T, U, R> function) {
        BiKovalFunction<T, U, R> result = new BiKovalFunction<>(a, b, r) {
            @Override
            public R apply(Object[] actionParams, T a, U b) {
                return function.apply(actionParams, a, b);
            }
        };
        return KovalRegistry.FUNCTIONS.register(id, result);
    }

    public static void init() {
        Debug.log("Register functions...");
    }
}
