package net.smok.koval.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.smok.Values;
import net.smok.koval.forging.*;
import net.smok.utility.Vec2Int;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ShapeDataProvider implements DataProvider {

    private static final Map<Identifier, ShapeData> dataMap = new HashMap<>();

    private final DataGenerator.PathResolver pathResolver;


    public static final Identifier ATTACK_DAMAGE = Values.Parameters.ATTACK_DAMAGE;
    public static final Identifier ATTACK_SPEED = Values.Parameters.ATTACK_SPEED;
    public static final Identifier MINING_SPEED = Values.Parameters.MINING_SPEED;
    public static final Identifier EFFECTIVE = Values.Parameters.EFFECTIVE_BLOCKS;
    public static final Identifier DURABILITY = Values.Parameters.DURABILITY;
    public static final Identifier LEVEL = Values.Parameters.MINING_LEVEL;
    public static final Identifier REPAIR_MATERIAL = Values.Parameters.REPAIR_MATERIAL;

    private static final Identifier MINING_HEAD = generate(new Identifier(Values.MOD_ID, "mining_head"), new ShapeData()
            .addProperty(ATTACK_DAMAGE, 6)
            .addProperty(ATTACK_SPEED, -3.2f)
            .addProperty(MINING_SPEED, 0)
            .addProperty(EFFECTIVE, "mineable/axe")
            .addRecipe(FunctionParameter.of(Functions.Assemblers.HAS_ANY_ITEM, PrimitiveParameter.of("koval:parts/tool_rod")), Values.MINING_TOOL)

            .addParameter(DURABILITY, PropertyParameter.MATERIAL)
            .addParameter(LEVEL, PropertyParameter.MATERIAL)
            .addParameter(MINING_SPEED, Functions.Numbers.ADD, PropertyParameter.material(), PropertyParameter.shape())
            .addParameter(ATTACK_SPEED, Functions.Numbers.ADD, PropertyParameter.material(), PropertyParameter.shape())
            .addParameter(ATTACK_DAMAGE, Functions.Numbers.ADD, PropertyParameter.material(), PropertyParameter.shape())
            .addParameter(EFFECTIVE, Functions.Blocks.BLOCK_STATE_IS_IN, FunctionParameter.of(Functions.Action.TARGET_BLOCK), PropertyParameter.shape())
            .addParameter(REPAIR_MATERIAL, Functions.Items.ITEM_STACK_IS_IN, FunctionParameter.of(Functions.Action.TARGET_ITEM), PropertyParameter.material())
    );

    private static final Identifier AXE_HEAD = generateHead("axe_head", 6, -3.2f, 0, "mineable/axe");

    private static final Identifier PICKAXE_HEAD = generateHead("pickaxe_head", 1, 0, 0, "mineable/pickaxe");

    private static final Identifier SHOVEL_HEAD = generateHead("shovel_head", 1.5f, -3, 0, "mineable/shovel");

    private static final Identifier HOE_HEAD = generateHead("hoe_head", -2, -3, 0, "mineable/hoe");

    private static final Identifier TOOL_ROD = generate(new Identifier(Values.MOD_ID, "tool_rod"), new ShapeData()
            .addPart(MaterialDataProvider.BONE, Items.BONE)
            .addPart(MaterialDataProvider.WOOD, Items.STICK)
            .addDefaultPart(MaterialDataProvider.STONE)
            .addDefaultPart(MaterialDataProvider.GOLD)
            .addDefaultPart(MaterialDataProvider.IRON)

            .addParameter(MINING_SPEED, Functions.Numbers.MULTIPLY,
                    PointerParameter.of(Number.class, Vec2Int.UP),
                    PropertyParameter.material(Values.Parameters.SPEED_MULTIPLIER))
            .addParameter(DURABILITY, Functions.Numbers.MULTIPLY,
                    PointerParameter.of(Number.class, Vec2Int.UP),
                    PropertyParameter.material(Values.Parameters.DURABILITY_MULTIPLIER))
            .addParameter(Values.Parameters.FRAGILE, PropertyParameter.material())
    );


    public ShapeDataProvider(DataGenerator.PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    @Override
    public void run(DataWriter writer) throws IOException {

        for (Map.Entry<Identifier, ShapeData> entry : dataMap.entrySet()) {
            Identifier identifier = entry.getKey();
            ShapeData shapeData = entry.getValue();
            DataProvider.writeToPath(writer, shapeData.toJson(), pathResolver.resolveJson(identifier));
        }
    }

    @Override
    public String getName() {
        return "Shapes";
    }

    private static Identifier generateHead(String path, float attackDamage, float attackSpeed, float miningSpeed, String effective) {
        return generate(new Identifier(Values.MOD_ID, path), new ShapeData(MINING_HEAD)
                .addProperty(ATTACK_DAMAGE, attackDamage)
                .addProperty(ATTACK_SPEED, attackSpeed)
                .addProperty(MINING_SPEED, miningSpeed)
                .addProperty(EFFECTIVE, effective)
                .addAllDefaultPart(MaterialDataProvider.MiningIds));
    }

    private static Identifier generate(Identifier identifier, ShapeData shape) {
        dataMap.put(identifier, shape);
        return identifier;
    }

}
