package net.smok.koval.forging;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.smok.Debug;
import net.smok.koval.KovalRegistry;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("ALL")
public final class Functions {

    
    
    public static final class Numbers {
        
        public static final String NAMESPACE = "number";

        public static final BiKovalFunction<Number, Number, Number> ADD = register(
                "add", (a, b) -> a + b, (a, b) -> a + b);

        public static final BiKovalFunction<Number, Number, Number> SUBTRACT = register(
                "subtract", (a, b) -> a - b, (a, b) -> a - b);

        public static final BiKovalFunction<Number, Number, Number> MULTIPLY = register(
                "multiply", (a, b) -> a * b, (a, b) -> a * b);

        public static final BiKovalFunction<Number, Number, Number> DIVIDE = register(
                "divide", (a, b) -> a / b, (a, b) -> a / b);

        public static final BiKovalFunction<Number, Number, Number> MIN = register(
                "min", (a, b) -> Math.min(a, b), (a, b) -> Math.min(a, b));

        public static final BiKovalFunction<Number, Number, Number> MAX = register(
                "max", (a, b) -> Math.max(a, b), (a, b) -> Math.max(a, b));

        public static final MultiFunction<Number, Number> SUM = register(
                "sum", 1, (place, list) -> {
                    int i = 0;
                    float f = 0;

                    for (Number number : list) {
                        if (number instanceof Float)
                             f += number.floatValue();
                        else i += number.intValue();
                    }
                    return f > 0 ? f + i : i;
                }
        );


        private static BiKovalFunction<Number, Number, Number> register(
                String path,
                BiFunction<Integer, Integer, Integer> intFunction,
                BiFunction<Float, Float, Float> floatFunction)
        {
            BiKovalFunction<Number, Number, Number> result = new BiKovalFunction<>(Number.class, Number.class, Number.class, (place, a, b) -> {
                if (a instanceof Float || b instanceof Float)
                    return floatFunction.apply(a.floatValue(), b.floatValue());
                    return intFunction.apply(a.intValue(), b.intValue());
            });
            return Registry.register(KovalRegistry.FUNCTIONS, new Identifier(NAMESPACE, path), result);
        }

        private static MultiFunction<Number, Number> register(
                String path, int minParamsAmount, BiFunction<ParameterPlace, ArrayList<Number>, Number> function)
        {
            return Registry.register(KovalRegistry.FUNCTIONS, new Identifier(NAMESPACE, path),
                    new MultiFunction<>(Number.class, Number.class, minParamsAmount, function));
        }


        public static String init() {
            return NAMESPACE;
        }
    }
    
    public static final class Booleans {
        public static final String NAMESPACE = "boolean";


        public static final BiKovalFunction<Boolean, Boolean, Boolean> AND = register(
                "and", (actionParams, a, b) -> a && b);

        public static final BiKovalFunction<Boolean, Boolean, Boolean> OR = register(
                "or", (actionParams, a, b) -> a || b);

        public static final BiKovalFunction<Boolean, Boolean, Boolean> XOR = register(
                "xor", (actionParams, a, b) -> a ^ b);

        public static final MonoKovalFunction<Boolean, Boolean> NOT = register(
                "not", (actionParams, inner) -> !inner);



        private static BiKovalFunction<Boolean, Boolean, Boolean> register(String path, TriFunction<ParameterPlace, Boolean, Boolean, Boolean> function) {
            BiKovalFunction<Boolean, Boolean, Boolean> result = new BiKovalFunction<>(Boolean.class, Boolean.class, Boolean.class, function);
            return Registry.register(KovalRegistry.FUNCTIONS, new Identifier(NAMESPACE, path), result);
        }

        private static MonoKovalFunction<Boolean, Boolean> register(String path, BiFunction<ParameterPlace, Boolean, Boolean> function) {
            MonoKovalFunction<Boolean, Boolean> result = new MonoKovalFunction<>(Boolean.class, Boolean.class, function);
            return Registry.register(KovalRegistry.FUNCTIONS, new Identifier(NAMESPACE, path), result);
        }

        public static String init() {
            return NAMESPACE;
        }
    }
    

    public static final class Blocks {
        public static final String NAMESPACE = "block";


        public static final BiKovalFunction<BlockState, String, Boolean> BLOCK_STATE_IS_IN = register(
                new Identifier("block:is_in"), BlockState.class, String.class, Boolean.class,
                (actionParams, blockState, tag) -> blockState != null && blockState.isIn(TagKey.of(Registry.BLOCK_KEY, new Identifier(tag)))
        );


        public static String init() {
            return NAMESPACE;
        }

    }

    public static final class Items {
        public static final String NAMESPACE = "item";

        public static final BiKovalFunction<ItemStack, String, Boolean> ITEM_STACK_IS_IN = register(
                new Identifier("item:is_in"), ItemStack.class, String.class, Boolean.class,
                (actionParams, itemStack, tag) -> itemStack != null && itemStack.isIn(TagKey.of(Registry.ITEM_KEY, new Identifier(tag))),
                (place, itemStack, string) -> Text.of(string)
        );

        public static String init() {
            return NAMESPACE;
        }
    }

    public static final class Action {

        public static final String NAMESPACE = "block";

        public static final OutFunction<BlockState> TARGET_BLOCK = register(
                new Identifier("action:target_block"), BlockState.class,
                place -> place.context() instanceof ActionContext actionContext ? actionContext.targetBlock() : null
        );

        public static final KovalFunction<ItemStack> TARGET_ITEM = register(
                new Identifier("action:target_item"), ItemStack.class,
                place -> place.context() instanceof ActionContext actionContext ? actionContext.targetItem() : null
        );

        public static String init() {
            return NAMESPACE;
        }

    }


    public static final class Assemblers {
        public static final String NAMESPACE = "assembler";


        public static final MonoKovalFunction<String, Boolean> HAS_ANY_ITEM = register(
                new Identifier(NAMESPACE, "has_any_item"), String.class, Boolean.class, (place, name) ->
                        place.context() instanceof AssemblerContext context &&
                        context.table().stream().anyMatch(itemStack ->
                                itemStack.isIn(TagKey.of(Registry.ITEM_KEY, new Identifier(name))))
        );

        public static String init() {
            return NAMESPACE;
        }
    }





    private static <T, R> OutFunction<R> register(Identifier id, Class<R> r, Function<ParameterPlace, R> function) {
        return Registry.register(KovalRegistry.FUNCTIONS, id, new OutFunction<R>(r, function));
    }

    private static <T, R> MonoKovalFunction<T, R> register(Identifier id, Class<T> inner, Class<R> r,
                                                           BiFunction<ParameterPlace, T, R> function) {

        return Registry.register(KovalRegistry.FUNCTIONS, id, new MonoKovalFunction<T, R>(inner, r, function));
    }

    private static <T, U, R> BiKovalFunction<T, U, R> register(Identifier id, Class<T> a, Class<U> b, Class<R> r,
                                                               TriFunction<ParameterPlace, T, U, R> function) {

        return Registry.register(KovalRegistry.FUNCTIONS, id, new BiKovalFunction<T, U, R>(a, b, r, function));
    }

    private static <T, U, R> BiKovalFunction<T, U, R> register(Identifier id, Class<T> a, Class<U> b, Class<R> r,
                                                               TriFunction<ParameterPlace, T, U, R> function,
                                                               TriFunction<ParameterPlace, T, U, Text> textFunction) {

        return Registry.register(KovalRegistry.FUNCTIONS, id, new BiKovalFunction<T, U, R>(a, b, r, function));
    }

    public static void init() {
        Debug.log("Register functions: "+Numbers.init()+", "+Booleans.init()+", "+Items.init()+", "+Blocks.init()+", "+Assemblers.init()+", "+Action.init());
    }
}
