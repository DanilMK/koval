package net.smok.koval.forging;

import com.google.gson.JsonElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.koval.Material;
import net.smok.koval.Shape;
import net.smok.utility.DefaultedTranslatableTextContent;
import net.smok.utility.NumberUtils;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractParameter<T> {


    public abstract Optional<T> get(ParameterPlace context);

    public abstract Class<T> getValueType();

    public static Text toText(Identifier identifier, @NotNull Object object) {
        if (identifier.equals(Values.Parameters.ATTACK_DAMAGE) && object instanceof Number n)
            return Text.literal(NumberUtils.add(n, 1f).toString());
        if (identifier.equals(Values.Parameters.ATTACK_SPEED) && object instanceof Number n)
            return Text.literal(NumberUtils.add(n, 4f).toString());

        String defaultValue = object.toString();
        String key;
        if (object instanceof Number number) {
            float f = number.floatValue();
            key = String.format("%.0f", Math.floor(f));
            defaultValue = f == (long)f ? String.format("%d", (long)f) : String.format("%s", f);
        }
        else if (object instanceof String s) key = s.replace("/", ".");
        else key = defaultValue;

        return MutableText.of(new DefaultedTranslatableTextContent(
                identifier.toTranslationKey("koval.parameter") + "." + key, defaultValue));
    }

    /**
     * @param place Table Action place
     * @return Text in Action place
     */
    public Text getText(ParameterPlace place) {
        Optional<T> o = get(place);
        //if (o instanceof AbstractParameter ap) return ap.toText(place);
        return o.isEmpty() ? Text.literal("null") : toText(place.parameterId(), o.get());
    }


    public boolean canAssemble(ParameterPlace context) {
        return true;
    }

    public Vec2Int[] getPointers() {
        return new Vec2Int[0];
    }

    public abstract JsonElement toJson(Identifier identifier);


    /**
     * @param identifier parameter identifier.
     * @param shape      own shape.
     * @param material   own material.
     * @return return this parameter or initialized clone.
     * @throws NullPointerException property has not any parameter with provided id.
     */
    public AbstractParameter<?> initialize(Identifier identifier, Shape shape, Material material) throws NullPointerException {
        return this;
    }

    @Override
    public abstract AbstractParameter<T> clone();
}
