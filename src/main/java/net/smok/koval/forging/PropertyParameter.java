package net.smok.koval.forging;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.koval.AbstractParameter;
import net.smok.koval.ActionContext;
import net.smok.koval.Part;
import net.smok.koval.Properties;

import java.util.Objects;

public class PropertyParameter extends AbstractParameter {

    private final Identifier identifier;
    private final PropertyType type;



    protected PropertyParameter(Class<?> valueType, Identifier identifier, PropertyType type) {
        super(valueType);
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public Object get(ActionContext context) {
        return switch (type) {
            case SHAPE -> context.shapeProperties().getAny(identifier, "Shape");
            case MATERIAL -> context.materialProperty().getAny(identifier, "Material");
        };
    }

    @Override
    public Text toText(ActionContext context) {
        return toText(context.part());
    }

    @Override
    public Text toText(Part part) {
        Properties properties = type == PropertyType.SHAPE ? part.shape().getProperties() : part.material().getProperties();

        return properties.contains(identifier) ?
                Text.literal(properties.getAny(identifier, type.name()).toString())
                : Text.literal("UNKNOWN PROPERTY");
    }

    @Override
    public String toString() {
        return "Property{" + identifier + "=" + type + " as " +valueType.getName() + '}';
    }

    public enum PropertyType {
        MATERIAL, SHAPE
    }

    @Override
    public boolean canAssemble(ActionContext context) {
        if (Objects.requireNonNull(type) == PropertyType.MATERIAL) {
            return context.materialProperty().contains(identifier);
        } else if (type == PropertyType.SHAPE) {
            return context.shapeProperties().contains(identifier);
        }
        return true;
    }
}
