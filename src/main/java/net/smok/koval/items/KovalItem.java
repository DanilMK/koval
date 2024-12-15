package net.smok.koval.items;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.koval.Assembly;

import java.util.ArrayList;
import java.util.List;

public interface KovalItem {

    Multimap<EntityAttribute, EntityAttributeModifier> getMaterialAttributeModifiers(EquipmentSlot slot, KovalStack stack);

    int getDurability(KovalStack stack);
    
    default List<Identifier> modelIds(KovalStack itemStack) {
        return itemStack.getNbt() != null && itemStack.getNbt().contains(Values.Json.PARTS, NbtElement.COMPOUND_TYPE) ?
                new ArrayList<>(Assembly.nbtToItemsMap(itemStack.getNbt().getCompound(Values.Json.PARTS)).values()) : List.of();
    }
}
