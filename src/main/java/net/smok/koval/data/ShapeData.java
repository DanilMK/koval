package net.smok.koval.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.smok.Values;
import net.smok.koval.forging.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShapeData {

    @Nullable private final Identifier parent;
    private final ParametersGroup parameters = new ParametersGroup(new HashMap<>());
    private final HashMap<String, JsonPrimitive> properties = new HashMap<>();
    private final HashMap<String, String> parts = new HashMap<>();
    private final List<AssembleRecipe> recipes = new ArrayList<>();


    public ShapeData() {
        this.parent = null;
    }

    public ShapeData(@Nullable Identifier parent) {
        this.parent = parent;
    }


    public ShapeData addRecipe(AssembleRecipe recipe) {
        recipes.add(recipe);
        return this;
    }

    public ShapeData addRecipe(AbstractParameter condition, Item result) {
        recipes.add(new AssembleRecipe(condition, result));
        return this;
    }

    public ShapeData addAlwaysResultRecipe(Item result) {
        recipes.add(new AssembleRecipe(PrimitiveParameter.of(true), result));
        return this;
    }

    public ShapeData addPart(Identifier material, Item part) {
        return addPart(material, Registry.ITEM.getId(part));
    }

    public ShapeData addPart(Identifier material, Identifier part) {
        String key = material.toString();
        if  (parts.containsKey(key))
             parts.replace(key, part.toString());
        else parts.put(key, part.toString());
        return this;
    }

    public ShapeData addDefaultPart(Identifier material) {
        return addDefaultPart(material, Values.MOD_ID);
    }

    public ShapeData addDefaultPart(Identifier material, String nameSpace) {
        String key = material.toString();
        if  (parts.containsKey(key))
            parts.replace(key, nameSpace + ":#material_#shape");
        else parts.put(key, nameSpace + ":#material_#shape");
        return this;
    }

    public ShapeData addAllDefaultPart(List<Identifier> materials) {
        materials.forEach(this::addDefaultPart);
        return this;
    }

    public ShapeData addAllDefaultPart(List<Identifier> materials, String nameSpace) {
        materials.forEach(material -> addDefaultPart(material, nameSpace));
        return this;
    }



    public <T, U, R> ShapeData addParameter(Identifier identifier, BiKovalFunction<T, U, R> function, AbstractParameter<T> fist, AbstractParameter<U> second) {
        return addParameter(identifier, FunctionParameter.of(function, fist, second));
    }

    public <T, R> ShapeData addParameter(Identifier identifier, MonoKovalFunction<T, R> function, AbstractParameter<T> parameter) {
        return addParameter(identifier, FunctionParameter.of(function, parameter));
    }

    public <R> ShapeData addParameter(Identifier identifier, KovalFunction<R> function, AbstractParameter<?>... params) {
        return addParameter(identifier, FunctionParameter.of(function, params));
    }

    public ShapeData addParameter(Identifier identifier, AbstractParameter<?> parameter) {

        parameters.add(identifier, parameter);
        return this;
    }




    public ShapeData addColor(int color) {
        return addProperty(Values.Parameters.COLOR, "0x" + Integer.toHexString(color).toUpperCase());
    }

    public ShapeData addProperty(Identifier id, boolean property) {
        return addProperty(id.toString(), new JsonPrimitive(property));
    }

    public ShapeData addProperty(Identifier id, Number property) {
        return addProperty(id.toString(), new JsonPrimitive(property));
    }

    public ShapeData addProperty(Identifier id, String property) {
        return addProperty(id.toString(), new JsonPrimitive(property));
    }

    protected ShapeData addProperty(String id, JsonPrimitive property) {
        if  (properties.containsKey(id))
             properties.replace(id, property);
        else properties.put(id, property);
        return this;
    }

    public JsonObject toJson() {
        JsonObject shape = new JsonObject();
        JsonObject propertiesJson = new JsonObject();
        JsonObject partsJson = new JsonObject();
        JsonArray recipesJson = new JsonArray();

        properties.forEach(propertiesJson::add);
        parts.forEach(partsJson::addProperty);
        recipes.forEach(recipe -> recipesJson.add(recipe.toJson()));

        if (parent != null) shape.add(Values.Json.PARENT, new JsonPrimitive(parent.toString()));
        if (!properties.isEmpty()) shape.add(Values.Json.PROPERTIES, propertiesJson);
        if (!parts.isEmpty()) shape.add(Values.Json.PARTS, partsJson);
        if (!recipes.isEmpty()) shape.add(Values.Json.RECIPE, recipesJson);
        if (!parameters.isEmpty()) shape.add(Values.Json.PARAMETERS, parameters.toJson());


        return shape;
    }

}
