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
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

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
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        // clicked on a block?
        RayTraceResult mop = this.rayTrace(world, player, false);

        if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        BlockPos clickPos = mop.getBlockPos();
        // can we place liquid there?
        if (world.isBlockModifiable(player, clickPos))
        {
            // the block adjacent to the side we clicked on
            BlockPos targetPos = clickPos.offset(mop.sideHit);

            // can the player place there?
            if (player.canPlayerEdit(targetPos, mop.sideHit, itemstack))
            {
                // try placing liquid
                FluidActionResult result = FluidUtil.tryPlaceFluid(player, world, targetPos, itemstack, fluidStack);
                if (result.isSuccess() && !player.capabilities.isCreativeMode)
                {
                    // success!
                    player.addStat(StatList.getObjectUseStats(this));

                    itemstack.shrink(1);
                    ItemStack drained = result.getResult();
                    ItemStack emptyStack = !drained.isEmpty() ? drained.copy() : new ItemStack(this);

                    // check whether we replace the item or add the empty one to the inventory
                    if (itemstack.isEmpty())
                    {
                        return ActionResult.newResult(EnumActionResult.SUCCESS, emptyStack);
                    }
                    else
                    {
                        // add empty bucket to player inventory
                        ItemHandlerHelper.giveItemToPlayer(player, emptyStack);
                        return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
                    }
                }
            }
        }

        // couldn't place liquid there2
        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
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
        FluidStack fluidStack = this.getFluid(stack);
        if (fluidStack == null) {
            // special handling of empty stack
            return new TextComponentTranslation("item.mmdbuckets.bucket_empty.name",
                    this.getMMDMaterial().getCapitalizedName()
            ).getFormattedText();
        }
        else {
            return new TextComponentTranslation("item.mmdbuckets.bucket.name",
                    this.getMMDMaterial().getCapitalizedName(),
                    fluidStack.getLocalizedName()
            ).getFormattedText();
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
        return new MMDBucketWrapper(stack);
    }
}
