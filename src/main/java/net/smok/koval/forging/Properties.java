package net.smok.koval.forging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.smok.Debug;
import net.smok.Values;
import net.smok.utility.SavableObject;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;

public class Properties implements SavableObject<Properties> {

    public static Properties EMPTY = new Properties(new HashMap<>());

    final HashMap<Identifier, PrimitiveParameter<?>> properties;

    public Properties(HashMap<Identifier, PrimitiveParameter<?>> properties) {
        this.properties = properties;
    }

    public Properties createChild(@Nullable JsonObject propertiesJson) {
        return new Properties(parseProperties(propertiesJson, properties));
    }

    public boolean contains(Identifier identifier) {
        return properties.containsKey(identifier);
    }

    public PrimitiveParameter<?> getValue(Identifier identifier) {
        if (!properties.containsKey(identifier)) throw new NullPointerException("Cannot find "+identifier+" in properties");
        return properties.get(identifier);
    }


    /**
     * @param identifier   property ID
     * @param defaultValue default color value
     * @return property if it contains and is Int or String hex
     */
    public int getOrDefaultColor(Identifier identifier, int defaultValue) {
        if (properties.containsKey(identifier)) return properties.get(identifier).toColor();
        return defaultValue;
    }

    public int getColorIndex() {
        if (properties.containsKey(Values.Parameters.COLOR))
            return properties.get(Values.Parameters.COLOR).toColor();
        return 0xFFFFFFFF;
    }


    private HashMap<Identifier, PrimitiveParameter<?>> parseProperties(JsonObject json, HashMap<Identifier, PrimitiveParameter<?>> baseMap) {
        if (json == null) return new HashMap<>(baseMap);
        HashMap<Identifier, PrimitiveParameter<?>> resultMap = new HashMap<>(baseMap);

        ParameterDeserializer deserializer = new ParameterDeserializer();
        for (String name : json.keySet()) {
            Identifier id = new Identifier(name);
            JsonElement element = json.get(name);
            try {
                if (deserializer.isPrimitiveParameter(element)) {
                    PrimitiveParameter<?> primitive = deserializer.deserializePrimitive(element.getAsJsonPrimitive());

                    if (resultMap.containsKey(id)) resultMap.replace(id, primitive);
                    else resultMap.put(id, primitive);
                } else Debug.warn(MessageFormat.format("Property {0} is not primitive. {1}", id, element));
            } catch (JsonParseException e) {
                Debug.warn(e.toString());
            }
        }

        return resultMap;
    }

    @Override
    public String toString() {
        return "Properties{" +
                "properties=[" + String.join(", ",
                properties.entrySet().stream().map(entry -> entry.getKey()+"="+entry.getValue().value).toList()) +
                "]}";
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();

        properties.forEach((identifier, parameter) -> jsonObject.add(identifier.toString(), parameter.toJson(identifier)));

        return jsonObject;
    }
}