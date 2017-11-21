package com.mcmoddev.mmdbuckets.proxy;

import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.mmdbuckets.init.Items;
import com.mcmoddev.mmdbuckets.init.Materials;
import com.mcmoddev.mmdbuckets.init.Recipes;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import com.mcmoddev.mmdbuckets.util.Config;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        Config.init();
        Materials.init();
        Items.init();
        Recipes.init();
        MinecraftForge.EVENT_BUS.register(com.mcmoddev.mmdbuckets.init.Items.class);
        MinecraftForge.EVENT_BUS.register(com.mcmoddev.mmdbuckets.init.Recipes.class);
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        Config.postInit();
        for (MMDMaterial mat : Materials.getAllMaterials()) {
            Item bucket = mat.getItem("bucket");
            if (bucket != null && bucket instanceof ItemMMDBucket) {
                MinecraftForge.EVENT_BUS.register(bucket);
            }
        }
    }
}
