package com.hexagram2021.infiltrators.common.register;

import com.google.common.collect.Lists;
import com.hexagram2021.infiltrators.common.config.InfCommonConfig;
import com.hexagram2021.infiltrators.common.entities.InfiltratorDataHolder;
import com.hexagram2021.infiltrators.common.items.SpecialBookItem;
import com.hexagram2021.infiltrators.common.util.InfDamageSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

@SuppressWarnings("unused")
public class InfItems {
	private static final Supplier<Item.Properties> SPECIAL_BOOK_PROPERTIES = () -> new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON);
	private static final Supplier<Item.Properties> NORMAL_ITEM_PROPERTIES = Item.Properties::new;
	
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	
	public static final ItemEntry<SpecialBookItem> SEER_BOOK = register("seer_book", SPECIAL_BOOK_PROPERTIES, (props) -> new SpecialBookItem(props) {
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
	
	public static final ItemEntry<SpecialBookItem> SAVIOR_BOOK = register("savior_book", SPECIAL_BOOK_PROPERTIES, (props) -> new SpecialBookItem(props) {
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
	
	public static final ItemEntry<SpecialBookItem> HUNTER_BOOK = register("hunter_book", SPECIAL_BOOK_PROPERTIES, (props) -> new SpecialBookItem(props) {
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
	
	public static final ItemEntry<SpecialBookItem> ALCHEMIST_BOOK = register("alchemist_book", SPECIAL_BOOK_PROPERTIES, (props) -> new SpecialBookItem(props) {
		@Override
		protected String doBookSpecialUse(ServerPlayer player, Villager villager, ItemStack itemStack, boolean fake) {
			boolean notWorking = fake && villager.getRandom().nextInt(100) < InfCommonConfig.FAKE_SPECIAL_BOOK_RATE.get();
			if(!notWorking) {
				villager.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2400, 1), player);
				villager.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 1), player);
				InfTriggers.PLAYER_USE_SPECIAL_BOOK.trigger(player, villager, itemStack);
				return "message.alchemist.positive";
			}
			return "message.alchemist.negative";
		}
	});
	
	public static final ItemEntry<BlockItem> ANALYST_TABLE = register(InfBlocks.ANALYST_TABLE.getId().getPath(), NORMAL_ITEM_PROPERTIES,
			(props) -> new BlockItem(InfBlocks.ANALYST_TABLE.get(), props));
	
	static <T extends Item> ItemEntry<T> register(String name, Supplier<Item.Properties> props, Function<Item.Properties, T> factory) {
		return new ItemEntry<>(name, props, factory);
	}
	
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	public static final class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {
		public static final List<ItemEntry<? extends Item>> REGISTERED_ITEMS = Lists.newArrayList();

		private final RegistryObject<T> item;
		private final Supplier<Item.Properties> properties;

		public ItemEntry(String name, Supplier<Item.Properties> properties, Function<Item.Properties, T> make) {
			this.properties = properties;
			this.item = REGISTER.register(name, () -> make.apply(properties.get()));

			REGISTERED_ITEMS.add(this);
		}

		@Override @NotNull
		public T get() {
			return this.item.get();
		}

		public ResourceLocation getId() {
			return this.item.getId();
		}

		public Item.Properties getProperties() {
			return this.properties.get();
		}

		@NotNull
		@Override
		public Item asItem() {
			return this.item.get();
		}
	}
}
