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
		
		Options.enableAdamantine = configuration.getBoolean("EnableAdamantine", MATERIALS_CAT, true, "Enable Adamantine Buckets");
        Options.enableAluminum = configuration.getBoolean("EnableAluminum", MATERIALS_CAT, true, "Enable Aluminum Buckets");
        Options.enableAluminumBrass = configuration.getBoolean("EnableAluminumBrass", MATERIALS_CAT, true, "Enable Aluminum Brass Buckets");
		Options.enableAntimony = configuration.getBoolean("EnableAntimony", MATERIALS_CAT, true, "Enable Antimony Buckets");
		Options.enableAquarium = configuration.getBoolean("EnableAquarium", MATERIALS_CAT, true, "Enable Aquarium Buckets");
		Options.enableBismuth = configuration.getBoolean("EnableBismuth", MATERIALS_CAT, true, "Enable Bismuth Buckets");
		Options.enableBrass = configuration.getBoolean("EnableBrass", MATERIALS_CAT, true, "Enable Brass Buckets");
		Options.enableBronze = configuration.getBoolean("EnableBronze", MATERIALS_CAT, true, "Enable Bronze Buckets");
        Options.enableCadmium = configuration.getBoolean("EnableCadmium", MATERIALS_CAT, true, "Enable Cadmium Buckets");
        Options.enableChromium = configuration.getBoolean("EnableChromium", MATERIALS_CAT, true, "Enable Chromium Buckets");
		Options.enableColdIron = configuration.getBoolean("EnableColdIron", MATERIALS_CAT, true,"Enable ColdIron Buckets");
		Options.enableCopper = configuration.getBoolean("EnableCopper", MATERIALS_CAT, true, "Enable Copper Buckets");
		Options.enableCupronickel = configuration.getBoolean("EnableCupronickel", MATERIALS_CAT, true, "Enable Cupronickel Buckets");
		Options.enableElectrum = configuration.getBoolean("EnableElectrum", MATERIALS_CAT, true, "Enable Electrum Buckets");
        Options.enableGalvanizedSteel = configuration.getBoolean("EnableGalvanizedSteel", MATERIALS_CAT, true, "Enable Galvanized Steel Buckets");
		Options.enableInvar = configuration.getBoolean("EnableInvar", MATERIALS_CAT, true, "Enable Invar Buckets");
        Options.enableIridium = configuration.getBoolean("EnableIridium", MATERIALS_CAT, true, "Enable Iridium Buckets");
		Options.enableLead = configuration.getBoolean("EnableLead", MATERIALS_CAT, true, "Enable Lead Buckets");
        Options.enableMagnesium = configuration.getBoolean("EnableMagnesium", MATERIALS_CAT, true, "Enable Magnesium Buckets");
        Options.enableManganese = configuration.getBoolean("EnableManganese", MATERIALS_CAT, true, "Enable Manganese Buckets");
		Options.enableMithril = configuration.getBoolean("EnableMitheril", MATERIALS_CAT, true, "Enable Mithril Buckets");
		Options.enableNickel = configuration.getBoolean("EnableNickel", MATERIALS_CAT, true, "Enable Nickel Buckets");
        Options.enableNichrome = configuration.getBoolean("EnableNichrome", MATERIALS_CAT, true, "Enable Nichrome Buckets");
        Options.enableOsmium = configuration.getBoolean("EnableOsmium", MATERIALS_CAT, true, "Enable Osmium Buckets");
		Options.enablePewter = configuration.getBoolean("EnablePewter", MATERIALS_CAT, true, "Enable Pewter Buckets");
		Options.enablePlatinum = configuration.getBoolean("EnablePlatinum", MATERIALS_CAT, true, "Enable Platinum Buckets");
        Options.enablePlutonium = configuration.getBoolean("EnablePlutonium", MATERIALS_CAT, true, "Enable Plutonium Buckets");
        Options.enableRutile = configuration.getBoolean("EnableRutile", MATERIALS_CAT, true, "Enable Rutile Buckets");
		Options.enableSilver = configuration.getBoolean("EnableSilver", MATERIALS_CAT, true, "Enable Silver Buckets");
        Options.enableStainlessSteel = configuration.getBoolean("EnableStainlessSteel", MATERIALS_CAT, true, "Enable Stainless Steel Buckets");
		Options.enableStarSteel = configuration.getBoolean("EnableStarSteel", MATERIALS_CAT, true, "Enable StarSteel Buckets");
		Options.enableSteel = configuration.getBoolean("EnableSteel", MATERIALS_CAT, true, "Enable Steel Buckets");
        Options.enableTantalum = configuration.getBoolean("EnableTantalum", MATERIALS_CAT, true, "Enable Tantalum Buckets");
		Options.enableTin = configuration.getBoolean("EnableTin", MATERIALS_CAT, true, "Enable Tin Buckets");
        Options.enableTitanium = configuration.getBoolean("EnableTitanium", MATERIALS_CAT, true, "Enable Titanium Buckets");
        Options.enableTungsten = configuration.getBoolean("EnableTungsten", MATERIALS_CAT, true, "Enable Tungsten Buckets");
        Options.enableUranium = configuration.getBoolean("EnableUranium", MATERIALS_CAT, true, "Enable Uranium Buckets");
		Options.enableZinc = configuration.getBoolean("EnableZinc", MATERIALS_CAT, true, "Enable Zinc Buckets");
        Options.enableZirconium = configuration.getBoolean("EnableZirconium", MATERIALS_CAT, true, "Enable Zirconium Buckets");
		
        if (configuration.hasChanged()) {
        	configuration.save();
        }
	}
	
	public static class Options {
		public static boolean enableAdamantine = true;
		public static boolean enableAluminum = true;
		public static boolean enableAluminumBrass = true;
		public static boolean enableAntimony = true;
		public static boolean enableAquarium = true;
		public static boolean enableBismuth = true;
		public static boolean enableBrass = true;
		public static boolean enableBronze = true;
		public static boolean enableCadmium = true;
		public static boolean enableChromium = true;
		public static boolean enableColdIron = true;
		public static boolean enableCopper = true;
		public static boolean enableCupronickel = true;
		public static boolean enableElectrum = true;
		public static boolean enableGalvanizedSteel = true;
		public static boolean enableInvar = true;
		public static boolean enableIridium = true;
		public static boolean enableLead = true;
		public static boolean enableMagnesium = true;
		public static boolean enableManganese = true;
		public static boolean enableMithril = true;
		public static boolean enableNickel = true;
		public static boolean enableNichrome = true;
		public static boolean enableOsmium = true;
		public static boolean enablePewter = true;
		public static boolean enablePlatinum = true;
		public static boolean enablePlutonium = true;
		public static boolean enableRutile = true;
		public static boolean enableSilver = true;
		public static boolean enableStainlessSteel = true;
		public static boolean enableStarSteel = true;
		public static boolean enableSteel = true;
		public static boolean enableTantalum = true;
		public static boolean enableTin = true;
		public static boolean enableTitanium = true;
		public static boolean enableTungsten = true;
		public static boolean enableUranium = true;
		public static boolean enableZinc = true;
		public static boolean enableZirconium = true;

		private Options() {
			throw new IllegalAccessError("Not an instantiable class");
		}
	}
	
	public static void postInit() {
		// do nothing - we have nothing to do
	}
}
