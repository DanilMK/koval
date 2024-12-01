package net.smok.koval;

import net.minecraft.util.Identifier;
import net.smok.koval.forging.Context;
import net.smok.koval.forging.ParameterPlace;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record MovablePlace(@Nullable Context context, Function<Vec2Int, Part> tableGetter, Part part,
                           Vec2Int position, Identifier parameterId) implements ParameterPlace {


    @Override
    public Optional<ParameterPlace> moveTo(Vec2Int pointer) {
        Vec2Int point = position.add(pointer);
        Part part = tableGetter.apply(point);
        if (part == null) return Optional.empty();
        return Optional.of(new MovablePlace(this.context, tableGetter, part, point, parameterId));
    }


    @Override
    public String toString() {
        return "ActionContext[" +
                "tableGetter=" + tableGetter + ", " +
                "part=" + part + ", " +
                "position=" + position + ']';
    }
}
