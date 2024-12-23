package net.smok.mixin;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.world.World;
import net.smok.koval.items.KovalItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(RepairItemRecipe.class)
public class RepairRecipeMixin {

    @Inject(method = "matches(Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/world/World;)Z", at = @At("HEAD"), cancellable = true)
    private void match(CraftingInventory craftingInventory, World world, CallbackInfoReturnable<Boolean> cir) {

        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof KovalItem) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
