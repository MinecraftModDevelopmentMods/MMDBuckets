package com.mcmoddev.mmdbuckets.init;

import com.mcmoddev.lib.data.SharedStrings;
import com.mcmoddev.lib.events.MMDLibRegisterItems;
import com.mcmoddev.lib.material.MMDMaterialType;
import com.mcmoddev.mmdbuckets.MMDBuckets;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid=MMDBuckets.MODID)
public class Items extends com.mcmoddev.lib.init.Items {

    public static final Fluid[] FLUIDS = {FluidRegistry.WATER, FluidRegistry.LAVA};

	@SubscribeEvent
	public static void registerItems(MMDLibRegisterItems ev) {
		Materials.getAllMaterials().stream()
		.filter(m -> !"default".equalsIgnoreCase(m.getName()))
		.filter(m -> !"iron".equalsIgnoreCase(m.getName()))
		.filter(m -> !m.hasItem("bucket"))
		.filter(m -> m.getType() == MMDMaterialType.MaterialType.METAL)
		.forEach(m -> {
                    ItemMMDBucket nb = new ItemMMDBucket(m);
                    m.addNewItem("bucket", addItem(nb, "bucket", m, com.mcmoddev.lib.init.ItemGroups.getTab(SharedStrings.TAB_ITEMS)));
                });
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> ev) {
		Materials.getAllMaterials().stream()
		.filter(m -> !"default".equalsIgnoreCase(m.getName()))
		.filter(m -> !"iron".equalsIgnoreCase(m.getName()))
		.filter(m -> m.hasItem("bucket"))
		.filter(m -> m.getItem("bucket") instanceof ItemMMDBucket)
		.forEach(mat -> ev.getRegistry().register(mat.getItem("bucket")));
    }
}