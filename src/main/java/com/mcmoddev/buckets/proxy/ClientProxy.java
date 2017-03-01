package com.mcmoddev.buckets.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		// do shit here
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		// do shit here
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		// do even more shit here
	}

}
