package com.hexagram2021.infiltrators.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class InfBannerPatterns {
	public static final DeferredRegister<BannerPattern> REGISTER = DeferredRegister.create(Registries.BANNER_PATTERN, MODID);
	
	public static final List<BannerEntry> ALL_BANNERS = new ArrayList<>();
	
	public static final BannerEntry VILLAGER = addBanner("villager", "villager");
	public static final BannerEntry ILLAGER = addBanner("illager", "illager");
	
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
	
	private static BannerEntry addBanner(String name, String hashName) {
		RegistryObject<BannerPattern> pattern = REGISTER.register(name, () -> new BannerPattern("inf_"+hashName));
		TagKey<BannerPattern> tag = TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(MODID, "pattern_item/" + name));
		InfItems.ItemEntry<BannerPatternItem> item = InfItems.register(
				name + "_banner_pattern",
				() -> new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
				(props) -> new BannerPatternItem(tag, props)
		);
		BannerEntry result = new BannerEntry(pattern, tag, item);
		ALL_BANNERS.add(result);
		return result;
	}
	
	public record BannerEntry(
			RegistryObject<BannerPattern> pattern,
			TagKey<BannerPattern> tag,
			InfItems.ItemEntry<BannerPatternItem> item
	) { }
}
