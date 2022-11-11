package com.hexagram2021.infiltrators.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InfSounds {
	static final Set<SoundEvent> registeredEvents = new HashSet<>();
	
	public static final SoundEvent VILLAGER_WORK_PHARMACIST = registerSound("villager.work_pharmacist");
	
	@SuppressWarnings("SameParameterValue")
	private static SoundEvent registerSound(String name) {
		ResourceLocation location = new ResourceLocation(MODID, name);
		SoundEvent event = new SoundEvent(location);
		registeredEvents.add(event.setRegistryName(location));
		return event;
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> evt) {
		for(SoundEvent event : registeredEvents)
			evt.getRegistry().register(event);
	}
}
