package net.smok.koval.forging;

import net.smok.koval.AbstractParameter;
import net.smok.koval.ActionContext;

public abstract class MonoKovalFunction<T, R> extends KovalFunction<R>{

    private final Class<T> innerType;

    public MonoKovalFunction(Class<T> innerType, Class<R> returnType) {
        super(returnType, innerType);
        this.innerType = innerType;
    }

    @Override
    public R apply(ActionContext context, AbstractParameter[] parameters) {
        return apply(context.actionParams(), parameters[0].get(context, innerType));
    }

    /**
     *
     * @param actionParams Parameters invoke method. (E.g. breaking BlockState, usable ItemStack)
     * @param inner Inner parameter.
     * @return result of function.
     */
    public abstract R apply(Object[] actionParams, T inner);

}
