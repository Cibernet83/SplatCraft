package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.commands.ClearInkCommand;
import com.cibernet.splatcraft.commands.InkColorCommand;
import com.cibernet.splatcraft.commands.ReplaceColorCommand;
import com.cibernet.splatcraft.commands.ScanTurfCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SplatcraftCommands
{
	@SubscribeEvent
	public static void registerCommands(FMLServerStartingEvent event)
	{
		CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();
		
		InkColorCommand.register(dispatcher);
		ScanTurfCommand.register(dispatcher);
		ClearInkCommand.register(dispatcher);
		ReplaceColorCommand.register(dispatcher);
	}
}