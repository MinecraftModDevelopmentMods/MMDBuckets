package com.mcmoddev.buckets.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mcmoddev.buckets.items.ItemMMDBucket;
import com.mcmoddev.buckets.util.Config;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.material.MetalMaterial.MaterialType;

import net.minecraft.init.Blocks;

public class Items extends com.mcmoddev.lib.init.Items {
	
	protected static List<ItemMMDBucket> buckets = new ArrayList<>();

	public static void init() {
		Collection<MetalMaterial> materials = Materials.getAllMaterials();
		
		for( MetalMaterial mat : materials ) {
			if( Config.get(mat.getName()) && mat.getType() == MaterialType.METAL ) {
				buckets.add(new ItemMMDBucket(Blocks.AIR, mat));
			}
		}
	}
	
	public static int getCount() {
		return buckets.size();
	}
	
	public static List<ItemMMDBucket> getBuckets() {
		return Collections.unmodifiableList(buckets);
	}
	
	public static String getNameFromMeta( int meta ) {
		return buckets.get(meta).getMaterial().getName();
	}
}
