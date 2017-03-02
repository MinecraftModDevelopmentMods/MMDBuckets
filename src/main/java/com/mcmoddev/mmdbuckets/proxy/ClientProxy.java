package com.mcmoddev.mmdbuckets.proxy;

import com.mcmoddev.mmdbuckets.init.Items;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;

import net.minecraft.client.Minecraft;
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
		ModelLoader.setCustomModelResourceLocation(Items.MetalBucket, 0, new ModelResourceLocation(Items.MetalBucket.getRegistryName(), "inventory"));	}

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
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

}
