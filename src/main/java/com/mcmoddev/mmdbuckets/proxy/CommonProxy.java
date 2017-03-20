package com.mcmoddev.mmdbuckets.proxy;

import com.mcmoddev.mmdbuckets.init.*;
import com.mcmoddev.mmdbuckets.util.Config;

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
