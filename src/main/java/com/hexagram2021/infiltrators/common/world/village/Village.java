package com.hexagram2021.infiltrators.common.world.village;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hexagram2021.infiltrators.common.register.InfBlocks;
import com.hexagram2021.infiltrators.common.register.InfItems;
import com.hexagram2021.infiltrators.common.util.InfSounds;
import com.hexagram2021.infiltrators.mixin.HeroGiftsTaskAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
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

import static com.hexagram2021.infiltrators.Infiltrators.MODID;
import static com.hexagram2021.infiltrators.common.config.InfCommonConfig.*;

public final class Village {
	public static final ResourceLocation PHARMACIST = new ResourceLocation(MODID, "pharmacist");
	
	public static void init() {
		HeroGiftsTaskAccess.getGifts().put(Village.Registers.PHARMACIST.get(), new ResourceLocation(MODID, "gameplay/hero_of_the_village/pharmacist_gift"));
	}
	
	public static final class Registers {
		public static final DeferredRegister<PoiType> POINTS_OF_INTEREST = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);
		public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, MODID);
		
		public static final RegistryObject<PoiType> POI_ANALYST_TABLE = POINTS_OF_INTEREST.register(
				"analyst_table", () -> createPOI("analyst_table", assembleStates(InfBlocks.ANALYST_TABLE.get()))
		);
		
		public static final RegistryObject<VillagerProfession> PHARMACIST = PROFESSIONS.register(
				"pharmacist", () -> createProf(Village.PHARMACIST, POI_ANALYST_TABLE.get(), InfSounds.VILLAGER_WORK_PHARMACIST)
		);
		
		private static Collection<BlockState> assembleStates(Block block) {
			return block.getStateDefinition().getPossibleStates();
		}
		
		@SuppressWarnings("SameParameterValue")
		private static PoiType createPOI(String name, Collection<BlockState> block) {
			return new PoiType(new ResourceLocation(MODID, name).toString(), ImmutableSet.copyOf(block), 1, 1);
		}
		
		@SuppressWarnings("SameParameterValue")
		private static VillagerProfession createProf(ResourceLocation name, PoiType poi, SoundEvent sound) {
			return new VillagerProfession(
					name.toString(), poi,
					ImmutableSet.<Item>builder().build(),
					ImmutableSet.<Block>builder().build(),
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
			if(PHARMACIST.equals(event.getType().getRegistryName())) {
				event.getTrades().putAll(ImmutableMap.of(
						1, ImmutableList.of(
								new InfTrades.ItemsForEmeralds(InfItems.SAVIOR_BOOK::get, PRICE_SAVIOR_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_1_SELL),
								new InfTrades.ItemsForEmeralds(InfItems.SEER_BOOK::get, PRICE_SEER_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_1_SELL)
						),
						2, ImmutableList.of(
								new InfTrades.EmeraldForItems(Items.TOTEM_OF_UNDYING, 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_2_BUY),
								new InfTrades.EmeraldForItems(Items.SADDLE, 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_2_BUY),
								new InfTrades.ItemsForEmeralds(Items.NAME_TAG, 22, 1, InfTrades.DEFAULT_SUPPLY, InfTrades.XP_LEVEL_2_SELL)
						),
						3, ImmutableList.of(
								new InfTrades.ItemsForEmeralds(InfItems.HUNTER_BOOK::get, PRICE_HUNTER_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_3_SELL),
								new InfTrades.ItemsForEmeralds(InfItems.ALCHEMIST_BOOK::get, PRICE_ALCHEMIST_BOOK.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_3_SELL)
						),
						4, ImmutableList.of(
								new InfTrades.PotionItemsForEmeralds(Potions.HEALING, PRICE_POTION.get(), InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_4_SELL),
								new InfTrades.PotionItemsForEmeralds(Potions.REGENERATION, PRICE_POTION.get(), InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_4_SELL),
								new InfTrades.PotionItemsForEmeralds(Potions.STRENGTH, PRICE_POTION.get(), InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_4_SELL)
						),
						5, ImmutableList.of(
								new InfTrades.ItemsForEmeralds(InfItems.VILLAGER_BANNER_PATTERN::get, PRICE_BANNER_PATTERN.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_5_TRADE),
								new InfTrades.ItemsForEmeralds(InfItems.ILLAGER_BANNER_PATTERN::get, PRICE_BANNER_PATTERN.get(), 1, InfTrades.UNCOMMON_ITEMS_SUPPLY, InfTrades.XP_LEVEL_5_TRADE)
						)
				));
			}
		}
	}
}
