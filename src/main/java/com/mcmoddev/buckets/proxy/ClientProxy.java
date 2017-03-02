package com.mcmoddev.buckets.proxy;

import com.mcmoddev.buckets.init.Items;
import com.mcmoddev.buckets.items.ItemMMDBucket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		ModelLoader.setCustomMeshDefinition(Items.MetalBucket, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation(stack.getItem().getRegistryName(), "inventory");
			}
		});
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				ItemMMDBucket thisBucket = Items.getBucketByMeta(stack.getMetadata());
				if( thisBucket == null )
					return 0;
				return thisBucket.getMetalMaterial().tintColor;
			}
		}, Items.MetalBucket );
		// do shit here
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

}
