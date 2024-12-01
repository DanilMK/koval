package net.smok.koval.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataGenerator;

public class KovalDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		DataGenerator.PathResolver materialPath = fabricDataGenerator.createPathResolver(DataGenerator.OutputType.DATA_PACK, "koval_forging/materials");
		DataGenerator.PathResolver shapePath = fabricDataGenerator.createPathResolver(DataGenerator.OutputType.DATA_PACK, "koval_forging/shapes");
        fabricDataGenerator.addProvider(new MaterialDataProvider(materialPath));
		fabricDataGenerator.addProvider(new ShapeDataProvider(shapePath));
	}

}
