package com.hexagram2021.infiltrators.common;

import com.hexagram2021.infiltrators.common.register.*;
import com.hexagram2021.infiltrators.common.util.InfSounds;
import com.hexagram2021.infiltrators.common.world.village.Village;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Consumer;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InfContent {
	@SuppressWarnings("unused")
	public static void modConstruction(IEventBus bus, Consumer<Runnable> runLater) {
		InfBlocks.init(bus);
		InfItems.init(bus);
		InfBannerPatterns.init(bus);
		InfBlockEntities.init(bus);
		InfTags.init();
		Village.Registers.init(bus);
		InfMenuTypes.init(bus);
	}
	
	public static void init() {
	
	}
	
	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		InfSounds.init(event);
	}
}
