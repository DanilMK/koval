package net.smok.koval.forging;

import net.smok.Values;

import java.util.Optional;

public abstract class KovalFunction<R> {

    protected static final Class<?>[] EMPTY_TYPES = new Class[0];

    private final Class<?>[] parametersTypes;
    private final Class<R> returnType;

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


    public void checkSize(int providedSize) {
        if (providedSize != parametersTypes.length)
            throw Values.Json.exceptionInvalidParametersAmount(parametersTypes.length, providedSize);
    }

    public abstract Optional<R> apply(ParameterPlace context, AbstractParameter<?>[] parameters);


    public Class<R> returnType() {
        return returnType;
    }



}
