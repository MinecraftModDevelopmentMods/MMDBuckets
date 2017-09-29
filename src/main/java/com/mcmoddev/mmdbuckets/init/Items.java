package com.mcmoddev.mmdbuckets.init;

import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.lib.material.MMDMaterial.MaterialType;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Items extends com.mcmoddev.lib.init.Items {
	
	public static final Fluid[] FLUIDS = {FluidRegistry.WATER, FluidRegistry.LAVA};
	
	public static void init() {
		for( MMDMaterial mat : Materials.getAllMaterials() ) {
			if( mat.getType() == MaterialType.METAL ) {
				if( !mat.getName().equals("iron") && !mat.hasItem("bucket") ) {
					ItemMMDBucket nb = new ItemMMDBucket(mat);
					mat.addNewItem("bucket", addItem(nb, "bucket", mat, com.mcmoddev.basemetals.init.ItemGroups.myTabs.itemsTab));
				}
			}
		}		
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> ev) {
		for( MMDMaterial mat : Materials.getAllMaterials() ) {
			if( mat.hasItem("bucket") && mat.getItem("bucket") instanceof ItemMMDBucket ) {
				ev.getRegistry().register(mat.getItem("bucket"));
			}
		}
	}
}
