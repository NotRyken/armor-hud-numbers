package com.notryken;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmorHudNumbersClient implements ClientModInitializer {
    public static final String MOD_ID = "armor-hud-numbers";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}
