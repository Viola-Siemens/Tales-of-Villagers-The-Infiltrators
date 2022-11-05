package com.hexagram2021.infiltrators.common;

import com.hexagram2021.infiltrators.common.register.InfItems;
import com.hexagram2021.infiltrators.common.register.InfTags;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Consumer;

public class InfContent {
	public static void modConstruction(IEventBus bus, Consumer<Runnable> runLater) {
		InfItems.init(bus);
		InfTags.init();
	}
	
	public static void init() {
	
	}
}
