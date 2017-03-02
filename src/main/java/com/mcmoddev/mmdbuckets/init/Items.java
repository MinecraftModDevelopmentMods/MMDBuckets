package com.mcmoddev.mmdbuckets.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.material.MetalMaterial.MaterialType;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import com.mcmoddev.mmdbuckets.util.Config;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Items extends com.mcmoddev.lib.init.Items {
	
	public static Item MetalBucket = null;
	protected static List<ItemMMDBucket> buckets = new ArrayList<>();

	private static class BucketComparator implements Comparator<ItemMMDBucket> {
		public int compare(ItemMMDBucket left, ItemMMDBucket right) {
			String leftName = left.getMaterial().getName();
			String rightName = right.getMaterial().getName();
			return leftName.compareTo(rightName);			
		}
	}
	
	public static void init() {
		Collection<MetalMaterial> materials = Materials.getAllMaterials();
		MetalBucket = new ItemMMDBucket();
		GameRegistry.register(MetalBucket);
		
		for( MetalMaterial mat : materials ) {
			if( Config.get(mat.getName()) && mat.getType() == MaterialType.METAL ) {
				buckets.add(new ItemMMDBucket(Blocks.AIR, mat));
			}
		}
		
		Collections.sort(buckets, new BucketComparator());
	}
	
	public static int getCount() {
		return buckets.size();
	}
	
	public static List<ItemMMDBucket> getBuckets() {
		return Collections.unmodifiableList(buckets);
	}
	
	public static String getNameFromMeta( int meta ) {
		if( meta > buckets.size() ) {
			return buckets.get(0).getMaterial().getName();
		}
		return buckets.get(meta).getMaterial().getName();
	}

	public static ItemMMDBucket getBucketByMeta(int metadata) {
		if( metadata > buckets.size() ) {
			return buckets.get(0);
		}
		return buckets.get(metadata);
	}
}
