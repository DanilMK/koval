package net.smok.koval;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.smok.Debug;
import net.smok.Values;
import net.smok.koval.forging.*;
import net.smok.utility.SavableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Shape implements SavableObject<Shape> {


    public static final Shape BASE_SHAPE = new Shape(Properties.EMPTY, new ParametersGroup(Map.of()), Map.of(), List.of());

    @NotNull private final Properties properties;
    @NotNull private final ParametersGroup parameters;
    @NotNull private final Map<String, String> parts;
    @NotNull private final List<AssembleRecipe> recipes;

    public Shape(@NotNull Properties properties, @NotNull ParametersGroup parameters, @NotNull Map<String, String> parts,
                 @NotNull List<AssembleRecipe> recipes) {
        this.properties = properties;
        this.parameters = parameters;
        this.parts = parts;
        this.recipes = recipes;
    }

    @Override
    public Shape createChild(@NotNull JsonObject json) {
        Properties propertiesChild = properties.createChild(json.getAsJsonObject(Values.Json.PROPERTIES));
        ParametersGroup parametersChild = parameters.createChild(json.getAsJsonObject(Values.Json.PARAMETERS));

        return new Shape(
                propertiesChild,
                parametersChild,
                parseItems(json.get(Values.Json.PARTS), parts),
                parseRecipes(json.get(Values.Json.RECIPE), recipes));
    }

    public @NotNull ParametersGroup getParameters() {
        return parameters;
    }

    public @Nullable Item testRecipe(ParameterPlace place) {
        for (AssembleRecipe recipe : recipes) if (recipe.test(place)) return recipe.result();
        return null;
    }

    public void foreachParts(Identifier shapeId, BiConsumer<Material, Identifier> action) {
        parts.forEach((materialStr, partStr) -> {
            Identifier matId = new Identifier(materialStr);
            Material material = KovalRegistry.MATERIALS.get(matId);
            if (material == null) {
                Debug.warn(MessageFormat.format("For shape ({0}) unknown material id ({1})", shapeId, matId));
                return;
            }


            String path = partStr.replace("#material", matId.getPath()).replace("#shape", shapeId.getPath());
            String namespace = shapeId.getNamespace();
            if (path.contains(":")) {
                String[] split = path.split(":");
                path = split[1];
                namespace = split[0];
            }

            Identifier partId = new Identifier(namespace, path);
            if (!Registry.ITEM.containsId(partId)) {
                Debug.warn(MessageFormat.format("For shape ({0}) and material ({1}) unknown item id ({2})", shapeId, matId, partId));
                return;
            }

            action.accept(material, partId);
        });
    }

    public static @NotNull Map<String, String> parseItems(JsonElement json, Map<String, String> baseMap) {
        HashMap<String, String> parts = new HashMap<>(baseMap);
        if (json != null && json.isJsonObject()) for (String key : json.getAsJsonObject().keySet()) {

            String value = json.getAsJsonObject().get(key).getAsString();
            if (parts.containsKey(key)) parts.replace(key, value);
            else parts.put(key, value);

        }
        return parts;
    }

    public static List<AssembleRecipe> parseRecipes(JsonElement json, List<AssembleRecipe> baseList) {
        List<AssembleRecipe> list = new ArrayList<>(baseList);
        if (json == null) return list;


        if (json.isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray()) {
                try {
                    AssembleRecipe recipe = AssembleRecipe.fromJson(element);
                    list.add(recipe);
                } catch (JsonParseException e) {
                    Debug.warn("Recipe will be skip. ");
                    Debug.err(e.toString());
                }
            }
        } else {

            try {
                AssembleRecipe recipe = AssembleRecipe.fromJson(json);
                list.add(recipe);
            } catch (JsonParseException e) {
                Debug.warn("Recipe will be skip. ");
                Debug.err(e.toString());
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "Shape {" +
                " properties=" + properties +
                ", parameters=" + parameters +
                ", parts=[" + String.join(", ", parts.entrySet().stream().map(entry -> entry.getKey()+": "+entry.getValue()).toList()) + "]" +
                ", conditions=" + recipes +
                '}';
    }

    public @NotNull Properties getProperties() {
        return properties;
    }
}