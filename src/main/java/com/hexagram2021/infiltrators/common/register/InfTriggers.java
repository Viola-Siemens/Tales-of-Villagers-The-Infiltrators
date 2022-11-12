package com.hexagram2021.infiltrators.common.register;

import com.hexagram2021.infiltrators.common.util.triggers.PlayerUseSpecialBookTrigger;
import com.hexagram2021.infiltrators.common.util.triggers.VillagerGetKilledTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class InfTriggers {
	public static final VillagerGetKilledTrigger VILLAGER_GET_KILLED = CriteriaTriggers.register(new VillagerGetKilledTrigger());
	
	public static final PlayerUseSpecialBookTrigger PLAYER_USE_SPECIAL_BOOK = CriteriaTriggers.register(new PlayerUseSpecialBookTrigger());
	
	public static void init() { }
}
