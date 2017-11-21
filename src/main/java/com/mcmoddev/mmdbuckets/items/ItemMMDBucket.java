package com.mcmoddev.mmdbuckets.items;


import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.mmdbuckets.init.Materials;
import com.mcmoddev.mmdbuckets.util.DispenseMMDBucket;
import com.mcmoddev.mmdbuckets.util.MMDBucketWrapper;
import com.mcmoddev.mmdbuckets.util.Utils;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.lib.material.IMMDObject;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;
import com.mcmoddev.mmdbuckets.MMDBuckets;

@Mod.EventBusSubscriber
public class ItemMMDBucket extends UniversalBucket implements IOreDictionaryEntry, IMMDObject {

//	private static final String FLUID_TAG = "mmdbucketfluids";

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
        if (this.isInCreativeTab(tab))
        {
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
        if (fluidStack == null)
        {
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
            ((ItemMMDBucket)emptyBucket.getItem()).onFillBucket(event);
            ItemStack filled = event.getFilledBucket();

            //noinspection ConstantConditions
            if (filled != null) {
                ItemStack result = ((ItemMMDBucket)emptyBucket.getItem()).getEmpty().copy();
                if (filled.hasTagCompound()) {
                    result.setTagCompound(filled.getTagCompound());
                    event.setFilledBucket(result);
                }
                else if (filled.getItem() == Items.WATER_BUCKET) {
                    result.setTagCompound(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME).writeToNBT(new NBTTagCompound()));
                    event.setFilledBucket(result);
                }
                else if (filled.getItem() == Items.LAVA_BUCKET) {
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
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
        FluidStack fluidStack = getFluid(stack);
        if (fluidStack == null)
        {
            // special handling of empty stack
            return I18n.translateToLocalFormatted("bucket.name");
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, NBTTagCompound nbt) {
        return new MMDBucketWrapper(stack);
    }

    //	/*
//	 * Now for the fun stuff
//	 */
//	@Override
//	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
//		MMDBuckets.logger.fatal("onItemRightClick(%s, %s, %s)", world, player, hand);
//		ItemStack itemIn = player.getHeldItem(hand);
//		FluidStack fluidStack = getFluid(itemIn);
//
//		MMDBuckets.logger.fatal("itemIn: %s -- fluidStack: %s", itemIn, fluidStack);
//
//		RayTraceResult trace = Utils.getNearestBlockWithDefaultReachDistance(world,player);
//		ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, itemIn, trace);
//		if( ret != null ) {
//			MMDBuckets.logger.fatal("ret == %s", ret);
//			return ret;
//		}
//
//
//		if( trace == null ) {
//			MMDBuckets.logger.fatal("Trace is Null");
//			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemIn);
//		} else if( trace.typeOfHit != RayTraceResult.Type.BLOCK ) {
//			MMDBuckets.logger.fatal("Trace result is not BLOCK");
//			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemIn);
//		} else {
//			BlockPos pos = trace.getBlockPos();
//			MMDBuckets.logger.fatal("Clicked at %s", pos);
//
//			// can we place liquid there?
//			if (!world.isBlockModifiable(player, pos)) {
//				MMDBuckets.logger.fatal("Not Modifiable!");
//				return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemIn);
//			} else {
//				BlockPos nPos = world.getBlockState(pos).getBlock().isReplaceable(world, pos) &&
//						trace.sideHit == EnumFacing.UP ? pos : pos.offset(trace.sideHit);
//				MMDBuckets.logger.fatal("nPos = %s", nPos);
//				if(!player.canPlayerEdit(nPos, trace.sideHit, itemIn)) {
//					MMDBuckets.logger.fatal("Player Cannot Edit");
//					return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemIn);
//				} else {
//					FluidActionResult result = FluidUtil.tryPlaceFluid(player, world, nPos, itemIn, fluidStack);
//					if( result.isSuccess() && !player.capabilities.isCreativeMode ) {
//						itemIn.shrink(1);
//						ItemStack drained = result.getResult();
//						ItemStack emptyStack = !drained.isEmpty()?drained.copy():new ItemStack(this);
//
//						if( itemIn.isEmpty() ) {
//							MMDBuckets.logger.fatal("itemIn.isEmpty() == true");
//							return ActionResult.newResult(EnumActionResult.SUCCESS, emptyStack);
//						} else {
//							MMDBuckets.logger.fatal("itemIn.isEmpty() != true");
//							ItemHandlerHelper.giveItemToPlayer(player, emptyStack);
//							return ActionResult.newResult(EnumActionResult.SUCCESS, itemIn);
//						}
//					}
//				}
//			}
//		}
//		// couldn't place liquid there
//		MMDBuckets.logger.fatal("Bad Click or Creative Mode");
//		return ActionResult.newResult(EnumActionResult.FAIL, itemIn);
//	}
	
//	public FluidStack getFluid(ItemStack itemIn) {
//		NBTTagCompound tags = itemIn.getTagCompound();
//		if( tags != null ) {
//			return FluidStack.loadFluidStackFromNBT(tags.getCompoundTag(FLUID_TAG));
//		}
//		return null;
//	}

//	@SubscribeEvent
//	public static void onFillBucket(FillBucketEvent ev) {
//		if (ev.getResult() != Event.Result.DEFAULT) {
//			return;
//		}
//
//		ItemStack empty = ev.getEmptyBucket();
//		if (empty.isEmpty()) { // || !empty.getItem().equals(this) ) {
//			return;
//		}
//
//		ItemStack bucket = empty.copy();
//		bucket.setCount(1);
//
//		MMDBuckets.logger.fatal("onFillBucket(%s)", ev);
//		RayTraceResult target = ev.getTarget();
//		if ((target == null) || (target.typeOfHit != RayTraceResult.Type.BLOCK)) {
//			MMDBuckets.logger.fatal("Bad Click ?");
//			return;
//		}
//
//		World world = ev.getWorld();
//		BlockPos targetPos = target.getBlockPos();
//
//		FluidActionResult res = FluidUtil.tryPickUpFluid(bucket, ev.getEntityPlayer(), world, targetPos, target.sideHit);
//		MMDBuckets.logger.fatal("FluidUtil.tryPickupFluid res: %s", res);
//		if (res.isSuccess()) {
//			MMDBuckets.logger.fatal("Result Success - %s", res.getResult());
//			ev.setResult(Event.Result.ALLOW);
//			ev.setFilledBucket(res.getResult());
//		} else {
//			MMDBuckets.logger.fatal("Result Failure!");
//			ev.setCanceled(true);
//		}
//	}

//	public int getCapacity() {
//		return Fluid.BUCKET_VOLUME;
//	}
		
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
//    {
//        return new MMDBucketWrapper(stack, this);
//    }
}
