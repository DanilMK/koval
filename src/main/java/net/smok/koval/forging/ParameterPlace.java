package net.smok.koval.forging;

import net.minecraft.util.Identifier;
import net.smok.koval.Part;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ParameterPlace {

    Part part();
    Identifier parameterId();

    Optional<ParameterPlace> moveTo(Vec2Int pointer);

    default <T> Optional<AbstractParameter<T>> getParameter(Identifier identifier, Class<T> type) {
        AbstractParameter<?> abstractParameter = part().parameters().get(identifier);
        if (abstractParameter == null) return Optional.empty();
        if (type.isAssignableFrom(abstractParameter.getValueType()))
            //noinspection unchecked
            return Optional.of((AbstractParameter<T>) abstractParameter);
        return Optional.empty();
    }

    default AbstractParameter<?> moveParameter(Identifier identifier) {
        return part().parameters().get(identifier);
    }


    @Nullable Context context();
}
