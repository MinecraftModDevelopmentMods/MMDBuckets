package com.mcmoddev.mmdbuckets.util;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.DispenseFluidContainer;

public class DispenseMMDBucket extends BehaviorDefaultDispenseItem {

    private static final DispenseMMDBucket INSTANCE = new DispenseMMDBucket();

    private DispenseMMDBucket() {
    }

    public static DispenseMMDBucket getInstance() {
        return INSTANCE;
    }

    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        return DispenseFluidContainer.getInstance().dispenseStack(source, stack);
    }
}
