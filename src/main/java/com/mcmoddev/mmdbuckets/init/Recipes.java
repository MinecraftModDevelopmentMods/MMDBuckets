package com.mcmoddev.mmdbuckets.init;

import com.mcmoddev.lib.util.Oredicts;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class Recipes extends com.mcmoddev.lib.init.Recipes {
	public static void init() {
		for( ItemMMDBucket bucket : Items.getBuckets() ) {
			String name = bucket.getMetalMaterial().getName();
			String oreDictName = name.substring(0, 1).toUpperCase()+name.substring(1);
			
			GameRegistry.addRecipe( new ShapedOreRecipe( new ItemStack(Items.MetalBucket, 1, Items.nameMap.indexOf(name)), "x x", " x ", 'x', Oredicts.INGOT+oreDictName) );
		}
	}
}
