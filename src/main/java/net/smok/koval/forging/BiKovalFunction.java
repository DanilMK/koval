package net.smok.koval.forging;

import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public class BiKovalFunction<T, U, R> extends KovalFunction<R>{

    private final Class<T> firstType;
    private final Class<U> secondType;
    private final TriFunction<ParameterPlace, T, U, R> function;


    public BiKovalFunction(Class<T> firstType, Class<U> secondType, Class<R> returnType,
                           TriFunction<ParameterPlace, T, U, R> function) {

        super(returnType, firstType, secondType);
        this.firstType = firstType;
        this.secondType = secondType;
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<R> apply(ParameterPlace context, AbstractParameter<?>[] parameters) {
        Optional<T> first = (Optional<T>) parameters[0].get(context);
        Optional<U> second = (Optional<U>) parameters[1].get(context);
        if (first.isEmpty() || second.isEmpty()) return Optional.empty();
        return Optional.ofNullable(function.apply(context, firstType.cast(first.get()), secondType.cast(second.get())));
    }

}
