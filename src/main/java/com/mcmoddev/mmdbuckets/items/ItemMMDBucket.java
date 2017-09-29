package com.mcmoddev.mmdbuckets.items;


import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.mmdbuckets.init.Materials;
import com.mcmoddev.mmdbuckets.util.DispenseMMDBucket;
import com.mcmoddev.mmdbuckets.util.MMDBucketWrapper;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

import com.mcmoddev.lib.material.IMMDObject;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;
import com.mcmoddev.mmdbuckets.MMDBuckets;

public class ItemMMDBucket extends Item implements IOreDictionaryEntry, IMMDObject  {

	private static final String FLUID_TAG = "mmdbucketfluids";
	
	private final MMDMaterial base;
	
	public ItemMMDBucket() {
		this(Materials.getMaterialByName("iron"));
	}

	public ItemMMDBucket(MMDMaterial mat) {
		this.base = mat;
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseMMDBucket.getInstance());
	}

	@Override
	public int getItemStackLimit( ItemStack stack ) {
		if( getFluid(stack) == null ) {
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
		return "bucket"+this.base.getCapitalizedName();
	}

	/*
	 * Now for the fun stuff
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack itemIn = player.getHeldItem(hand);
		FluidStack fluidStack = getFluid(itemIn);
		
		if( fluidStack == null) {
			// we fill the bucket instead of empty it
			if( getFluid(itemIn) == null ) {
				ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, itemIn, this.rayTrace(world, player, true));
				if( ret != null ) {
					return ret;
				}
			} else {
				MMDBuckets.logger.error("getFluid(stack) was not null, but getFluid(itemIn) was");
			}
			
			return ActionResult.newResult(EnumActionResult.PASS, itemIn);
		}
		
		
        // clicked on a block?
        RayTraceResult mop = this.rayTrace(world, player, false);

        if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK) {
            return ActionResult.newResult(EnumActionResult.PASS, itemIn);
        }

        BlockPos clickPos = mop.getBlockPos();
        // can we place liquid there?
        if (world.isBlockModifiable(player, clickPos)) {
            // the block adjacent to the side we clicked on
            BlockPos targetPos = clickPos.offset(mop.sideHit);

            // can the player place there?
            if (player.canPlayerEdit(targetPos, mop.sideHit, itemIn)) {
                // try placing liquid
            	FluidActionResult res = FluidUtil.tryPlaceFluid(player, world, targetPos, itemIn, fluidStack); 
                if( (res.isSuccess()) && (!player.capabilities.isCreativeMode) ) {
                    // success!
                    player.addStat(StatList.getObjectUseStats(this));

                    itemIn.shrink(1);
                    ItemStack emptyStack = new ItemStack(itemIn.getItem(), 1, itemIn.getMetadata());

                    // check whether we replace the item or add the empty one to the inventory
                    if (itemIn.isEmpty()) {
                        return ActionResult.newResult(EnumActionResult.SUCCESS, emptyStack);
                    } else {
                        // add empty bucket to player inventory
                        ItemHandlerHelper.giveItemToPlayer(player, emptyStack);
                        return ActionResult.newResult(EnumActionResult.SUCCESS, itemIn);
                    }
                }
            }
        }

        // couldn't place liquid there2
		return ActionResult.newResult(EnumActionResult.FAIL, itemIn);
	}
	
	
	public FluidStack getFluid(ItemStack itemIn) {
		NBTTagCompound tags = itemIn.getTagCompound();
		if( tags != null ) {
			return FluidStack.loadFluidStackFromNBT(tags.getCompoundTag(FLUID_TAG));
		}
		return null;
	}

	@SubscribeEvent
	public void onFillBucket(FillBucketEvent ev) {
		if( ev.getResult() != Event.Result.DEFAULT ) {
			return;
		}
		
		ItemStack empty = ev.getEmptyBucket();
		if( empty.isEmpty() || !empty.getItem().equals(this) ) {
			return;
		}
		
		ItemStack bucket = empty.copy();
		bucket.setCount(1);
		
		RayTraceResult target = ev.getTarget();		
		if( target == null || target.typeOfHit != RayTraceResult.Type.BLOCK ) {
			return;
		}
		
		World world = ev.getWorld();
		BlockPos targetPos = target.getBlockPos();

		FluidActionResult res = FluidUtil.tryPickUpFluid(bucket, ev.getEntityPlayer(), world, targetPos, target.sideHit); 
		
		if( res.isSuccess() ) {
			ev.setResult(Event.Result.ALLOW);
			ev.setFilledBucket(res.getResult());
		} else {
			ev.setCanceled(true);
		}
	}

	public int getCapacity() {
		return Fluid.BUCKET_VOLUME;
	}
		
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new MMDBucketWrapper(stack, this);
    }
}
