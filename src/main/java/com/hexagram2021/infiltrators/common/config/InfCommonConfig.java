package com.hexagram2021.infiltrators.common.config;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public class InfCommonConfig {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_POSSIBILITY_CAUSE_RAID;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_CAUSE_RAID_TICK;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_POSSIBILITY_CHANGE_PROFESSION;
	public static final ForgeConfigSpec.IntValue INFILTRATOR_CHANGE_PROFESSION_TICK;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_BREAK_OTHERS_WORK;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_BREAK_WORKSTATION_MIN;
	public static final ForgeConfigSpec.IntValue INFILTRATOR_BREAK_WORKSTATION_MAX;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_SPAWN_POSSIBILITY;
	
	static {
		BUILDER.push("infiltrators-common-config");
		
		INFILTRATOR_POSSIBILITY_CAUSE_RAID = BUILDER.comment("The possibility for an infiltrator causes raid each day.")
				.defineInRange("INFILTRATOR_POSSIBILITY_CAUSE_RAID", 10, 0, 100);
		INFILTRATOR_CAUSE_RAID_TICK = BUILDER.comment("The only tick in each day that infiltrators try to cause raid.")
				.defineInRange("INFILTRATOR_CAUSE_RAID_TICK", 14000, 0, 24000);
		
		INFILTRATOR_POSSIBILITY_CHANGE_PROFESSION = BUILDER.comment("The possibility for an infiltrator changes his own profession each day.")
				.defineInRange("INFILTRATOR_POSSIBILITY_CHANGE_PROFESSION", 25, 0, 100);
		INFILTRATOR_CHANGE_PROFESSION_TICK = BUILDER.comment("The only tick in each day that infiltrators try to change his profession.")
				.defineInRange("INFILTRATOR_CHANGE_PROFESSION_TICK", 50, 0, 24000);
		
		INFILTRATOR_BREAK_OTHERS_WORK = BUILDER.comment("The possibility for villagers been interrupted their works by infiltrators.")
				.defineInRange("INFILTRATOR_BREAK_OTHERS_WORK", 80, 0, 100);
		
		INFILTRATOR_BREAK_WORKSTATION_MIN = BUILDER.comment("The min possibility for an infiltrator breaks a workstation when he's pretending working at it.")
				.defineInRange("INFILTRATOR_BREAK_WORKSTATION_MIN", 5, 0, 100);
		INFILTRATOR_BREAK_WORKSTATION_MAX = BUILDER.comment("The max possibility for an infiltrator breaks a workstation when he's pretending working at it.")
				.defineInRange("INFILTRATOR_BREAK_WORKSTATION_MAX", 10, 0, 100);
		
		INFILTRATOR_SPAWN_POSSIBILITY = BUILDER.comment("The possibility for a villager spawned as an infiltrator.")
				.defineInRange("INFILTRATOR_SPAWN_POSSIBILITY", 5, 0, 100);
		
		BUILDER.pop();
		
		SPEC = BUILDER.build();
	}
}
