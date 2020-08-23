package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ClientUtils;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class WeaponBaseItem extends Item
{
	public WeaponBaseItem()
	{
		super(new Properties().maxStackSize(1).group(SplatcraftItemGroups.GROUP_WEAPONS));
		SplatcraftItems.inkColoredItems.add(this);
		SplatcraftItems.weapons.add(this);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		
		if(ColorUtils.isColorLocked(stack))
			tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		
		if(entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity))
			ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
	}
	
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		BlockPos pos = entity.getPosition().down();
		
		if(entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock)
		{
			InkColorTileEntity te = (InkColorTileEntity) entity.world.getTileEntity(pos);
			
			if(ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
			{
				ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
				ColorUtils.setColorLocked(entity.getItem(), true);
			}
		}
		
		return false;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		try
		{
			return ClientUtils.getDurabilityForDisplay(stack);
		}catch(NoClassDefFoundError e)
		{
			return 1;
		}
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return ColorUtils.getInkColor(stack);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		try
		{
			return ClientUtils.showDurabilityBar(stack);
		}catch(NoClassDefFoundError e)
		{
			return false;
		}
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		player.setActiveHand(hand);
		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft)
	{
		entity.resetActiveHand();
		super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
	}
	
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
	
	}
	
	public static float getInkAmount(LivingEntity player, ItemStack weapon)
	{
		ItemStack tank = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
		if(!(tank.getItem() instanceof InkTankItem))
			return 0;
		
		if(SplatcraftGameRules.getBooleanRuleValue(player.world, SplatcraftGameRules.REQUIRE_INK_TANK))
			return InkTankItem.getInkAmount(tank, weapon);
		return ((InkTankItem) tank.getItem()).capacity;
	}
	
	public static boolean hasInk(LivingEntity player, ItemStack weapon)
	{
		return getInkAmount(player, weapon) > 0;
	}
	
	public static void reduceInk(LivingEntity player, float amount)
	{
		ItemStack tank = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
		if(!(tank.getItem() instanceof InkTankItem))
			return;
		if(!SplatcraftGameRules.getBooleanRuleValue(player.world, SplatcraftGameRules.REQUIRE_INK_TANK))
			return;
		
		InkTankItem.setInkAmount(tank, InkTankItem.getInkAmount(tank) - amount);
	}
}
