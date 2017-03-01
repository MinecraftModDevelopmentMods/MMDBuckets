package com.mcmoddev.buckets.proxy;

import com.mcmoddev.buckets.util.Config;
import com.mcmoddev.buckets.init.*;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		Config.init();
		Materials.init();
		Items.init();
	}

	public void init(FMLInitializationEvent event) {
		Recipes.init();		
	}

	public void postInit(FMLPostInitializationEvent event) {
		Config.postInit();		
	}
}
