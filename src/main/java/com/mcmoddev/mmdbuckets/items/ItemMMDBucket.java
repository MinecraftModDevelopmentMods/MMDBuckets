package com.mcmoddev.mmdbuckets.items;


import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.mmdbuckets.init.Materials;

import java.util.List;

import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.registry.IOreDictionaryEntry;
import com.mcmoddev.mmdbuckets.init.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class ItemMMDBucket extends Item implements IFluidContainerItem, IOreDictionaryEntry, IMetalObject  {

	private static int numBuckets = Items.getCount();
	
	private final MetalMaterial base;
	
	public ItemMMDBucket() {
		this(Materials.getMaterialByName("iron"));
	}

	public ItemMMDBucket(MetalMaterial mat) {
		base = mat;
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.MISC);
		setMaxDamage(0);
		setRegistryName("metalbucket."+mat.getName());		
	}
	
	@Override
	public int getCapacity(ItemStack container) {
		return 1000;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if( container.stackSize != 1 || resource == null || resource.amount < 1000 || getFluid(container) != null) {
			return 0;
		}

		if( FluidRegistry.getBucketFluids().contains(resource.getFluid()) ) {
			if( doFill ) {
				NBTTagCompound tag = container.getTagCompound();
				if( tag == null ) {
					tag = new NBTTagCompound();
				}
				
				resource.writeToNBT(tag);
				container.setTagCompound(tag);
			}
			return 1000;
		} else if( resource.getFluid() == FluidRegistry.WATER ) {
			if( doFill ) {
				container.deserializeNBT(new ItemStack(net.minecraft.init.Items.WATER_BUCKET).serializeNBT());
			}
			return 1000;
		} else if( resource.getFluid() == FluidRegistry.LAVA ) {
			if( doFill ) {
				container.deserializeNBT( new ItemStack(net.minecraft.init.Items.LAVA_BUCKET).serializeNBT());
			}
			return 1000;
		}
		
		return 0;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		if( container.stackSize != 1 || maxDrain < 1000 ) {
			return null;
		}
		
		FluidStack fluidStack = getFluid(container);
		if( doDrain && fluidStack != null ) {
			container.stackSize = 0;
		}
		return fluidStack;
	}

	@Override
	public MetalMaterial getMaterial() {
		return this.base;
	}

	@Override
	public MetalMaterial getMetalMaterial() {
		return this.getMaterial();
	}

	@Override
	public String getOreDictionaryName() {
		return "bucket"+this.base.getCapitalizedName();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.mmdbuckets."+ Items.getNameFromMeta(stack.getMetadata())+".bucket.name";
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		MetalMaterial mat = Items.getBucketByMeta(stack.getMetadata()).getMetalMaterial();
		if( mat != null ) {
			String unloc = "item.mmdbuckets.bucket.name";
			String form = getUnlocalizedName(stack);
			String name = mat.getCapitalizedName();
			
			if( I18n.canTranslate(unloc) )
				return I18n.translateToLocalFormatted(unloc, name);
			
			return form;
		}
		return super.getItemStackDisplayName(stack);
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		if( numBuckets == 0 ) {
			Items.init();
			numBuckets = Items.getCount();
		}
		
		subItems.add(new ItemStack(itemIn));
		for( int i = 0; i < numBuckets; i++ ) {
			ItemStack x = new ItemStack(itemIn, 1, i);
			if( !subItems.contains(x) ) {
				subItems.add( x );
			}
		}
	}
	
	/*
	 * Now for the fun stuff
	 */
	@Override
	public FluidStack getFluid(ItemStack container) {
		NBTTagCompound tags = container.getTagCompound();
		if( tags != null ) {
			return FluidStack.loadFluidStackFromNBT(tags.getCompoundTag("fluids"));
		}
		
		return null;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemIn, World worldIn, EntityPlayer playerIn, EnumHand handIn ) {
		if( getFluid(itemIn) != null ) {
			ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(playerIn, worldIn, itemIn, this.rayTrace(worldIn, playerIn, true));
			if(ret != null) {
				return ret;
			}

			return ActionResult.newResult(EnumActionResult.PASS, itemIn);
		}

		RayTraceResult finder = this.rayTrace(worldIn, playerIn, false);
		if(finder == null || finder.typeOfHit != RayTraceResult.Type.BLOCK) {
			return ActionResult.newResult(EnumActionResult.PASS, itemIn);
		}

		BlockPos clicked = finder.getBlockPos();
		// can we place our liquid here ?
		if(worldIn.isBlockModifiable(playerIn, clicked)) {
			BlockPos target = clicked.offset(finder.sideHit);

			// can we place something there ?
			if(playerIn.canPlayerEdit(target, finder.sideHit, itemIn)) {
				FluidStack fluidStack = getFluid(itemIn);
				if(FluidUtil.tryPlaceFluid(playerIn, playerIn.getEntityWorld(), fluidStack, target)) {
					if(fluidStack.getFluid() == FluidRegistry.WATER || fluidStack.getFluid() == FluidRegistry.LAVA) {
						worldIn.notifyBlockOfStateChange(target, worldIn.getBlockState(target).getBlock());
					}

					// only empty if not creative
					if(!playerIn.capabilities.isCreativeMode) {
						drain(itemIn, Fluid.BUCKET_VOLUME, true);
					}

					return ActionResult.newResult(EnumActionResult.SUCCESS, itemIn);
				}
			}
		}

		// couldn't place liquid there
		return ActionResult.newResult(EnumActionResult.FAIL, itemIn);
	}
	
	@SubscribeEvent
	public void onFillBucket(FillBucketEvent ev) {
		if( ev.getResult() != Event.Result.DEFAULT ) return;
		
		ItemStack empty = ev.getEmptyBucket();
		if( empty == null || !empty.getItem().equals(this) ) return;
		
		ItemStack bucket = empty.copy();
		bucket.stackSize = 1;
		
		RayTraceResult target = ev.getTarget();		
		if( target == null || target.typeOfHit != RayTraceResult.Type.BLOCK ) return;
		
		World world = ev.getWorld();
		BlockPos targetPos = target.getBlockPos();
		
		ItemStack filled = FluidUtil.tryPickUpFluid(bucket, ev.getEntityPlayer(), world, targetPos, target.sideHit);
		
		if( filled != null ) {
			ev.setResult(Event.Result.ALLOW);
			ev.setFilledBucket(filled);
		} else {
			ev.setCanceled(true);
		}
	}
}
