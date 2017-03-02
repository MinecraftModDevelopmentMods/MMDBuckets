package com.mcmoddev.buckets.items;

import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;

import java.util.List;

import com.mcmoddev.buckets.init.Items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMMDBucket extends ItemBucket implements IOreDictionaryEntry, IMetalObject {

	private final MetalMaterial baseMaterial;
	private static final int numBuckets = Items.getCount();
	
	public ItemMMDBucket(Block containedBlockIn, MetalMaterial base) {
		super(containedBlockIn);
		this.baseMaterial = base;
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.MISC);
		this.setMaxDamage(0);
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

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.bucket." + Items.getNameFromMeta(stack.getMetadata()) + ".name";
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn));
		for( int i = 0; i < numBuckets; i++ ) {
			subItems.add( new ItemStack(itemIn, 1, i));
		}
	}
}
