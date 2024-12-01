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

public class ShapeDataProvider implements DataProvider {

    private static final Map<Identifier, ShapeData> dataMap = new HashMap<>();

    private final DataGenerator.PathResolver pathResolver;


    public static final Identifier ATTACK_DAMAGE = Values.Parameters.ATTACK_DAMAGE;
    public static final Identifier ATTACK_SPEED = Values.Parameters.ATTACK_SPEED;
    public static final Identifier MINING_SPEED = Values.Parameters.MINING_SPEED;
    public static final Identifier EFFECTIVE = Values.Parameters.EFFECTIVE_BLOCKS;
    public static final Identifier DURABILITY = Values.Parameters.DURABILITY;
    public static final Identifier LEVEL = Values.Parameters.MINING_LEVEL;

    private static final Identifier MINING_HEAD = generate(new Identifier(Values.MOD_ID, "mining_head"), new ShapeData()
            .addProperty(ATTACK_DAMAGE, 6)
            .addProperty(ATTACK_SPEED, -3.2f)
            .addProperty(MINING_SPEED, 0)
            .addProperty(EFFECTIVE, "mineable/axe")
            .addRecipe(FunctionParameter.of(Functions.Assemblers.HAS_ANY_ITEM, PrimitiveParameter.of("koval:parts/tool_rod")), Values.MINING_TOOL)

            .addParameter(DURABILITY, PropertyParameter.MATERIAL)
            .addParameter(LEVEL, PropertyParameter.MATERIAL)
            .addParameter(MINING_SPEED, Functions.Numbers.ADD, PropertyParameter.MATERIAL, PropertyParameter.SHAPE)
            .addParameter(ATTACK_SPEED, Functions.Numbers.ADD, PropertyParameter.MATERIAL, PropertyParameter.SHAPE)
            .addParameter(ATTACK_DAMAGE, Functions.Numbers.ADD, PropertyParameter.MATERIAL, PropertyParameter.SHAPE)
            .addParameter(EFFECTIVE, Functions.Blocks.BLOCK_STATE_IS_IN, FunctionParameter.of(Functions.Blocks.TARGET_BLOCK_STATE), PropertyParameter.SHAPE)
    );

    private static final Identifier AXE_HEAD = generate(new Identifier(Values.MOD_ID, "axe_head"), new ShapeData(MINING_HEAD)
            .addProperty(ATTACK_DAMAGE, 6)
            .addProperty(ATTACK_SPEED, -3.2)
            .addProperty(MINING_SPEED, 0)
            .addProperty(EFFECTIVE, "mineable/axe")
            .addAllDefaultPart(MaterialDataProvider.MiningIds)
    );

    private static final Identifier PICKAXE_HEAD = generate(new Identifier(Values.MOD_ID, "pickaxe_head"), new ShapeData(MINING_HEAD)
            .addProperty(ATTACK_DAMAGE, 1)
            .addProperty(ATTACK_SPEED, 0)
            .addProperty(MINING_SPEED, 0)
            .addProperty(EFFECTIVE, "mineable/pickaxe")
            .addAllDefaultPart(MaterialDataProvider.MiningIds)
    );

    private static final Identifier SHOVEL_HEAD = generate(new Identifier(Values.MOD_ID, "shovel_head"), new ShapeData(MINING_HEAD)
            .addProperty(ATTACK_DAMAGE, 1.5)
            .addProperty(ATTACK_SPEED, -3)
            .addProperty(MINING_SPEED, 0)
            .addProperty(EFFECTIVE, "mineable/shovel")
            .addAllDefaultPart(MaterialDataProvider.MiningIds)
    );

    private static final Identifier HOE_HEAD = generate(new Identifier(Values.MOD_ID, "hoe_head"), new ShapeData(MINING_HEAD)
            .addProperty(ATTACK_DAMAGE, -2)
            .addProperty(ATTACK_SPEED, -3)
            .addProperty(MINING_SPEED, 0)
            .addProperty(EFFECTIVE, "mineable/hoe")
            .addAllDefaultPart(MaterialDataProvider.MiningIds)
    );

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


    private static Identifier generate(Identifier identifier, ShapeData shape) {
        dataMap.put(identifier, shape);
        return identifier;
    }

}
