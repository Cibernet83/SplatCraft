package com.cibernet.splatcraft.gui;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ICharge;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiOverlay extends Gui
{
	public static final GuiOverlay instance = new GuiOverlay();
	private final ResourceLocation OVERLAY = new ResourceLocation(SplatCraft.MODID, "textures/gui/charger_overlay.png");
	
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();
			EntityPlayer player = mc.player;
			ItemStack stack = player.getHeldItemMainhand();
			
			if(!(stack.getItem() instanceof ICharge))
				stack = player.getHeldItemOffhand();
			
			if(stack.getItem() instanceof ICharge )
			{
				mc.renderEngine.bindTexture(OVERLAY);
				int x = width / 2 - 9;
				int y = height / 2 - 9;
				int charge = (int) (SplatCraftPlayerData.getWeaponCharge(player, stack)*59);
				int tx = (charge % 13) * 19;
				int ty = (charge/6) * 19;
						
						System.out.println(charge);
				
				GlStateManager.enableAlpha();
				drawTexturedModalRect(x, y, tx, ty, 19, 19);
			}
			
		}
	}
}