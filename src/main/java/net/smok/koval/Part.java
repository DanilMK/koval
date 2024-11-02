package net.smok.koval;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.smok.koval.forging.ParametersGroup;
import net.smok.utility.Vec2Int;

import java.util.List;
import java.util.function.Function;

public record Part(Shape shape, Material material, ParametersGroup parameters) {

    public Part(Shape shape, Material material) {
        this(shape, material, new ParametersGroup(shape.getParameters().parameters(), material.getParameters().parameters()));
    }

    public void appendTooltip(List<Text> text) {
        parameters.parameters().forEach((identifier, parameter) ->
                text.add( Text.translatable(identifier.toTranslationKey("koval.parameter"))
                        .append(": ")
                        .append(parameter.toText(this))
                        .styled(style -> style.withColor(material.getColorIndex()))
                ));
    }

    public boolean testCondition(Function<Vec2Int, ItemStack> partGetter) {
        return shape.getConditions().test(partGetter);
    }
}
