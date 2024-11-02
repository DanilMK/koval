package net.smok.koval.forging;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.smok.koval.AbstractParameter;
import net.smok.koval.ActionContext;
import net.smok.koval.Part;
import net.smok.utility.Vec2Int;

public class PointerParameter extends AbstractParameter {

    private final Vec2Int pointer;
    private final Identifier identifier;

    public PointerParameter(Class<?> valueType, Vec2Int pointer, Identifier identifier) {
        super(valueType);
        this.pointer = pointer;
        this.identifier = identifier;
    }

    @Override
    public Object get(ActionContext context) {
        Pair<AbstractParameter, ActionContext> move = context.moveTo(identifier, pointer);
        if (move == null) return null;
        return move.getLeft().get(move.getRight());
    }

    @Override
    public Text toText(ActionContext context) {
        Object value = get(context);
        return value != null ? Text.literal(value.toString()) : toText(context.part());
    }

    @Override
    public Text toText(Part part) {
        return Text.literal(pointer.toString());
    }

    @Override
    public String toString() {
        return "Pointer{" + identifier + "=" + pointer + " as " +valueType.getName() + "}";
    }

    @Override
    public boolean canAssemble(ActionContext context) {
        Pair<AbstractParameter, ActionContext> move = context.moveTo(identifier, pointer);

        if (move == null || !move.getLeft().canAssemble(move.getRight())) return false;

        Object o = get(context);
        return valueType.isInstance(o);
    }

    @Override
    public Vec2Int[] getPointers() {
        return new Vec2Int[] {pointer};
    }
}
