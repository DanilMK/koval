package net.smok.koval.forging;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public record AssemblerContext(DefaultedList<ItemStack> table) implements Context {

}
