package net.smok.koval.forging;

import net.minecraft.text.Text;
import net.smok.koval.AbstractParameter;
import net.smok.koval.ActionContext;
import net.smok.koval.Part;
import org.jetbrains.annotations.NotNull;

public class BaseParameter extends AbstractParameter {

    protected final @NotNull Object value;

    public BaseParameter(@NotNull Object value) {
        super(value.getClass());
        this.value = value;
    }

    @Override
    public Object get(ActionContext context) {
        return value;
    }

    @Override
    public Text toText(Part part) {
        return Text.literal(value.toString());
    }

    @Override
    public Text toText(ActionContext context) {
        return Text.literal(value.toString());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
