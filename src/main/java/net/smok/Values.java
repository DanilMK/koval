package net.smok;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.text.MessageFormat;

public final class Values {
    public static final String MOD_ID = "koval";


    public static final ItemGroup PARTS_CREATIVE_TAB = FabricItemGroupBuilder.create(new Identifier(Values.MOD_ID, "parts"))
            .icon(Items.STICK::getDefaultStack).build();

    public static final AssemblerBlockFactory ASSEMBLER_3X3_FACTORY = new AssemblerBlockFactory(
            new Identifier(MOD_ID, "assembler_table"), 3, 3,
            AbstractBlock.Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD),
            new Item.Settings().group(PARTS_CREATIVE_TAB)).register();

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


    public static final class Json {


        public static final String PARENT = "parent";
        public static final String PARTS = "parts";
        public static final String PROPERTIES = "properties";
        public static final String PARAMETERS = "parameters";
        public static final String RECIPE = "recipe";


        public static final String FUNCTION_ID = "function_id";
        public static final String PROPERTY_TYPE = "parameter";
        public static final String ID = "id";
        public static final String ID1 = "identifier";


        public static @NotNull IllegalArgumentException exceptionInvalidParametersAmount(int expected, int provided) {
            return new IllegalArgumentException(MessageFormat.format("Invalid parameters amount. Expected: {0}, provided: {1}.", expected, provided));
        }

        public static @NotNull JsonParseException exceptionUnknownFunctionType(Identifier providedType) {
            return new JsonParseException(MessageFormat.format("Unknown function type: {0}.", providedType));
        }

        public static @NotNull JsonParseException exceptionInvalidPropertyType(String type) {
            return new JsonParseException(MessageFormat.format("Invalid parameter type. Expected #shape or #material, provided parameter: {0}", type));
        }

        public static @NotNull JsonParseException exceptionInvalidParameterType(@Nullable Type type, String json) {
            return new JsonParseException(MessageFormat.format("Invalid parameter type. Expected type: {0}, provided parameter: {1}", type, json));
        }

        public static @NotNull JsonParseException exceptionInvalidFunctionType(Type typeA, Type typeB) {
            return new JsonParseException(MessageFormat.format("Invalid function parameter type. Expected type: {0}, function type: {1}", typeA, typeB));
        }

        public static @NotNull JsonParseException exceptionUnexpectedParameter(Identifier identifier, JsonElement json, @Nullable Type type) {
            return new JsonParseException(MessageFormat.format("Unexpected parameter exception. ID: {0}, Expected type: {1}, provided parameter: {2}", identifier, type, json));
        }

        public static @NotNull UnsupportedOperationException exceptionInitializeValue(Identifier identifier) {
            return new UnsupportedOperationException(MessageFormat.format("First, parameter {0} must be initialized.", identifier));
        }

        public static @NotNull UnsupportedOperationException exceptionInvalidPrimitive(JsonElement parameter) {
            return new UnsupportedOperationException(MessageFormat.format("Invalid primitive parameter: {0}", parameter));
        }
    }

    public static void init() {
        Debug.log("Load Koval Mod Values...");
    }

    public static final class Parameters {
        public static final Identifier MINING_SPEED = new Identifier("mining", "speed");
        public static final Identifier MINING_LEVEL = new Identifier("mining", "level");
        public static final Identifier EFFECTIVE_BLOCKS = new Identifier("mining", "effective_blocks");

        public static final Identifier ATTACK_SPEED = new Identifier("attack", "speed");
        public static final Identifier ATTACK_DAMAGE = new Identifier("attack", "damage");

        public static final Identifier DURABILITY = new Identifier("base", "durability");
        public static final Identifier COLOR = new Identifier("base", "color");
        public static final Identifier REPAIR_MATERIAL = new Identifier("base", "repair_material");
        public static final Identifier ATTRIBUTE_MODIFIER = new Identifier("base", "attribute_modifier");

        public static final Identifier DURABILITY_MULTIPLIER = new Identifier("tool_rod", "durability_multiplier");
        public static final Identifier SPEED_MULTIPLIER = new Identifier("tool_rod", "speed_multiplier");

    }
}
