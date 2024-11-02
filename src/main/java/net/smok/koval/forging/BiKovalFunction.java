package net.smok.koval.forging;

import net.smok.koval.AbstractParameter;
import net.smok.koval.ActionContext;

public abstract class BiKovalFunction<T, U, R> extends KovalFunction<R>{

    private final Class<T> firstType;
    private final Class<U> secondType;

    public BiKovalFunction(Class<T> firstType, Class<U> secondType, Class<R> returnType) {
        super(returnType, firstType, secondType);
        this.firstType = firstType;
        this.secondType = secondType;
    }

    @Override
    public R apply(ActionContext context, AbstractParameter[] parameters) {
        return apply(context.actionParams(), parameters[0].get(context, firstType), parameters[1].get(context, secondType));
    }

    /**
     *
     * @param actionParams Parameters invoke method. (E.g. breaking BlockState, usable ItemStack)
     * @param a First inner parameter.
     * @param b Second inner parameter.
     * @return result of function.
     */
    public abstract R apply(Object[] actionParams, T a, U b);
}
