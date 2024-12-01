package net.smok.koval.forging;

import java.util.Optional;
import java.util.function.BiFunction;

public class MonoKovalFunction<T, R> extends KovalFunction<R>{

    private final Class<T> innerType;
    private final BiFunction<ParameterPlace, T, R> function;

    public MonoKovalFunction(Class<T> innerType, Class<R> returnType, BiFunction<ParameterPlace, T, R> function) {
        super(returnType, innerType);
        this.innerType = innerType;
        this.function = function;
    }

    @Override
    public Optional<R> apply(ParameterPlace context, AbstractParameter<?>[] parameters) {
        Optional<T> result = (Optional<T>) parameters[0].get(context);
        return result.map(object -> function.apply(context, innerType.cast(object)));
    }

}
