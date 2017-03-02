package com.mcmoddev.buckets.util;

import java.io.File;

import com.mcmoddev.buckets.MMDBuckets;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {
	private static Configuration configuration;
	private static final String CONFIG_FILE = "config/MMDBuckets.cfg";
	private static final String MATERIALS_CAT = "Metals";

	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e) {
		if (e.getModID().equals(MMDBuckets.MODID)) {
			init();
		}
	}
	
	public static void init() {
		if (configuration == null) {
			configuration = new Configuration(new File(CONFIG_FILE));
		}

	}
		
	public static void postInit() {
		// do nothing - we have nothing to do
	}
	
	public static boolean get( String name ) {
		return configuration.getBoolean("Enable"+name, MATERIALS_CAT, true, "Enable buckets made of "+name);
	}
}
