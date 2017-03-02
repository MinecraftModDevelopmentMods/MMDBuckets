package com.mcmoddev.mmdbuckets.items;


import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.mmdbuckets.init.Materials;

import java.util.List;

import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;
import com.mcmoddev.mmdbuckets.init.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMMDBucket extends Item implements IFluidContainerItem, IOreDictionaryEntry, IMetalObject  {

	private static int numBuckets = Items.getCount();
	
	private final MetalMaterial base;
	
	public ItemMMDBucket() {
		this(Materials.getMaterialByName("iron"));
	}

	public ItemMMDBucket(MetalMaterial mat) {
		base = mat;
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.MISC);
		setMaxDamage(0);
		setRegistryName("metalbucket."+mat.getName());		
	}
	
	@Override
	public FluidStack getFluid(ItemStack container) {
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
	}

	@Override
	public int getCapacity(ItemStack container) {
		return 1000;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if( container.stackSize != 1 || resource == null || resource.amount < 1000 || getFluid(container) != null) {
			return 0;
		}

		if( FluidRegistry.getBucketFluids().contains(resource.getFluid()) ) {
			if( doFill ) {
				NBTTagCompound tag = container.getTagCompound();
				if( tag == null ) {
					tag = new NBTTagCompound();
				}
				
				resource.writeToNBT(tag);
				container.setTagCompound(tag);
			}
			return 1000;
		} else if( resource.getFluid() == FluidRegistry.WATER ) {
			if( doFill ) {
				container.deserializeNBT(new ItemStack(net.minecraft.init.Items.WATER_BUCKET).serializeNBT());
			}
			return 1000;
		} else if( resource.getFluid() == FluidRegistry.LAVA ) {
			if( doFill ) {
				container.deserializeNBT( new ItemStack(net.minecraft.init.Items.LAVA_BUCKET).serializeNBT());
			}
			return 1000;
		}
		
		return 0;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		if( container.stackSize != 1 || maxDrain < 1000 ) {
			return null;
		}
		
		FluidStack fluidStack = getFluid(container);
		if( doDrain && fluidStack != null ) {
			container.stackSize = 0;
		}
		return fluidStack;
	}

	@Override
	public MetalMaterial getMaterial() {
		return this.base;
	}

	@Override
	public MetalMaterial getMetalMaterial() {
		return this.getMaterial();
	}

	@Override
	public String getOreDictionaryName() {
		return "bucket"+this.base.getCapitalizedName();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.mmdbuckets."+ Items.getNameFromMeta(stack.getMetadata())+".bucket.name";
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		MetalMaterial mat = Items.getBucketByMeta(stack.getMetadata()).getMetalMaterial();
		if( mat != null ) {
			String unloc = "item.mmdbuckets.bucket.name";
			String form = getUnlocalizedName(stack);
			String name = mat.getCapitalizedName();
			
			if( I18n.canTranslate(unloc) )
				return I18n.translateToLocalFormatted(unloc, name);
			
			return form;
		}
		return super.getItemStackDisplayName(stack);
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		if( numBuckets == 0 ) {
			Items.init();
			numBuckets = Items.getCount();
		}
		
		subItems.add(new ItemStack(itemIn));
		for( int i = 0; i < numBuckets; i++ ) {
			ItemStack x = new ItemStack(itemIn, 1, i);
			if( !subItems.contains(x) ) {
				subItems.add( x );
			}
		}
	}
}
