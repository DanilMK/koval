package net.smok.koval;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.koval.forging.Context;
import net.smok.koval.forging.ParameterPlace;
import net.smok.koval.forging.ParametersGroup;
import net.smok.utility.Vec2Int;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record Part(Shape shape, Material material, ParametersGroup parameters) {

    public Part(Shape shape, Material material) {
        this(shape, material, ParametersGroup.initialize(shape, material));
    }

    public void appendTooltip(List<Text> text) {
        parameters.parameters().forEach((identifier, parameter) ->
                text.add( Text.translatable(identifier.toTranslationKey("koval.parameter"))
                        .append(": ")
                        .append(parameter.getText(emptyPlace(this, identifier)))
                        .styled(style -> style.withColor(material.getColorIndex()))
                ));
    }

    public @Nullable Item testRecipe(ParameterPlace place) {
        return shape.testRecipe(place);
    }

    @Override
    public String toString() {
        return "Part{" +
                "shape=" + KovalRegistry.SHAPES.getId(shape) +
                ", material=" + KovalRegistry.MATERIALS.getId(material) +
                ", parameters=" + parameters +
                '}';
    }

    static ParameterPlace emptyPlace(Part part, Identifier identifier) {
        return new ParameterPlace() {
            @Override
            public Part part() {
                return part;
            }

            @Override
            public Identifier parameterId() {
                return identifier;
            }

            @Override
            public Optional<ParameterPlace> moveTo(Vec2Int pointer) {
                return Optional.empty();
            }

            @Override
            public @NotNull Context context() {
                return Context.EMPTY;
            }
        };
    }
}
