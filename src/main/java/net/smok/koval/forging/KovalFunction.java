package net.smok.koval.forging;

import net.smok.koval.AbstractParameter;
import net.smok.koval.ActionContext;

public abstract class KovalFunction<R> {

    private final Class<?>[] parametersTypes;
    private final Class<R> returnType;
    private boolean canSumResult = true;

    protected KovalFunction(Class<R> returnType, Class<?>... parametersTypes) {
        this.returnType = returnType;
        this.parametersTypes = parametersTypes;
    }

    public int innerParametersCount() {
        return parametersTypes.length;
    }

    public Class<?>[] getParametersTypes() {
        return parametersTypes;
    }

    public abstract R apply(ActionContext context, AbstractParameter[] parameters);

    public Class<R> returnType() {
        return returnType;
    }

    public boolean canSumResult() {
        return canSumResult;
    }

    public KovalFunction<R> setCanSumResult(boolean b) {
        canSumResult = b;
        return this;
    }


}
