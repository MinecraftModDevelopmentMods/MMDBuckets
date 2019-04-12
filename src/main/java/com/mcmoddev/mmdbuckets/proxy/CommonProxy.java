package com.mcmoddev.mmdbuckets.proxy;

import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.mmdbuckets.init.Materials;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        for (MMDMaterial mat : Materials.getAllMaterials()) {
            Item bucket = mat.getItem("bucket");
            if (bucket != null && bucket instanceof ItemMMDBucket) {
                MinecraftForge.EVENT_BUS.register(bucket);
            }
        }
    }
}
