package com.mcmoddev.mmdbuckets.init;

import javax.annotation.Nonnull;

import com.mcmoddev.lib.data.SharedStrings;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.lib.material.MMDMaterial.MaterialType;
import com.mcmoddev.mmdbuckets.MMDBuckets;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Items extends com.mcmoddev.lib.init.Items {

    public static final Fluid[] FLUIDS = {FluidRegistry.WATER, FluidRegistry.LAVA};

    public static void init() {
    	Materials.getAllMaterials().stream()
    	.filter(Items::canHaveBucket)
    	.forEach(Items::addBucket);
    }

    private static boolean canHaveBucket(@Nonnull final MMDMaterial mat) {
    	return mat.getType().equals(MaterialType.METAL) && 
    			!mat.hasItem("bucket") && 
    			!mat.getName().equalsIgnoreCase("iron") &&
    			!mat.isEmpty() &&
    			!mat.isDefault();
    }
    
    private static void addBucket(@Nonnull final MMDMaterial mat) {
    	ItemMMDBucket nb = (ItemMMDBucket) addItem((Item)new ItemMMDBucket(mat), "bucket", mat, 
    			com.mcmoddev.basemetals.init.ItemGroups.getTab(SharedStrings.TAB_ITEMS));
    	mat.addNewItem("bucket", nb);
    }

    private static boolean hasBucket(@Nonnull final MMDMaterial mat) {
    	return mat.hasItem("bucket");
    }
    
    private static boolean mineToRegister(@Nonnull final Item it) {
    	return (it instanceof ItemMMDBucket) &&
    			(it.getRegistryName().getResourceDomain().equalsIgnoreCase(MMDBuckets.MODID));
    }
    
    private static Item mapper(@Nonnull final MMDMaterial mat) {
    	return mat.getItem("bucket");
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> ev) {
    	Materials.getAllMaterials().stream()
    	.filter(Items::hasBucket)
    	.map(Items::mapper)
    	.filter(Items::mineToRegister)
    	.forEach(ev.getRegistry()::register);
    }
}
