package net.smok.koval;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.smok.Debug;
import net.smok.Values;
import net.smok.koval.forging.ConditionGroup;
import net.smok.koval.forging.ParametersGroup;
import net.smok.utility.SavableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Shape implements SavableObject<Shape> {


    public static final Shape BASE_SHAPE = new Shape(Properties.EMPTY, new ParametersGroup(Map.of()), Map.of(), ConditionGroup.EMPTY, null);

    @NotNull private final Properties properties;
    @NotNull private final ParametersGroup parameters;
    @NotNull private final Map<String, String> parts;
    @NotNull private final ConditionGroup conditions;
    @Nullable private final Item constructItem;

    public Shape(@NotNull Properties properties, @NotNull ParametersGroup parameters, @NotNull Map<String, String> parts,
                 @NotNull ConditionGroup conditions, @Nullable Item constructItem) {
        this.properties = properties;
        this.parameters = parameters;
        this.constructItem = constructItem;
        this.parts = parts;
        this.conditions = conditions;
    }

    @Override
    public Shape createChild(@NotNull JsonObject json) {
        Properties propertiesChild = properties.createChild(json.getAsJsonObject(Values.JsonKeys.PROPERTIES));
        ParametersGroup parametersChild = parameters.createChild(json.getAsJsonObject(Values.JsonKeys.PARAMETERS));
        ConditionGroup conditionsChild = conditions.createChild(json.getAsJsonObject("conditions"));

        return new Shape(
                propertiesChild,
                parametersChild,
                parseItems(json.get(Values.JsonKeys.IDS), parts),
                conditionsChild,
                JsonHelper.getItem(json, "construct_item", constructItem)
        );
    }

    public @Nullable Item getConstructItem() {
        return constructItem;
    }

    public @NotNull ParametersGroup getParameters() {
        return parameters;
    }

    public @NotNull ConditionGroup getConditions() {
        return conditions;
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

    @Override
    public String toString() {
        return "Shape {" +
                " properties=" + properties +
                ", parameters=" + parameters +
                ", parts=[" + String.join(", ", parts.entrySet().stream().map(entry -> entry.getKey()+": "+entry.getValue()).toList()) + "]" +
                ", conditions=" + conditions +
                ", constructItem=" + constructItem +
                '}';
    }

    public Properties getProperties() {
        return properties;
    }
}