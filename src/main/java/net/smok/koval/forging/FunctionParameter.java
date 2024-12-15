package net.smok.koval.forging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.koval.*;
import net.smok.utility.Vec2Int;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class FunctionParameter<R> extends AbstractParameter<R> {

    private static final AbstractParameter<?>[] EMPTY_PARAMETERS = new AbstractParameter[0];

    public static final String FUNCTION_ID = Values.Json.FUNCTION_ID;
    private final KovalFunction<R> function;
    private final AbstractParameter<?>[] parameters;


    @Contract("_, _, _ -> new")
    public static <T, U, R> @NotNull FunctionParameter<R> of(BiKovalFunction<T, U, R> function, AbstractParameter<T> first, AbstractParameter<U> second) {
        return new FunctionParameter<>(function, new AbstractParameter[]{first, second});
    }

    @Contract("_, _ -> new")
    public static <T, R> @NotNull FunctionParameter<R> of(MonoKovalFunction<T, R> function, AbstractParameter<T> parameter) {
        return new FunctionParameter<>(function, new AbstractParameter[]{parameter});
    }

    @Contract("_, _ -> new")
    public static <R> @NotNull FunctionParameter<R> of(@NotNull KovalFunction<R> function, AbstractParameter<?>... parameters) {
        function.checkSize(parameters.length);
        return new FunctionParameter<>(function, parameters);
    }

    @Contract("_ -> new")
    public static <R> @NotNull FunctionParameter<R> of(@NotNull KovalFunction<R> function) {
        function.checkSize(0);
        return new FunctionParameter<>(function, EMPTY_PARAMETERS);
    }

    protected FunctionParameter(KovalFunction<R> function, AbstractParameter<?>[] parameters) {
        this.function = function;
        this.parameters = parameters;
    }

    @Override
    public Optional<R> get(ParameterPlace context) {
        return function.apply(context, parameters);
    }

    @Override
    public Class<R> getValueType() {
        return function.returnType();
    }

    @Override
    public Text getText(ParameterPlace place) {

        Optional<R> o = get(place);
        if (o.isPresent()) return toText(place.parameterId(), o.get());

        Identifier id = KovalRegistry.FUNCTIONS.getId(function);
        if (id == null) return Text.translatable("koval.function.unknown");
        return Text.translatable(id.toTranslationKey("koval.function"),
                Arrays.stream(parameters).map(parameter -> parameter.getText(place)).toList().toArray());
    }

    @Override
    public boolean canAssemble(ParameterPlace context) {
        return Arrays.stream(parameters).allMatch(parameter -> parameter.canAssemble(context));
    }

    @Override
    public Vec2Int[] getPointers() {
        if (Arrays.stream(parameters).allMatch(parameter -> parameter.getPointers().length == 0)) return new Vec2Int[0];


        Vec2Int[] result = new Vec2Int[0];
        for (AbstractParameter<?> parameter : parameters)
            result = ArrayUtils.addAll(result, parameter.getPointers());

        return result;
    }

    @Override
    public JsonElement toJson(Identifier identifier) {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray(parameters.length);

        Identifier id = KovalRegistry.FUNCTIONS.getId(function);
        if (id == null) return null;
        object.addProperty(FUNCTION_ID, id.toString());
        for (AbstractParameter<?> parameter : parameters) {
            array.add(parameter.toJson(identifier));
        }
        object.add(Values.Json.PARAMETERS, array);

        return object;
    }

    @Override
    public AbstractParameter<R> initialize(Identifier identifier, Shape shape, Material material) throws  NullPointerException {
        AbstractParameter<?>[] clone = new AbstractParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            clone[i] = parameters[i].initialize(identifier, shape, material);
        }
        return new FunctionParameter<>(function, clone);
    }

    @Override
    public AbstractParameter<R> clone() {
        return new FunctionParameter<>(function, parameters);
    }

    @Override
    public String toString() {
        Identifier id = KovalRegistry.FUNCTIONS.getId(function);
        return "FunctionParameter{" +
                " function=" + (id != null ? id : function) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
