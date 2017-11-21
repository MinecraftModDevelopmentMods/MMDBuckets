package com.mcmoddev.mmdbuckets.proxy;

import com.mcmoddev.basemetals.init.Materials;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.mmdbuckets.items.ItemMMDBucket;
import com.mcmoddev.mmdbuckets.util.ModelMMDBucket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);

        ModelLoaderRegistry.registerLoader(ModelMMDBucket.LoaderMMDBucket.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        for (MMDMaterial mat : Materials.getAllMaterials()) {
            if (mat.hasItem("bucket") && mat.getItem("bucket") instanceof ItemMMDBucket) {
                Item bucket = mat.getItem("bucket");
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
                    @Override
                    public int colorMultiplier(ItemStack stack, int tintIndex) {
                        ItemMMDBucket thisBucket = (ItemMMDBucket) stack.getItem();
                        if ((thisBucket == null) || (tintIndex > 0))
                            return -1;
                        return thisBucket.getMMDMaterial().getTintColor();
                    }

                }, bucket);
            }
        }
    }

    @SubscribeEvent
    public void modelRegistryBits(ModelRegistryEvent ev) {
        for (MMDMaterial mat : Materials.getAllMaterials()) {
            if (mat.hasItem("bucket") && mat.getItem("bucket") instanceof ItemMMDBucket) {
                Item item = mat.getItem("bucket");
                ModelLoader.setCustomMeshDefinition(item, stack ->
                        (FluidUtil.getFluidContained(stack) == null) ? ModelMMDBucket.EMPTY_LOCATION : ModelMMDBucket.LOCATION
                );
                ModelBakery.registerItemVariants(item, ModelMMDBucket.EMPTY_LOCATION, ModelMMDBucket.LOCATION);
            }
        }
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent ev) {
        ModelMMDBucket.LoaderMMDBucket.INSTANCE.register(ev.getMap());
    }
}
