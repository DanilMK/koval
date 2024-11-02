package net.smok.koval;

import com.google.common.collect.ImmutableList;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MiningLevel {

    private static final List<Level> levels = ImmutableList.of(
            new Level(MiningLevels.DIAMOND, BlockTags.NEEDS_DIAMOND_TOOL),
            new Level(MiningLevels.IRON, BlockTags.NEEDS_IRON_TOOL),
            new Level(MiningLevels.STONE, BlockTags.NEEDS_STONE_TOOL)
    );

    public static boolean test(int toolLevel, BlockState blockState, TagKey<Block> zeroLevel) {
        for (Level level : levels)
            if (blockState.isIn(level.blockTags)) {
                return toolLevel >= level.level;
            }
        return blockState.isIn(zeroLevel);
    }

    public static boolean test(int toolLevel, BlockState blockState, boolean isEffective) {
        for (Level level : levels)
            if (blockState.isIn(level.blockTags)) {
                return toolLevel >= level.level;
            }
        return isEffective;
    }


    private record Level(int level, TagKey<Block> blockTags) implements Comparable<Level> {
        @Override
        public int compareTo(@NotNull MiningLevel.Level o) {
            return Integer.compare(level, o.level);
        }
    }
}
