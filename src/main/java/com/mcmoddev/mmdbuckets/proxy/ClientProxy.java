package com.mcmoddev.mmdbuckets.proxy;

import com.mcmoddev.basemetals.init.Materials;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.mmdbuckets.MMDBuckets;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		for( MMDMaterial mat : Materials.getAllMaterials()) {
			if( mat.hasItem("bucket") && mat.getItem("bucket") instanceof ItemMMDBucket) {
				ModelLoader.setCustomModelResourceLocation(mat.getItem("bucket"), 0, new ModelResourceLocation(mat.getItem("bucket").getRegistryName(), "inventory"));
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		for( MMDMaterial mat : Materials.getAllMaterials() ) {
			if( mat.hasItem("bucket") && mat.getItem("bucket") instanceof ItemMMDBucket ) {
				ItemMMDBucket bucket = (ItemMMDBucket) mat.getItem("bucket");
				Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
					@Override
					public int colorMultiplier(ItemStack stack, int tintIndex) {
						ItemMMDBucket thisBucket = (ItemMMDBucket)stack.getItem();
						if( thisBucket == null )
							return -1;
						MMDBuckets.logger.fatal("Bucket "+thisBucket+" meta "+stack.getMetadata()+" tint: "+thisBucket.getMMDMaterial().getTintColor());
						return thisBucket.getMMDMaterial().getTintColor();
					}

				}, bucket );
			}
		}
	}	
}
