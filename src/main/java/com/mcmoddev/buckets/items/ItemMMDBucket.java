package com.mcmoddev.buckets.items;

import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;

public class ItemMMDBucket extends ItemBucket implements IOreDictionaryEntry, IMetalObject {

	public ItemMMDBucket(Block containedBlockIn) {
		super(containedBlockIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public MetalMaterial getMaterial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetalMaterial getMetalMaterial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOreDictionaryName() {
		// TODO Auto-generated method stub
		return null;
	}

}
