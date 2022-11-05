package com.hexagram2021.infiltrators.common.register;

import com.hexagram2021.infiltrators.Infiltrators;
import com.hexagram2021.infiltrators.common.SpecialBookItem;
import com.hexagram2021.infiltrators.common.entity.InfiltratorDataHolder;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class InfItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	
	public static final RegistryObject<SpecialBookItem> SEER_BOOK = register("seer_book", () -> new SpecialBookItem(new Item.Properties().stacksTo(16).tab(Infiltrators.ITEM_GROUP)) {
		@Override
		protected String doBookSpecialUse(Villager villager) {
			return ((InfiltratorDataHolder)villager).isInfiltrator() ? "message.seer.positive" : "message.seer.negative";
		}
	});
	
	public static final RegistryObject<SpecialBookItem> SAVIOR_BOOK = register("savior_book", () -> new SpecialBookItem(new Item.Properties().stacksTo(16).tab(Infiltrators.ITEM_GROUP)) {
		@Override
		protected String doBookSpecialUse(Villager villager) {
			((InfiltratorDataHolder)villager).setImmuneToBadOmen();
			return "message.savior.success";
		}
	});
	
	private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
		return REGISTER.register(name, item);
	}
	
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
