package net.smok;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class KovalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		HandledScreens.register(Values.ASSEMBLER_3X3_FACTORY.getHandlerType(), AssemblerTableScreen::new);
	}
}