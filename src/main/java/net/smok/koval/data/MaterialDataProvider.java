package net.smok.koval.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.smok.Values;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialDataProvider implements DataProvider {

    private static final Map<Identifier, MaterialData> dataMap = new HashMap<>();

    private final DataGenerator.PathResolver pathResolver;

    public static final List<Identifier> MiningIds = new ArrayList<>();



    public static final Identifier WOOD = generateMining(new Identifier("koval:wood"), 0xA1662F,
            59, 0, 2, 0, 0, 1, 1);

    public static final Identifier STONE = generateMining(new Identifier("koval:stone"), 0x9A9aA9A,
            131, 1, 4, 1, 0, 1.2f, 0.8f);

    public static final Identifier IRON = generateMining(new Identifier("koval:iron"), 0xD8D8D8,
            250, 2, 6, 2, 1, 1.2f, 0.95f);

    public static final Identifier GOLD = generateMining(new Identifier("koval:gold"), 0xFDFF76,
            32, 0, 12, 0, 0, 0.7f, 1.25f);

    public static final Identifier DIAMOND = generateMining(new Identifier("koval:diamond"), 0x33EBCB,
            1561, 3, 8, 3, 1);

    public static final Identifier NETHERITE = generateMining(new Identifier("koval:netherite"), 0x867B86,
            2031, 4, 9, 4, 1);

    public static final Identifier BONE = generateToolRod(new Identifier("koval:bone"), 0xD8D8D8,
            0.95f, 1f);


    public MaterialDataProvider(DataGenerator.PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    @Override
    public void run(DataWriter writer) throws IOException {

        for (Map.Entry<Identifier, MaterialData> entry : dataMap.entrySet()) {
            Identifier identifier = entry.getKey();
            MaterialData materialData = entry.getValue();
            DataProvider.writeToPath(writer, materialData.toJson(), pathResolver.resolveJson(identifier));
        }
    }

    @Override
    public String getName() {
        return "Materials";
    }

    private static Identifier generateMining(Identifier identifier, int color, int durability, int miningLevel,
                                             float miningSpeed, float attackDamage, float attackSpeed) {
        MiningIds.add(identifier);
        return generate(identifier, new MaterialData()
                .addColor(color)
                .addProperty(Values.Parameters.DURABILITY, durability)
                .addProperty(Values.Parameters.MINING_LEVEL, miningLevel)
                .addProperty(Values.Parameters.MINING_SPEED, miningSpeed)
                .addProperty(Values.Parameters.ATTACK_DAMAGE, attackDamage)
                .addProperty(Values.Parameters.ATTACK_SPEED, attackSpeed)
        );
    }

    private static Identifier generateMining(Identifier identifier, int color, int durability, int miningLevel,
                                             float miningSpeed, float attackDamage, float attackSpeed,
                                             float durabilityMultiplier, float speedMultiplier) {
        MiningIds.add(identifier);
        return generate(identifier, new MaterialData()
                .addColor(color)
                .addProperty(Values.Parameters.DURABILITY, durability)
                .addProperty(Values.Parameters.MINING_LEVEL, miningLevel)
                .addProperty(Values.Parameters.MINING_SPEED, miningSpeed)
                .addProperty(Values.Parameters.ATTACK_DAMAGE, attackDamage)
                .addProperty(Values.Parameters.ATTACK_SPEED, attackSpeed)
                .addProperty(Values.Parameters.DURABILITY_MULTIPLIER, durabilityMultiplier)
                .addProperty(Values.Parameters.SPEED_MULTIPLIER, speedMultiplier)
        );
    }

    private static Identifier generateToolRod(Identifier identifier, int color, float durabilityMultiplier, float speedMultiplier) {
        return generate(identifier, new MaterialData()
                .addColor(color)
                .addProperty(Values.Parameters.DURABILITY_MULTIPLIER, durabilityMultiplier)
                .addProperty(Values.Parameters.SPEED_MULTIPLIER, speedMultiplier)
        );
    }

    private static Identifier generate(Identifier identifier, MaterialData material) {
        dataMap.put(identifier, material);
        return identifier;
    }




}
