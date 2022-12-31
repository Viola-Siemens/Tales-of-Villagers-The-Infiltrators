package com.hexagram2021.infiltrators.client;

import com.hexagram2021.infiltrators.client.screen.AnalystTableScreen;
import com.hexagram2021.infiltrators.common.register.InfBannerPatterns;
import com.hexagram2021.infiltrators.common.register.InfMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;

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
		InfBannerPatterns.ALL_BANNERS.forEach(entry -> {
			ResourceKey<BannerPattern> pattern = Objects.requireNonNull(entry.pattern().getKey());
			Sheets.BANNER_MATERIALS.put(pattern, new Material(Sheets.BANNER_SHEET, BannerPattern.location(pattern, true)));
			Sheets.SHIELD_MATERIALS.put(pattern, new Material(Sheets.SHIELD_SHEET, BannerPattern.location(pattern, false)));
		});
	}
	
	@SubscribeEvent
	public static void registerTextureStitchPre(TextureStitchEvent.Pre event) {
		ResourceLocation sheet = event.getAtlas().location();
		if (sheet.equals(Sheets.BANNER_SHEET) || sheet.equals(Sheets.SHIELD_SHEET)) {
			InfBannerPatterns.ALL_BANNERS.forEach(entry -> {
				ResourceKey<BannerPattern> pattern = Objects.requireNonNull(entry.pattern().getKey());
				event.addSprite(BannerPattern.location(pattern, sheet.equals(Sheets.BANNER_SHEET)));
			});
		}
	}
}
