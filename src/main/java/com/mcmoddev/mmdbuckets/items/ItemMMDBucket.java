package com.mcmoddev.mmdbuckets.items;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.mcmoddev.lib.material.IMMDObject;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;
import com.mcmoddev.mmdbuckets.init.Materials;
import com.mcmoddev.mmdbuckets.util.DispenseMMDBucket;
import com.mcmoddev.mmdbuckets.util.MMDBucketWrapper;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ItemMMDBucket extends UniversalBucket implements IOreDictionaryEntry, IMMDObject {
    private final MMDMaterial base;
    private final ItemStack empty = new ItemStack(this);

    public ItemMMDBucket() {
        this(Materials.getMaterialByName("iron"));
    }

    public ItemMMDBucket(MMDMaterial mat) {
        this.base = mat;
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseMMDBucket.getInstance());
    }

    @Override
    public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            subItems.add(new ItemStack(this));
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (getFluid(stack) == null) {
            return 16;
        }

        return 1;
    }

    @Override
    public MMDMaterial getMMDMaterial() {
        return this.base;
    }

    @Override
    public String getOreDictionaryName() {
        return "bucket" + this.base.getCapitalizedName();
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        FluidStack fluidStack = getFluid(itemstack);
        if (fluidStack == null) {
            // empty bucket
            RayTraceResult trace = this.rayTrace(world, player, true);
            ActionResult<ItemStack> result = ForgeEventFactory.onBucketUse(player, world, itemstack, trace);
            if (result != null) {
                return result;
            }
        }

        return super.onItemRightClick(world, player, hand);
    }

    @SubscribeEvent // (priority = EventPriority.LOW) // low priority so other mods can handle their stuff first
    public static void onFillBucketStatic(FillBucketEvent event) {
        if (event.getResult() != Event.Result.DEFAULT) {
            // event was already handled
            return;
        }

        // not for us to handle
        ItemStack emptyBucket = event.getEmptyBucket();
        if (emptyBucket.getItem() instanceof ItemMMDBucket) {
            ((ItemMMDBucket) emptyBucket.getItem()).onFillBucket(event);
            ItemStack filled = event.getFilledBucket();

            //noinspection ConstantConditions
            if (filled != null) {
                ItemStack result = ((ItemMMDBucket) emptyBucket.getItem()).getEmpty().copy();
                if (filled.hasTagCompound()) {
                    result.setTagCompound(filled.getTagCompound());
                    event.setFilledBucket(result);
                } else if (filled.getItem() == Items.WATER_BUCKET) {
                    result.setTagCompound(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME).writeToNBT(new NBTTagCompound()));
                    event.setFilledBucket(result);
                } else if (filled.getItem() == Items.LAVA_BUCKET) {
                    result.setTagCompound(new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME).writeToNBT(new NBTTagCompound()));
                    event.setFilledBucket(result);
                }
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack getEmpty() {
        return this.empty;
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        FluidStack fluidStack = getFluid(stack);
        if (fluidStack == null) {
            // special handling of empty stack
            return I18n.translateToLocalFormatted("bucket.name");
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
        return new MMDBucketWrapper(stack);
    }
}
