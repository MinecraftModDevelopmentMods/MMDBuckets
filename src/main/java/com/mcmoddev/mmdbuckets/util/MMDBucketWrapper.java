package com.mcmoddev.mmdbuckets.util;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class MMDBucketWrapper extends FluidBucketWrapper {  // implements IFluidHandlerItem, ICapabilityProvider {
	private final Item original;

	public MMDBucketWrapper(@Nonnull ItemStack container) {
		super(container);
		this.original = container.getItem();
	}

	@Nullable
	@Override
	public FluidStack getFluid() {
		if (this.container.hasTagCompound()) {
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(this.container.getTagCompound());
			if (fluid != null) { // && ((fluid.getFluid() == FluidRegistry.WATER) || (fluid.getFluid() == FluidRegistry.LAVA))) {
				return fluid;
			}
		}
		return super.getFluid();
	}

	@Override
	protected void setFluid(@Nullable FluidStack fluidStack) {
		if (fluidStack == null) {
			this.container = new ItemStack(this.original);
		}
		else {
			this.container = new ItemStack(this.original);
			this.container.setTagCompound(fluidStack.writeToNBT(new NBTTagCompound()));
		}
	}

	//	@Nonnull
//	protected ItemStack container;
//	private ItemMMDBucket bucket;
//
//	public MMDBucketWrapper( ItemStack container, ItemMMDBucket baseBucket ) {
//		this.container = container;
//		this.bucket = baseBucket;
//	}
//
//	@Nonnull
//	@Override
//	public ItemStack getContainer() {
//		return container;
//	}
//
//	public boolean canFillFluidType(FluidStack fluid) {
//		if( fluid.getFluid() == FluidRegistry.WATER || fluid.getFluid() == FluidRegistry.LAVA) {
//			return true;
//		}
//		return FluidRegistry.getBucketFluids().contains(fluid.getFluid());
//	}
//
//	protected void setFluid(@Nullable FluidStack fluidStack) {
//		if(fluidStack == null) {
//			container = new ItemStack(this.bucket);
//		} else {
//			ItemStack newContainer = new ItemStack(this.bucket);
//			NBTTagCompound tag = new NBTTagCompound();
//			fluidStack.writeToNBT(tag);
//			newContainer.setTagCompound(tag);
//			container = newContainer;
//		}
//	}
//
//	@Nullable
//	public FluidStack getFluid() {
//		return this.bucket.getFluid(container);
//	}
//
//	@Override
//	public int fill(FluidStack resource, boolean doFill) {
//		if( container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME ||
//				getFluid() != null || !canFillFluidType(resource) ) {
//			return 0;
//		}
//
//		if( doFill) {
//			setFluid(resource);
//		}
//
//		return Fluid.BUCKET_VOLUME;
//	}
//
//	@Override
//	public IFluidTankProperties[] getTankProperties() {
//		return new FluidTankProperties[] { new FluidTankProperties( getFluid(), Fluid.BUCKET_VOLUME ) };
//	}
//
//	@Override
//	public FluidStack drain(FluidStack resource, boolean doDrain) {
//		if( container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME)
//			return null;
//
//		FluidStack fluidStack = getFluid();
//		if( fluidStack != null && fluidStack.isFluidEqual(resource) ) {
//			if( doDrain )
//				setFluid((FluidStack)null);
//
//			return fluidStack;
//		}
//		return null;
//	}
//
//	@Override
//	public FluidStack drain(int maxDrain, boolean doDrain) {
//		if( container.getCount() != 1 || maxDrain < Fluid.BUCKET_VOLUME)
//			return null;
//
//		FluidStack fluidStack = getFluid();
//
//		if( fluidStack != null) {
//			if( doDrain )
//				setFluid((FluidStack)null);
//
//			return fluidStack;
//		}
//
//		return null;
//	}
//
//	@Override
//	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
//		return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
//	}
//
//	@Override
//	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
//		if( capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ) {
//			return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
//		}
//		return null;
//	}
}
