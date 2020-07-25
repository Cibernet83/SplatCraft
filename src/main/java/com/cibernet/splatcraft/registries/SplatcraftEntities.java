package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.renderer.InkProjectileRenderer;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.entities.InkSquidEntity;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftEntities
{
	
	public static final EntityType<InkProjectileEntity> INK_PROJECTILE = create("ink_projectile", InkProjectileEntity::new, EntityClassification.MISC);
	public static final EntityType<InkSquidEntity> INK_SQUID = create("ink_squid", InkSquidEntity::new, EntityClassification.AMBIENT, 0.6f, 0.6f);
	
	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
	{
		IForgeRegistry<EntityType<?>> registry = event.getRegistry();
		
		registry.register(INK_PROJECTILE);
		registry.register(INK_SQUID);
	}
	
	private static <T extends Entity> EntityType<T> create(String name, EntityType.IFactory<T> supplier, EntityClassification classification, float width, float height)
	{
		EntityType<T> type =  EntityType.Builder.create(supplier, classification).size(width,height).build(Splatcraft.MODID+":"+name);
		
		type.setRegistryName(name);
		return type;
	}
	
	private static <T extends LivingEntity> EntityType<T> createLiving(String name, EntityType.IFactory<T> supplier, EntityClassification classification, float width, float height, Consumer<AttributeModifierMap> map)
	{
		EntityType<T> type =  EntityType.Builder.create(supplier, classification).size(width,height).build(Splatcraft.MODID+":"+name);
		type.setRegistryName(name);
		
		
		
		//GlobalEntityTypeAttributes.put(type, map.accept();)
		
		return type;
	}
	
	private static <T extends Entity> EntityType<T> create(String name, EntityType.IFactory<T> supplier, EntityClassification classification)
	{
		return create(name, supplier, classification,1, 1);
	}
	
	public static void bindRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(INK_PROJECTILE, InkProjectileRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(INK_SQUID, InkSquidRenderer::new);
	}
}