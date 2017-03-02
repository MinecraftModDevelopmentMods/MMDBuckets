package com.mcmoddev.mmdbuckets.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.material.MetalMaterial.MaterialType;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import com.mcmoddev.mmdbuckets.util.Config;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Items extends com.mcmoddev.lib.init.Items {
	
	public static final Fluid[] FLUIDS = {FluidRegistry.WATER, FluidRegistry.LAVA};
	public static ItemMMDBucket MetalBucket = null;
	protected static List<ItemMMDBucket> buckets = new ArrayList<>();
	public static List<String> nameMap = new ArrayList<>();
	
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
				if( !nameMap.contains(mat.getName()) ) {
					nameMap.add(mat.getName());
					buckets.add(new ItemMMDBucket(mat));
				}
			}
		}
		
		buckets.sort( new BucketComparator() );
		
		nameMap.sort( new Comparator<String>() {
			@Override
			public int compare(String left, String right) {
				return left.compareTo(right);
			}
		});
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

	public static int getMetaFromMaterialName( String name ) {
		for( int i = 0; i < buckets.size(); i++ ) {
			if( buckets.get(i).getMetalMaterial().getName().equals(name) ) {
				return i;
			}
		}
		return -1;
	}
}
