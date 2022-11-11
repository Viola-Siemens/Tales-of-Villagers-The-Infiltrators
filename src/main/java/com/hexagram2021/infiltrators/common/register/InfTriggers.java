package com.hexagram2021.infiltrators.common.register;

import com.hexagram2021.infiltrators.common.util.triggers.VillagerGetKilledTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class InfTriggers {
	public static final VillagerGetKilledTrigger VILLAGER_GET_KILLED = CriteriaTriggers.register(new VillagerGetKilledTrigger());
	
	public static void init() { }
}
