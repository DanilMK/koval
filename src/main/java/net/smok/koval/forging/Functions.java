package net.smok.koval.forging;

import net.minecraft.block.BlockState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.smok.Debug;
import net.smok.koval.KovalRegistry;
import net.smok.utility.NumberUtils;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("ALL")
public final class Functions {

    
    
    public static final class Numbers {
        
        public static final String NAMESPACE = "number";

        public static final BiKovalFunction<Number, Number, Number> ADD = register(
                "add", (actionParams, a, b) -> NumberUtils.add(a, b));

        public static final BiKovalFunction<Number, Number, Number> SUBTRACT = register(
                "subtract", (actionParams, a, b) -> NumberUtils.subtract(a, b));

        public static final BiKovalFunction<Number, Number, Number> MULTIPLY = register(
                "multiply", (actionParams, a, b) -> NumberUtils.multiply(a, b));

        public static final BiKovalFunction<Number, Number, Number> DIVIDE = register(
                "divide", (actionParams, a, b) -> NumberUtils.divide(a, b));


        // todo add min and rethink Numbers
        
        private static BiKovalFunction<Number, Number, Number> register(String path, TriFunction<ParameterPlace, Number, Number, Number> function) {
            BiKovalFunction<Number, Number, Number> result = new BiKovalFunction<>(Number.class, Number.class, Number.class, function);
            return Registry.register(KovalRegistry.FUNCTIONS, new Identifier(NAMESPACE, path), result);
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

        public static final MonoKovalFunction<String, Boolean> BLOCK_IS_IN = (MonoKovalFunction<String, Boolean>) register(
                new Identifier("block:is_in"), String.class, Boolean.class, (place, inner) ->
                {
                    if (place.context() instanceof ActionContext actionContext) {
                        BlockState targetBlock = actionContext.targetBlock();
                        return targetBlock != null && targetBlock.isIn(TagKey.of(Registry.BLOCK_KEY, new Identifier(inner)));
                    }
                    return false;
                }
        ).setCanSumResult(false);

        public static final BiKovalFunction<BlockState, String, Boolean> BLOCK_STATE_IS_IN = register(
                new Identifier("block:state_is_in"), BlockState.class, String.class, Boolean.class,
                (actionParams, blockState, tag) -> blockState != null && blockState.isIn(TagKey.of(Registry.BLOCK_KEY, new Identifier(tag)))
        );

        public static final KovalFunction<BlockState> TARGET_BLOCK_STATE = register(
                new Identifier("action:target_block_state"), BlockState.class,
                kovalContext -> kovalContext.context() instanceof ActionContext actionContext ? actionContext.targetBlock() : null
        );

        public static String init() {
            return NAMESPACE;
        }

    }

    public static final class Items {
        public static final String NAMESPACE = "item";


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




    private static <R> KovalFunction<R> register(Identifier id, Class<R> r, Function<ParameterPlace, R> function) {
        KovalFunction<R> result = new KovalFunction<>(r) {
            @Override
            public Optional<R> apply(ParameterPlace context, AbstractParameter<?>[] parameters) {
                return Optional.ofNullable(function.apply(context));
            }
        };
        return Registry.register(KovalRegistry.FUNCTIONS, id, result);
    }

    private static <T, R> MonoKovalFunction<T, R> register(Identifier id, Class<T> inner, Class<R> r, BiFunction<ParameterPlace, T, R> function) {
        MonoKovalFunction<T, R> result = new MonoKovalFunction<>(inner, r, function);
        return Registry.register(KovalRegistry.FUNCTIONS, id, result);
    }

    private static <T, U, R> BiKovalFunction<T, U, R> register(Identifier id, Class<T> a, Class<U> b, Class<R> r, TriFunction<ParameterPlace, T, U, R> function) {
        BiKovalFunction<T, U, R> result = new BiKovalFunction<>(a, b, r, function);
        return Registry.register(KovalRegistry.FUNCTIONS, id, result);
    }

    public static void init() {
        Debug.log("Register functions: "+Numbers.init()+", "+Booleans.init()+", "+Items.init()+", "+Blocks.init()+", "+Assemblers.init());
    }
}
