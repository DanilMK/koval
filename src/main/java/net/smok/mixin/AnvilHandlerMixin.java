package net.smok.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.smok.Debug;
import net.smok.koval.items.KovalItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilHandlerMixin {

    @Shadow
    private int repairItemUsage;

    @Shadow
    public static int getNextCost(int cost) {
        return 0;
    }


    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 5))
    private void resetLevelCost(Property levelCost, int defaultCost, @Local(ordinal = 0) ItemStack main, @Local(ordinal = 1) int j, @Local(ordinal = 2) int k) {
        // if only repair - 0, if with repair - prev repair

        if (main.getItem() instanceof KovalItem && repairItemUsage > 0)
             levelCost.set(k == 0 ? 0 : j);
        else levelCost.set(defaultCost);
    }

    @ModifyConstant(method = "canTakeOutput", constant = @Constant(intValue = 0))
    private int canTakeOutput(int constant) {
        // if it is repairing, then we can take output even 0 level cost
        return repairItemUsage > 0 ? -1 : constant;
    }

    @Redirect(method = "updateResult", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"
        )
    )
    private int resetNextCost(int cost, @Local(ordinal = 0) ItemStack main) {
        Debug.log("Item "+main + " usage "+repairItemUsage);
        if (main.getItem() instanceof KovalItem) return cost;
        return getNextCost(cost);
    }

    @ModifyArg(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1)
    )
    private Item breakRepair(Item item) {
        if (item instanceof KovalItem) return Items.AIR;
        return item;
    }

}
