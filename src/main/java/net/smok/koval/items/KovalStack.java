package net.smok.koval.items;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.smok.koval.Assembly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface KovalStack {


    List<Identifier> getAssemblyIds();

    List<Identifier> getModelIds();


    @NotNull
    Assembly getAssembly();


    default boolean isBroken() {
        return getMaxDamage() <= getDamage();
    }

    int getMaxDamage();

    int getDamage();

    NbtCompound getNbt();

}
