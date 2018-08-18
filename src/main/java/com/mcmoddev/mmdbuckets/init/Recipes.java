package com.mcmoddev.mmdbuckets.init;

import com.mcmoddev.lib.data.Names;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.lib.util.Config.Options;
import com.mcmoddev.lib.util.Oredicts;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class Recipes extends com.mcmoddev.lib.init.Recipes {
    public static void init() {
        for (MMDMaterial mat : Materials.getAllMaterials()) {
            if (mat.hasItem("bucket") && mat.getItem("bucket") instanceof ItemMMDBucket) {
                ItemStack bucket = new ItemStack(mat.getItem("bucket"));
                if (Options.isThingEnabled("furnaceCheese")) {
                    GameRegistry.addSmelting(bucket, new ItemStack(mat.getItem(Names.INGOT), 3), 0);
                } else if (Options.isThingEnabled("funace1112")) {
                    GameRegistry.addSmelting(bucket, new ItemStack(mat.getItem(Names.NUGGET)), 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> ev) {
        for (MMDMaterial mat : Materials.getAllMaterials()) {
            String oreDictName = mat.getCapitalizedName();
            ItemStack bucket = new ItemStack(mat.getItem("bucket"));

            if (!bucket.isEmpty() && bucket.getItem() instanceof ItemMMDBucket) {
                ShapedOreRecipe thisRecipe = new ShapedOreRecipe(new ResourceLocation("buckets"), bucket, "x x", " x ", 'x', Oredicts.INGOT + oreDictName);
                thisRecipe.setRegistryName(oreDictName + "_bucket");
                ev.getRegistry().register(thisRecipe);
            }
        }
    }
}
