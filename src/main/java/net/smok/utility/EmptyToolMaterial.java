package net.smok.utility;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class EmptyToolMaterial implements ToolMaterial {
    @Override
    public int getDurability() {
        return 1;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 0;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
}
