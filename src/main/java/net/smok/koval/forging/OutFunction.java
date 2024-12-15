package net.smok.koval.forging;

import java.util.Optional;
import java.util.function.Function;

public class OutFunction<R> extends KovalFunction<R> {

    private final Function<ParameterPlace, R> function;

    protected OutFunction(Class<R> returnType, Function<ParameterPlace, R> function) {
        super(returnType, EMPTY_TYPES);
        this.function = function;
    }

    @Override
    public Optional<R> apply(ParameterPlace context, AbstractParameter<?>[] parameters) {
        return Optional.ofNullable(function.apply(context));
    }
}
