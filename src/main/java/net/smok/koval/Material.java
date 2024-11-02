package net.smok.koval;

import com.google.gson.JsonObject;
import net.smok.Values;
import net.smok.koval.forging.ParametersGroup;
import net.smok.utility.SavableObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Material implements SavableObject<Material> {

    public static final Material BASE_MATERIAL = new Material(Properties.EMPTY, new ParametersGroup(new HashMap<>()));

    private final Properties properties;
    private final ParametersGroup parameters;

    public Material(@NotNull Properties properties, @NotNull ParametersGroup parameters) {
        this.properties = properties;
        this.parameters = parameters;
    }

    @Override
    public Material createChild(JsonObject json) {
        Properties propertiesChild = properties.createChild(json.getAsJsonObject(Values.JsonKeys.PROPERTIES));
        ParametersGroup parametersChild = parameters.createChild(json.getAsJsonObject(Values.JsonKeys.PARAMETERS));
        return new Material(
                propertiesChild,
                parametersChild);
    }

    @Override
    public String toString() {
        return "Material { " +
                "properties=" + properties +
                ", parameters=" + parameters +
                '}';
    }

    public Properties getProperties() {
        return properties;
    }

    public ParametersGroup getParameters() {
        return parameters;
    }

    public int color() {
        return properties.getColorIndex();
    }

    public int getColorIndex() {
        return properties.getColorIndex();
    }
}
