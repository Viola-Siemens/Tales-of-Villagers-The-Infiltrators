package com.hexagram2021.infiltrators.common.register;

import com.hexagram2021.infiltrators.common.crafting.AnalystTableMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class InfMenuTypes {
	public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
	
	public static final RegistryObject<MenuType<AnalystTableMenu>> ANALYST_TABLE_MENU = REGISTER.register(
			"analyst_table", () -> new MenuType<>(AnalystTableMenu::new, FeatureFlags.VANILLA_SET)
	);
	
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
