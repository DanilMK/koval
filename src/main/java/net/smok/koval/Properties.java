package net.smok.koval;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.utility.SavableObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Properties implements SavableObject<Properties> {

    public static Properties EMPTY = new Properties(new HashMap<>());

    final HashMap<Identifier, Object> properties;

    public Properties(HashMap<Identifier, Object> properties) {
        this.properties = properties;
    }

    public Properties createChild(@Nullable JsonObject propertiesJson) {
        return new Properties(parseProperties(propertiesJson, properties));
    }

    public Object getAny(Identifier identifier, String where) {
        if (!properties.containsKey(identifier)) throw new NullPointerException("Cannot find "+identifier+" in "+where+" properties");
        return properties.get(identifier);
    }

    public boolean contains(Identifier identifier) {
        return properties.containsKey(identifier);
    }




    /**
     * @param identifier   property ID
     * @param defaultValue default property value
     * @return property if it contains and is Number
     */
    public Number getOrDefault(Identifier identifier, Number defaultValue) {
        if (properties.containsKey(identifier)) {
            Object result = properties.get(identifier);
            if (result instanceof Number number) return number;
        }
        return defaultValue;
    }

    /**
     * @param identifier   property ID
     * @param defaultValue default property value
     * @return property if it contains and is Int
     */
    public int getOrDefault(Identifier identifier, int defaultValue) {
        if (properties.containsKey(identifier)) {
            Object result = properties.get(identifier);
            if (result instanceof Number number) return number.intValue();
        }
        return defaultValue;
    }

    /**
     * @param identifier   property ID
     * @param defaultValue default color value
     * @return property if it contains and is Int or String hex
     */
    public int getOrDefaultColor(Identifier identifier, int defaultValue) {
        if (properties.containsKey(identifier)) {
            Object result = properties.get(identifier);
            if (result instanceof String hexString) return Integer.decode(hexString);
            if (result instanceof Number number) return number.intValue();
        }
        return defaultValue;
    }

    /**
     * @param identifier   property ID
     * @param defaultValue default property value
     * @return property if it contains and is Float
     */
    public float getOrDefault(Identifier identifier, float defaultValue) {
        if (properties.containsKey(identifier)) {
            Object result = properties.get(identifier);
            if (result instanceof Number number) return number.floatValue();
        }
        return defaultValue;
    }

    /**
     * @param identifier   property ID
     * @param defaultValue default property value
     * @return property if it contains and is Boolean
     */
    public boolean getOrDefault(Identifier identifier, boolean defaultValue) {
        if (properties.containsKey(identifier)) {
            Object result = properties.get(identifier);
            if (result instanceof Boolean b) return b;
        }
        return defaultValue;
    }

    /**
     * @param identifier   property ID
     * @param defaultValue default property value
     * @return property if it contains and is String
     */
    public String getOrDefault(Identifier identifier, String defaultValue) {
        if (properties.containsKey(identifier)) {
            Object result = properties.get(identifier);
            if (result instanceof String str) return str;
        }
        return defaultValue;
    }

    public int getColorIndex() {
        return getOrDefaultColor(Values.JsonKeys.Base.COLOR, 0xFFFFFFFF);
    }

    public void appendTooltip(List<Text> tooltip) {
        tooltip.addAll(properties.entrySet().stream().map(entry -> Text.literal(entry.getKey().toString()).append(": ").append(entry.getValue().toString())).toList());
    }

    public String joinProperties() {
        return "{" + String.join(", ", properties.entrySet().stream().map(entry -> entry.getKey().toString() + ": " + entry.getValue().toString()).toList()) + "}";
    }

    // Support only 3 primitive object
    private Object primitiveObject(JsonPrimitive primitive) {
        if (primitive.isBoolean()) return primitive.getAsBoolean();
        if (primitive.isNumber()) return primitive.getAsNumber();
        if (primitive.isString()) return primitive.getAsString();
        return null;
    }

    HashMap<Identifier, Object> parseProperties(JsonObject jsonMaterial, HashMap<Identifier, Object> baseMap) {
        if (jsonMaterial == null) return new HashMap<>(baseMap);

        HashMap<Identifier, Object> resultMap = new HashMap<>(baseMap);

        for (String nameSpace : jsonMaterial.keySet()) {
            JsonElement bigObject = jsonMaterial.get(nameSpace); // get upper element

            // if upper element is Object ("namespace": {"path": "value"}) then use it as namespace
            if (bigObject.isJsonObject()) {
                JsonObject bigJsonObject = bigObject.getAsJsonObject();

                // get all lover elements from Object and use they name as path
                for (String path : bigJsonObject.keySet()) {
                    if (!bigJsonObject.get(path).isJsonPrimitive()) continue;


                    Object primitive = primitiveObject(bigJsonObject.getAsJsonPrimitive(path));
                    if (primitive == null) continue;


                    Identifier key = new Identifier(nameSpace.toLowerCase(), path.toLowerCase());
                    if (resultMap.containsKey(key)) resultMap.replace(key, primitive);
                    else resultMap.put(key, primitive);
                }
            }

            // if upper element is Primitive ("namespace:path": "value") then use it as namespace and path
            else if (bigObject.isJsonPrimitive()) {

                Object primitive = primitiveObject(bigObject.getAsJsonPrimitive());
                if (primitive != null) {

                    Identifier key = new Identifier(nameSpace.toLowerCase());
                    if (resultMap.containsKey(key)) resultMap.replace(key, primitive);
                    else resultMap.put(key, primitive);
                }

            }
        }

        return resultMap;
    }

    @Override
    public String toString() {
        return "Properties{" +
                "properties=[" + String.join(", ",
                properties.entrySet().stream().map(entry -> entry.getKey()+"="+entry.getValue()).toList()) +
                "]}";
    }
}