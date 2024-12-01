package net.smok.koval.forging;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public record ActionContext(@Nullable BlockState targetBlock, @Nullable LivingEntity user, @Nullable LivingEntity targetEntity) implements Context {
}
