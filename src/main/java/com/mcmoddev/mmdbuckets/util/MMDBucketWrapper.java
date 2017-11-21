package com.mcmoddev.mmdbuckets.util;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
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
            if (fluid != null) {
                return fluid;
            }
        }
        return super.getFluid();
    }

    @Override
    protected void setFluid(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            this.container = new ItemStack(this.original);
        } else {
            this.container = new ItemStack(this.original);
            this.container.setTagCompound(fluidStack.writeToNBT(new NBTTagCompound()));
        }
    }
}
