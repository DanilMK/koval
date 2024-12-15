package net.smok.koval.forging;

import net.smok.Values;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiFunction;

public class MultiFunction<T, R> extends KovalFunction<R> {


    private final BiFunction<ParameterPlace, ArrayList<T>, R> function;
    private final int minParamsAmount;

    protected MultiFunction(Class<R> returnType, Class<T> parametersType, int minParamsAmount, BiFunction<ParameterPlace, ArrayList<T>, R> function) {
        super(returnType, parametersType);
        this.minParamsAmount = minParamsAmount;
        this.function = function;
    }

    @Override
    public void checkSize(int providedSize) {
        if (providedSize < minParamsAmount) throw Values.Json.exceptionInvalidParametersAmount(minParamsAmount, providedSize);
    }

    @Override
    public Optional<R> apply(ParameterPlace context, AbstractParameter<?>[] parameters) {
        ArrayList<T> params = new ArrayList<>();
        for (AbstractParameter<?> parameter : parameters) {
            Optional<?> o = parameter.get(context);
            if (o.isEmpty()) return Optional.empty();
            //noinspection unchecked
            params.add((T) o.get());
        }
        return Optional.ofNullable(function.apply(context, params));
    }
}
