package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.commands.*;
import com.cibernet.splatcraft.commands.arguments.ColorCriterionArgument;
import com.cibernet.splatcraft.commands.arguments.InkColorArgument;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SplatcraftCommands
{
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        InkColorCommand.register(dispatcher);
        ScanTurfCommand.register(dispatcher);
        ClearInkCommand.register(dispatcher);
        ReplaceColorCommand.register(dispatcher);
        ColorScoresCommand.register(dispatcher);
    }

    public static void registerArguments()
    {
        ArgumentTypes.register(Splatcraft.MODID + ":ink_color", InkColorArgument.class, new ArgumentSerializer<>(InkColorArgument::inkColor));
        ArgumentTypes.register(Splatcraft.MODID + ":color_criterion", ColorCriterionArgument.class, new ArgumentSerializer<>(ColorCriterionArgument::colorCriterion));
    }
}
