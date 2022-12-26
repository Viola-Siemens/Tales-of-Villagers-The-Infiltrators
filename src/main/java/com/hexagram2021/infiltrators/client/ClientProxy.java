package com.hexagram2021.infiltrators.client;

import com.hexagram2021.infiltrators.client.screen.AnalystTableScreen;
import com.hexagram2021.infiltrators.common.register.InfItems;
import com.hexagram2021.infiltrators.common.register.InfMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {
	public static void modConstruction() {
	
	}
	
	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			registerRenderLayers();
			registerContainersAndScreens();
			registerBannerPatterns();
		});
	}
	
	private static void registerRenderLayers() {
	
	}
	
	private static void registerContainersAndScreens() {
		MenuScreens.register(InfMenuTypes.ANALYST_TABLE_MENU.get(), AnalystTableScreen::new);
	}
	
	private static void registerBannerPatterns() {
		registerBannerPattern(InfItems.VILLAGER_BANNER_PATTERN.get().getBannerPattern());
		registerBannerPattern(InfItems.ILLAGER_BANNER_PATTERN.get().getBannerPattern());
	}
	
	private static void registerBannerPattern(BannerPattern pattern) {
		Sheets.BANNER_MATERIALS.put(pattern, new Material(Sheets.BANNER_SHEET, pattern.location(true)));
		Sheets.SHIELD_MATERIALS.put(pattern, new Material(Sheets.SHIELD_SHEET, pattern.location(false)));
	}
	
	@SubscribeEvent
	public static void registerTextureStitchPre(TextureStitchEvent.Pre event) {
		ResourceLocation sheet = event.getAtlas().location();
		if (sheet.equals(Sheets.BANNER_SHEET) || sheet.equals(Sheets.SHIELD_SHEET)) {
			event.addSprite(InfItems.VILLAGER_BANNER_PATTERN.get().getBannerPattern().location(sheet.equals(Sheets.BANNER_SHEET)));
			event.addSprite(InfItems.ILLAGER_BANNER_PATTERN.get().getBannerPattern().location(sheet.equals(Sheets.BANNER_SHEET)));
		}
	}
}
