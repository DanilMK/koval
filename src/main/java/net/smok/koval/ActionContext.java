package net.smok.koval;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.smok.utility.Vec2Int;

import java.util.function.Function;

public record ActionContext(Function<Vec2Int, Part> tableGetter, Part part, Object[] actionParams, Vec2Int position) {



    public Properties materialProperty() {
        return part.material().getProperties();
    }

    public Properties shapeProperties() {
        return part.shape().getProperties();
    }

    public Pair<AbstractParameter, ActionContext> moveTo(Identifier identifier, Vec2Int pointer) {
        Vec2Int point = position.add(pointer);
        Part part = tableGetter.apply(point);
        if (part == null) return null;
        return new Pair<>(part.parameters().get(identifier), new ActionContext(tableGetter, part, actionParams, point));
    }

}
