package net.smok.koval;

import net.minecraft.text.Text;
import net.smok.utility.Vec2Int;

public abstract class AbstractParameter {

    public final Class<?> valueType;

    protected AbstractParameter(Class<?> valueType) {
        this.valueType = valueType;
    }

    public abstract Object get(ActionContext context);

    public <T> T get(ActionContext context, Class<T> cast) {
        Object result = get(context);
        if (cast.isInstance(result)) return cast.cast(result);
        else throw new ClassCastException();
    }

    /**
     * @param context Table Action context
     * @return Text in Action context
     */
    public Text toText(ActionContext context) {
        Object o = get(context);
        if (o instanceof AbstractParameter ap) return ap.toText(context);
        return Text.of(o == null ? "null" : o.toString());
    }

    /**
     * @return Text in Part context
     */
    public Text toText(Part part) {
        return Text.empty();
    }


    public void typeCheck(Class<?> cl) throws Exception {
        if (!cl.isAssignableFrom(valueType)) throw new Exception("Invalid parameter type. Expected type: "+cl+", provided: "+valueType);
    }

    public boolean typeCheckSafe(Class<?> cl) {
        return cl.isAssignableFrom(valueType);
    }

    public boolean canAssemble(ActionContext context) {
        return true;
    }

    public Vec2Int[] getPointers() {
        return new Vec2Int[0];
    }
}
