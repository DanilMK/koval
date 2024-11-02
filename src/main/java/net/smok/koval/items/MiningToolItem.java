package net.smok.koval.items;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.smok.koval.MiningLevel;
import net.smok.Values;
import net.smok.koval.Assembly;
import net.smok.utility.EmptyToolMaterial;
import net.smok.utility.EntityAttributeBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MiningToolItem extends net.minecraft.item.MiningToolItem implements KovalItem {

    public static final UUID ATTACK_DAMAGE_ID = ATTACK_DAMAGE_MODIFIER_ID;
    public static final UUID ATTACK_SPEED_ID = ATTACK_SPEED_MODIFIER_ID;

    public MiningToolItem() {
        super(0, 0, new EmptyToolMaterial(), TagKey.of(Registry.BLOCK_KEY, new Identifier("")), new FabricItemSettings());
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        KovalStack kovalStack = (KovalStack) (Object) stack;
        if (kovalStack == null || kovalStack.isBroken()) return 0;
        Assembly assembly = kovalStack.getAssembly();
        if (isSuitableFor(stack, state)) return assembly.applyValue(Values.JsonKeys.Mining.SPEED, 0f);
        else return 0.5f;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        KovalStack kovalStack = (KovalStack) (Object) stack;
        if (kovalStack != null) {
            Assembly assembly = kovalStack.getAssembly();
            if (assembly != null) assembly.applyValue(new Identifier("mining:post_hit"), false);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {

        KovalStack kovalStack = (KovalStack) (Object) stack;
        if (kovalStack != null) {
            Assembly assembly = kovalStack.getAssembly();
            if (assembly != null) assembly.applyValue(new Identifier("mining:post_mine"), false);
        }

        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, BlockState state) {
        KovalStack kovalStack = (KovalStack) (Object) stack;
        if (kovalStack == null) return false;
        Assembly assembly = kovalStack.getAssembly();

        int miningLevel = assembly.applyValue(Values.JsonKeys.Mining.LEVEL, 0);
        boolean isEffective = assembly.applyValue(Values.JsonKeys.Mining.EFFECTIVE, false, state);
        return MiningLevel.test(miningLevel, state, isEffective);
    }

    @Override
    public int getDurability(KovalStack stack) {
        Assembly assembly = stack.getAssembly();
        return assembly.applyValue(Values.JsonKeys.Base.DURABILITY, 0);
    }


    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getMaterialAttributeModifiers(EquipmentSlot slot, KovalStack stack) {
        if (slot != EquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot);
        Assembly slots = stack.getAssembly();
        EntityAttributeBuilder builder = new EntityAttributeBuilder();
        if (!stack.isBroken()) {
            float attackDamage = slots.applyValue(Values.JsonKeys.Attack.DAMAGE, 0f);
            float attackSpeed = slots.applyValue(Values.JsonKeys.Attack.SPEED, 0f);


            builder.put(
                    EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    new EntityAttributeModifier(MiningToolItem.ATTACK_DAMAGE_ID, "Tool modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION)
            );
            builder.put(
                    EntityAttributes.GENERIC_ATTACK_SPEED,
                    new EntityAttributeModifier(MiningToolItem.ATTACK_SPEED_ID, "Tool modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION)
            );
        }
        //if (!stack.isBroken()) slots.applyObjectValue(Values.JsonKeys.Base.ATTRIBUTE_MODIFIER, builder);
        return builder.build();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        KovalStack kovalStack = (KovalStack) (Object) stack;
        if (kovalStack == null) return;
        Assembly assembly = kovalStack.getAssembly();
        assembly.appendTooltip(tooltip);
    }
}
