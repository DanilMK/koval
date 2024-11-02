package net.smok;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.smok.koval.assembler.AssemblerBlockFactory;
import net.smok.koval.items.MiningToolItem;
import net.smok.koval.items.PartsFactory;

public class Values {
    public static final String MOD_ID = "koval";

    public static final AssemblerBlockFactory ASSEMBLER_3X3_FACTORY = new AssemblerBlockFactory(
            new Identifier(MOD_ID, "assembler_table"), 3, 3,
            AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD),
            new Item.Settings()).register();

    public static final ItemGroup PARTS_CREATIVE_TAB = FabricItemGroupBuilder.create(new Identifier(Values.MOD_ID, "parts"))
            .icon(Items.STICK::getDefaultStack).build();

    public static final MiningToolItem MINING_TOOL = Registry.register(Registry.ITEM,
            new Identifier(MOD_ID, "mining_tool"), new MiningToolItem());

    public static final PartsFactory MINING_PARTS_FACTORY = new PartsFactory(new FabricItemSettings(), "_", new String[][] {
            new String[] {"diamond", "gold", "iron", "netherite", "stone", "wood"},
            new String[] {"pickaxe_head", "axe_head", "hoe_head", "shovel_head"}
    }).register();

    public static final PartsFactory TOOL_ROD_FACTORY = new PartsFactory(new FabricItemSettings(), "_", new String[][]{
            new String[] {"gold", "iron", "stone"},
            new String[] {"tool_rod"}
    }).register();


    public static final String FILE_FILE_READ_EXCEPTION = "An exception occurred while reading the {0} file. This file will be skipped.\n";
    public static final String EXCEPTION_UNKNOWN_PARENT = "{0}: Cannot find parent {1} for provided part.";
    public static final String EXCEPTION_UNKNOWN_MATERIAL = "{0}: Cannot find material {1} for provided part.";
    public static final String EXCEPTION_EMPTY_PARENT = "{0}: Parent field can't be empty";
    public static final String EXCEPTION_ASSEMBLY_NULL_PART = "Incorrect part identifier {0} return Null part.";
    public static final String EXCEPTION_VEC_OUT_OF_BOUNDS = "Position {0} out of bounds: min = {1}, max = {2}";

    public static final String MATERIALS_DIR = "materials";
    public static final String SHAPES_DIR = "shapes";
    public static final String NBT_PARTS = "Parts";
    public static final String INVALID_VALUE_TYPE_EXCEPTION = "The expected value type is {0}, but the provided value is {1}";


    public static class JsonKeys {


        public static class Mining {

            public static final Identifier SPEED = new Identifier("mining", "speed");
            public static final Identifier LEVEL = new Identifier("mining", "level");
            public static final Identifier EFFECTIVE = new Identifier("mining", "effective_blocks");
            public static final Identifier SUITABLE = new Identifier("mining", "is_suitable_for");
        }
        public static class Attack {

            public static final Identifier SPEED = new Identifier("attack", "speed");
            public static final Identifier DAMAGE = new Identifier("attack", "damage");
        }
        public static class Base {
            public static final Identifier DURABILITY = new Identifier("base", "durability");
            public static final Identifier COLOR = new Identifier("base", "color");
            public static final Identifier REPAIR_MATERIAL = new Identifier("base", "repair_material");
            public static final Identifier ATTRIBUTE_MODIFIER = new Identifier("base", "attribute_modifier");
        }
        public static class ToolRod {
            public static final Identifier DURABILITY_MULTIPLIER = new Identifier("tool_rod", "durability_multiplier");
            public static final Identifier SPEED_MULTIPLIER = new Identifier("tool_rod", "speed_multiplier");
        }

        public static final String PARENT = "parent";
        public static final String IDS = "parts";
        public static final String PROPERTIES = "properties";
        public static final String PARAMETERS = "parameters";
    }

    public static void init() {
        Debug.log("Load Koval Mod Values...");
    }

}
