package net.smok.koval;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.smok.Debug;
import net.smok.Values;
import net.smok.koval.forging.Functions;
import net.smok.koval.forging.ContentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Koval implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("koval");

	@Override
	public void onInitialize() {

		LOGGER.info("Initialize Koval Mod...");
		Values.init();
		Debug.log("Done!");
		Functions.init();
		Debug.log("Done!");


		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ContentLoader());
	}
}