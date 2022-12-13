package com.hexagram2021.infiltrators.common;

import com.hexagram2021.infiltrators.common.register.*;
import com.hexagram2021.infiltrators.common.world.village.Village;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Consumer;

public class InfContent {
	public static void modConstruction(IEventBus bus, Consumer<Runnable> runLater) {
		InfBlocks.init(bus);
		InfItems.init(bus);
		InfBlockEntities.init(bus);
		InfTags.init();
		Village.Registers.init(bus);
		InfMenuTypes.init(bus);
	}
	
	public static void init() {
	
	}
}
