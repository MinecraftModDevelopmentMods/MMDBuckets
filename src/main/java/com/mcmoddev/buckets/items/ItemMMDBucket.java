package com.mcmoddev.buckets.items;

import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;

public class ItemMMDBucket extends ItemBucket implements IOreDictionaryEntry, IMetalObject {

	private final MetalMaterial baseMaterial;
	
	public ItemMMDBucket(Block containedBlockIn, MetalMaterial base) {
		super(containedBlockIn);
		this.baseMaterial = base;
		this.setMaxStackSize(1);
	}

	@Override
	public MetalMaterial getMaterial() {
		return this.baseMaterial;
	}

	@Override
	public MetalMaterial getMetalMaterial() {
		return this.getMaterial();
	}

	@Override
	public String getOreDictionaryName() {
		return "bucket"+this.baseMaterial.getCapitalizedName();
	}

}
