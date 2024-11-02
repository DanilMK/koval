package net.smok.koval.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.smok.Values;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PartsFactory(Map<Identifier, PartItem> parts) {

    public PartsFactory(FabricItemSettings settings, String delimiter, String[][] elements) {
        this(convert(settings, combineElements(delimiter, elements)));
    }

    public PartsFactory register() {
        parts.forEach((identifier, partItem) ->  Registry.register(Registry.ITEM, identifier, partItem));
        return this;
    }

    private static @NotNull Map<Identifier, PartItem> convert(FabricItemSettings settings, List<String> names) {
        HashMap<Identifier, PartItem> result = new HashMap<>();
        for (String name : names) {
            Identifier id = new Identifier(Values.MOD_ID, name);
            result.put(id, new PartItem(settings, id));
        }
        return result;
    }

    private static @NotNull List<String> combineElements(String delimiter, String[][] elements) {
        List<String> result = new ArrayList<>();

        for (String str0 : elements[0]) {

            if (elements.length == 1) result.add(str0);
            else for (String str1 : elements[1]) {

                if (elements.length == 2) result.add(str0 + delimiter + str1);
                else for (String str2 : elements[2]) {

                    if (elements.length == 3) result.add(str0 + delimiter + str1 + delimiter + str2);
                }
            }
        }
        return result;
    }
}
