package net.smok.utility;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public class EntityAttributeBuilder extends ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> {

    public EntityAttributeBuilder() {
    }
}
