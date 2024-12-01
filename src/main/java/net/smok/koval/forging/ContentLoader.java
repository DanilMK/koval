package net.smok.koval.forging;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.smok.Debug;
import net.smok.koval.Koval;
import net.smok.koval.KovalRegistry;
import net.smok.Values;
import net.smok.koval.Material;
import net.smok.koval.Part;
import net.smok.koval.Shape;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;

public class ContentLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier(Values.MOD_ID, "content_loader");
    }


    @Override
    public void reload(ResourceManager manager) {


//        Debug.log("Load parts... ");
        HashMap<Identifier, JsonObject> materialsJson = readResources(manager, "koval_forging/materials");
        HashMap<Identifier, JsonObject> shapesJson = readResources(manager, "koval_forging/shapes");
/*
        Debug.log(MessageFormat.format("Parts are loaded: ({0}) materials, ({1}) shapes. ",
                materialsJson.size(), shapesJson.size()));*/

        // Left - parent, Right - child
        List<Identifier> sortedMaterials = sortObjects(materialsJson);
        List<Identifier> sortedShapes = sortObjects(shapesJson);
/*
        Debug.log(MessageFormat.format("Parts are sorted: ({0}) materials, ({1}) shapes. \nAll shapes: [{2}] ",
                sortedMaterials.size(), sortedShapes.size(), String.join(" > ",
                        sortedShapes.stream().map(Identifier::toString).toList())));*/

        for (Identifier ownId : sortedMaterials) {
            Identifier parentId = getParentId(materialsJson.get(ownId));
            Material parent = KovalRegistry.MATERIALS.get(parentId);
            Registry.register(KovalRegistry.MATERIALS, ownId, parent.createChild(materialsJson.get(ownId)));
        }

        HashMap<Identifier, Shape> registeredShapes = new HashMap<>();
        for (Identifier ownId : sortedShapes) {
            Identifier parentId = getParentId(shapesJson.get(ownId));
            Shape parent = KovalRegistry.SHAPES.get(parentId);
            //Debug.log("Create "+ownId+" parameters child: "+shapesJson.get(ownId).get(Values.JsonKeys.PARAMETERS));

            Shape child = Registry.register(KovalRegistry.SHAPES, ownId, parent.createChild(shapesJson.get(ownId)));
            registeredShapes.put(ownId, child);
        }

        registeredShapes.forEach((identifier, shape) ->
                shape.foreachParts(identifier, (material, itemId) -> {
                    if (Registry.ITEM.containsId(itemId)) {
                        Part part = new Part(shape, material);
                        Registry.register(KovalRegistry.PARTS, itemId, part);
                    }
                }));

        Debug.log(MessageFormat.format("Successful load \n   Materials: [{0}]\n   Shapes: [{1}]\n   Parts: [{2}]",
                String.join(", ", KovalRegistry.MATERIALS.getEntrySet().stream().map(entry -> entry.getKey().toString()+"= "+entry.getValue()).toList()),
                String.join(", ", KovalRegistry.SHAPES.getEntrySet().stream().map(entry -> entry.getKey().toString()+"= "+entry.getValue()).toList()),
                String.join(", ", KovalRegistry.PARTS.getEntrySet().stream().map(entry -> entry.getKey().toString()+"= "+entry.getValue()).toList())));

    }

    private HashMap<Identifier, JsonObject> readResources(ResourceManager manager, String startPath) {
        HashMap<Identifier, JsonObject> result = new HashMap<>();

        for(Map.Entry<Identifier, Resource> entry : manager.findResources(startPath,
                path -> path.getPath().endsWith(".json")).entrySet()) {

            try (InputStream stream = entry.getValue().getInputStream()) {
                JsonObject materialObject = JsonHelper.deserialize(new InputStreamReader(stream));
                String path = entry.getKey().getPath();
                Identifier id = new Identifier(entry.getKey().getNamespace(), path.substring(path.lastIndexOf("/")+1, path.lastIndexOf(".")));

                result.put(id, materialObject);
            } catch (Exception e) {
                Koval.LOGGER.error("Error occurred while loading resource json {}\n", entry.getKey(), e);
            }
        }

        return result;
    }


    //Left - parent, Right - child
    private static List<Identifier> sortObjects(HashMap<Identifier, JsonObject> objectMap) {
        ArrayList<Identifier> sortedList = new ArrayList<>();
        Stack<Identifier> stack = new Stack<>();
        stack.addAll(objectMap.keySet());

        while (!stack.isEmpty()) {
            Identifier next = stack.pop();
            if (sortedList.contains(next)) continue;

            Identifier parentId = getParentId(objectMap.get(next));

            if (!sortedList.contains(parentId) && objectMap.containsKey(parentId)) {
                stack.push(next);
                stack.push(parentId);
                continue;
            }

            sortedList.add(next);

        }
        
        return sortedList;
    }

    private static @NotNull Identifier getParentId(JsonObject jsonObject) {
        if (jsonObject.has("parent") && jsonObject.get("parent").isJsonPrimitive())
            return new Identifier(jsonObject.getAsJsonPrimitive("parent").getAsString());
        return new Identifier("base");
    }


}
