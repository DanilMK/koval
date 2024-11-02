package net.smok.koval.forging;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.koval.AbstractParameter;
import net.smok.koval.ActionContext;
import net.smok.koval.Part;
import net.smok.utility.Vec2Int;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class KovalFunctionParameter extends AbstractParameter {

    private final Identifier functionId;
    private final KovalFunction<?> function;
    private final AbstractParameter[] parameters;

    protected KovalFunctionParameter(Class<?> valueType, Identifier functionId, KovalFunction<?> function, AbstractParameter[] parameters) {
        super(valueType);
        this.functionId = functionId;
        this.function = function;
        this.parameters = parameters;
    }

    @Override
    public Object get(ActionContext context) {
        return function.apply(context, parameters);
    }

    @Override
    public Text toText(ActionContext context) {
        Object value = get(context);
        return function.canSumResult() && value != null ? Text.literal(value.toString()) : Text.translatable(functionId.toTranslationKey("koval.function"),
                Arrays.stream(parameters).map(parameter -> parameter.toText(context.part())).toList().toArray());
    }

    @Override
    public Text toText(Part part) {
        if (function.canSumResult() && getPointers().length == 0) return Text.literal(get(new ActionContext(vec2Int -> null, part, new Object[0], Vec2Int.ZERO)).toString());
        return Text.translatable(functionId.toTranslationKey("koval.function"),
                Arrays.stream(parameters).map(parameter -> parameter.toText(part)).toList().toArray());
    }

    @Override
    public boolean canAssemble(ActionContext context) {
        return Arrays.stream(parameters).allMatch(parameter -> parameter.canAssemble(context));
    }

    @Override
    public Vec2Int[] getPointers() {
        if (Arrays.stream(parameters).allMatch(parameter -> parameter.getPointers().length == 0)) return new Vec2Int[0];


        Vec2Int[] result = new Vec2Int[0];
        for (AbstractParameter parameter : parameters)
            result = ArrayUtils.addAll(result, parameter.getPointers());

        return result;
    }
}
