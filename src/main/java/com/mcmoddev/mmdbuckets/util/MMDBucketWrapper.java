package com.mcmoddev.mmdbuckets.util;


import com.mcmoddev.mmdbuckets.init.Items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class MMDBucketWrapper extends FluidBucketWrapper {

	public MMDBucketWrapper( ItemStack container) {
		super(container);
	}

	public boolean canFillFluidType(FluidStack fluid) {
		if( fluid.getFluid() == FluidRegistry.WATER || fluid.getFluid() == FluidRegistry.LAVA) {
			return true;
		}
		return FluidRegistry.getBucketFluids().contains(fluid.getFluid());
	}
	
	protected void setFluid(Fluid fluid) {
		if( fluid == null ) {
			Items.MetalBucket.drain(container, 1000, true);
		} else if(FluidRegistry.getBucketFluids().contains(fluid) || fluid == FluidRegistry.LAVA
				|| fluid == FluidRegistry.WATER || fluid.getName().equals("milk") ) {
			Items.MetalBucket.fill(container, new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);
		}
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if( container.stackSize != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME || getFluid() != null || !canFillFluidType(resource) ) {
			return 0;
		}
		
		if( doFill) {
			setFluid(resource.getFluid());
		}
		
		return Fluid.BUCKET_VOLUME;
	}
}
