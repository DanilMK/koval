package net.smok.koval.forging;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ActionContext(@Nullable BlockState targetBlock, @Nullable ItemStack targetItem,
                            @Nullable LivingEntity user, @Nullable LivingEntity targetEntity) implements Context {

    @Contract(value = " -> new", pure = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private @Nullable BlockState targetBlock;
        private @Nullable ItemStack targetItem;
        private @Nullable LivingEntity user;
        private @Nullable LivingEntity targetEntity;

        public Builder targetBlock(@Nullable BlockState targetBlock) {
            this.targetBlock = targetBlock;
            return this;
        }

        public Builder targetItem(@Nullable ItemStack targetItem) {
            this.targetItem = targetItem;
            return this;
        }

        public Builder user(@Nullable LivingEntity user) {
            this.user = user;
            return this;
        }

        public Builder targetEntity(@Nullable LivingEntity targetEntity) {
            this.targetEntity = targetEntity;
            return this;
        }

        public ActionContext build() {
            return new ActionContext(targetBlock, targetItem, user, targetEntity);
        }

    }

}
