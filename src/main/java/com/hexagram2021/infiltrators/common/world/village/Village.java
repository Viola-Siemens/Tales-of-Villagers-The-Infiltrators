package com.hexagram2021.infiltrators.common.world.village;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.infiltrators.common.register.InfBannerPatterns;
import com.hexagram2021.infiltrators.common.register.InfBlocks;
import com.hexagram2021.infiltrators.common.register.InfItems;
import com.hexagram2021.infiltrators.common.util.InfSounds;
import com.hexagram2021.infiltrators.mixin.HeroGiftsTaskAccess;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;
import static com.hexagram2021.infiltrators.common.config.InfCommonConfig.*;
import static com.hexagram2021.infiltrators.common.util.RegistryHelper.getRegistryName;

public final class Village {
	public static final ResourceLocation PHARMACIST = new ResourceLocation(MODID, "pharmacist");
	
	public static void init() {
		HeroGiftsTaskAccess.getGifts().put(Village.Registers.PHARMACIST.get(), new ResourceLocation(MODID, "gameplay/hero_of_the_village/pharmacist_gift"));
	}
	
	public static final class Registers {
		public static final DeferredRegister<PoiType> POINTS_OF_INTEREST = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);
		public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, MODID);
		
		public static final RegistryObject<PoiType> POI_ANALYST_TABLE = POINTS_OF_INTEREST.register(
				"analyst_table", () -> createPOI(assembleStates(InfBlocks.ANALYST_TABLE.get()))
		);
		
		public static final RegistryObject<VillagerProfession> PHARMACIST = PROFESSIONS.register(
				"pharmacist", () -> createProf(Village.PHARMACIST, POI_ANALYST_TABLE::getKey, InfSounds.VILLAGER_WORK_PHARMACIST)
		);
		
		private static Collection<BlockState> assembleStates(Block block) {
			return block.getStateDefinition().getPossibleStates();
		}
		
		@SuppressWarnings("SameParameterValue")
		private static PoiType createPOI(Collection<BlockState> block) {
			return new PoiType(ImmutableSet.copyOf(block), 1, 1);
		}
		
		@SuppressWarnings("SameParameterValue")
		private static VillagerProfession createProf(ResourceLocation name, Supplier<ResourceKey<PoiType>> poi, SoundEvent sound) {
			ResourceKey<PoiType> poiName = poi.get();
			return new VillagerProfession(
					name.toString(),
					p -> p.is(poiName),
					p -> p.is(poiName),
					ImmutableSet.of(),
					ImmutableSet.of(),
					sound
			);
		}
		
		public static void init(IEventBus bus) {
			POINTS_OF_INTEREST.register(bus);
			PROFESSIONS.register(bus);
		}
	}
	
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Events {
		@SubscribeEvent
		public static void registerTrades(VillagerTradesEvent event) {
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
			if (PHARMACIST.equals(getRegistryName(event.getType()))) {
				trades.get(1).add(new InfTrades.ItemsForEmeralds(InfItems.SAVIOR_BOOK::get, PRICE_SAVIOR_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_1_SELL));
				trades.get(1).add(new InfTrades.ItemsForEmeralds(InfItems.SEER_BOOK::get, PRICE_SEER_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_1_SELL));
				trades.get(2).add(new InfTrades.EmeraldForItems(Items.TOTEM_OF_UNDYING, 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_2_BUY));
				trades.get(2).add(new InfTrades.EmeraldForItems(Items.SADDLE, 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_2_BUY));
				trades.get(2).add(new InfTrades.ItemsForEmeralds(Items.NAME_TAG, 22, 1, InfTrades.DEFAULT_SUPPLY, InfTrades.XP_LEVEL_2_SELL));
				trades.get(3).add(new InfTrades.ItemsForEmeralds(InfItems.HUNTER_BOOK::get, PRICE_HUNTER_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_3_SELL));
				trades.get(3).add(new InfTrades.ItemsForEmeralds(InfItems.ALCHEMIST_BOOK::get, PRICE_ALCHEMIST_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_3_SELL));
				trades.get(4).add(new InfTrades.PotionItemsForEmeralds(Potions.HEALING, PRICE_POTION.get(), InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_4_SELL));
				trades.get(4).add(new InfTrades.PotionItemsForEmeralds(Potions.REGENERATION, PRICE_POTION.get(), InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_4_SELL));
				trades.get(4).add(new InfTrades.PotionItemsForEmeralds(Potions.STRENGTH, PRICE_POTION.get(), InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_4_SELL));
				trades.get(5).add(new InfTrades.ItemsForEmeralds(InfBannerPatterns.VILLAGER.item()::get, PRICE_BANNER_PATTERN.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_5_TRADE));
				trades.get(5).add(new InfTrades.ItemsForEmeralds(InfBannerPatterns.ILLAGER.item()::get, PRICE_BANNER_PATTERN.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_5_TRADE));
			}
		}
	}
}
