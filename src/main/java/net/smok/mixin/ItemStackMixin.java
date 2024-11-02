package net.smok.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.smok.Values;
import net.smok.koval.Assembly;
import net.smok.koval.items.KovalItem;
import net.smok.koval.items.KovalStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements KovalStack {

    @Shadow public abstract boolean isDamageable();

    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean damage(int amount, Random random, @Nullable ServerPlayerEntity player);

    @Shadow public abstract boolean hasNbt();

    @Shadow @Nullable public abstract NbtCompound getNbt();

    @Shadow public abstract int getDamage();

    @Shadow public abstract int getMaxDamage();

    @Shadow private @Nullable NbtCompound nbt;

    @Shadow public abstract boolean isEmpty();

    @NotNull
    @Override
    public Assembly getAssembly() {
        return nbt != null && nbt.contains(Values.NBT_PARTS, NbtElement.COMPOUND_TYPE) ? Assembly.fromNbt(nbt.getCompound(Values.NBT_PARTS)) : Assembly.EMPTY;
    }

    @NotNull
    @Override
    public List<Identifier> getAssemblyIds() {
        return nbt != null && nbt.contains(Values.NBT_PARTS, NbtElement.COMPOUND_TYPE) ? new ArrayList<>(Assembly.nbtToItemsMap(nbt.getCompound(Values.NBT_PARTS)).values()) : List.of();
    }


    @Override
    public List<Identifier> getModelIds() {
        return !isEmpty() && getItem() instanceof KovalItem item ? item.modelIds(this) : List.of();
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (getItem() instanceof KovalItem kovalItem) cir.setReturnValue(kovalItem.getDurability(this));
    }

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private <T extends LivingEntity> void damage(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        if (!(getItem() instanceof KovalItem)) return;

        if (entity.world.isClient || (entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode)) return;
        if (!damage(amount, entity.getRandom(), entity instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity : null)) return;


        breakCallback.accept(entity);
        Item item = getItem();
        //decrement(1);
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity)entity).incrementStat(Stats.BROKEN.getOrCreateStat(item));
        }

        //setDamage(0);

    }


    @Inject(method = "getAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        NbtCompound nbt = getNbt();
        if (nbt == null) cir.setReturnValue(getItem().getAttributeModifiers(slot));
        else if (!nbt.contains("AttributeModifiers", NbtElement.LIST_TYPE) && getItem() instanceof KovalItem kovalItem)
            cir.setReturnValue(kovalItem.getMaterialAttributeModifiers(slot, this));
    }



}
