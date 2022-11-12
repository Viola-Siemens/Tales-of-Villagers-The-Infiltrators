package com.hexagram2021.infiltrators.common.register;

import com.hexagram2021.infiltrators.Infiltrators;
import com.hexagram2021.infiltrators.common.config.InfCommonConfig;
import com.hexagram2021.infiltrators.common.entities.InfiltratorDataHolder;
import com.hexagram2021.infiltrators.common.items.SpecialBookItem;
import com.hexagram2021.infiltrators.common.util.InfDamageSources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class InfItems {
	private static final Item.Properties SPECIAL_BOOK_PROPERTIES = new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON).tab(Infiltrators.ITEM_GROUP);
	private static final Item.Properties BANNER_PATTERN_PROPERTIES = new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).tab(Infiltrators.ITEM_GROUP);
	private static final Item.Properties NORMAL_ITEM_PROPERTIES = new Item.Properties().tab(Infiltrators.ITEM_GROUP);
	
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	
	public static final RegistryObject<SpecialBookItem> SEER_BOOK = register("seer_book", () -> new SpecialBookItem(SPECIAL_BOOK_PROPERTIES) {
		@Override
		protected String doBookSpecialUse(ServerPlayer player, Villager villager, ItemStack itemStack, boolean fake) {
			boolean notWorking = fake && villager.getRandom().nextInt(100) < InfCommonConfig.FAKE_SPECIAL_BOOK_RATE.get();
			if(((InfiltratorDataHolder)villager).isInfiltrator() ^ notWorking) {
				InfTriggers.PLAYER_USE_SPECIAL_BOOK.trigger(player, villager, itemStack);
				return "message.seer.positive";
			}
			return "message.seer.negative";
		}
	});
	
	public static final RegistryObject<SpecialBookItem> SAVIOR_BOOK = register("savior_book", () -> new SpecialBookItem(SPECIAL_BOOK_PROPERTIES) {
		@Override
		protected String doBookSpecialUse(ServerPlayer player, Villager villager, ItemStack itemStack, boolean fake) {
			boolean notWorking = fake && villager.getRandom().nextInt(100) < InfCommonConfig.FAKE_SPECIAL_BOOK_RATE.get();
			if(!notWorking) {
				((InfiltratorDataHolder)villager).setImmuneToBadOmen();
			}
			InfTriggers.PLAYER_USE_SPECIAL_BOOK.trigger(player, villager, itemStack);
			return "message.savior.success";
		}
	});
	
	public static final RegistryObject<SpecialBookItem> HUNTER_BOOK = register("hunter_book", () -> new SpecialBookItem(SPECIAL_BOOK_PROPERTIES) {
		@Override
		protected String doBookSpecialUse(ServerPlayer player, Villager villager, ItemStack itemStack, boolean fake) {
			boolean notWorking = fake && villager.getRandom().nextInt(100) < InfCommonConfig.FAKE_SPECIAL_BOOK_RATE.get();
			if(!notWorking) {
				villager.hurt(InfDamageSources.HUNTED, Float.MAX_VALUE);
				InfTriggers.PLAYER_USE_SPECIAL_BOOK.trigger(player, villager, itemStack);
				return "message.hunter.positive";
			}
			return "message.hunter.negative";
		}
	});
	
	public static final RegistryObject<BannerPatternItem> VILLAGER_BANNER_PATTERN = register("villager_banner_pattern", () -> {
		String enumName = MODID + "_villager";
		String fullName = "inf_villager";
		BannerPattern pattern = BannerPattern.create(enumName.toUpperCase(), enumName, fullName, true);
		return new BannerPatternItem(pattern, BANNER_PATTERN_PROPERTIES);
	});
	
	public static final RegistryObject<BannerPatternItem> ILLAGER_BANNER_PATTERN = register("illager_banner_pattern", () -> {
		String enumName = MODID + "_illager";
		String fullName = "inf_illager";
		BannerPattern pattern = BannerPattern.create(enumName.toUpperCase(), enumName, fullName, true);
		return new BannerPatternItem(pattern, BANNER_PATTERN_PROPERTIES);
	});
	
	public static final RegistryObject<BlockItem> ANALYST_TABLE = register(InfBlocks.ANALYST_TABLE.getId().getPath(), () ->
			new BlockItem(InfBlocks.ANALYST_TABLE.get(), NORMAL_ITEM_PROPERTIES));
	
	private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
		return REGISTER.register(name, item);
	}
	
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
