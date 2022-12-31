package com.hexagram2021.infiltrators.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class InfCommonConfig {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_POSSIBILITY_CAUSE_RAID;
	public static final ForgeConfigSpec.IntValue INFILTRATOR_CAUSE_RAID_TICK;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_POSSIBILITY_CHANGE_PROFESSION;
	public static final ForgeConfigSpec.IntValue INFILTRATOR_CHANGE_PROFESSION_TICK;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_CONVERT_ILLAGER_AND_JOIN_RAID_POSSIBILITY;
	public static final ForgeConfigSpec.IntValue INFILTRATOR_CONVERT_DELAY;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_BREAK_OTHERS_WORK;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_BREAK_WORKSTATION_MIN;
	public static final ForgeConfigSpec.IntValue INFILTRATOR_BREAK_WORKSTATION_MAX;
	
	public static final ForgeConfigSpec.IntValue INFILTRATOR_SPAWN_POSSIBILITY;
	
	public static final ForgeConfigSpec.IntValue FAKE_SPECIAL_BOOK_RATE;
	
	public static final ForgeConfigSpec.IntValue PRICE_ALCHEMIST_BOOK;
	public static final ForgeConfigSpec.IntValue PRICE_HUNTER_BOOK;
	public static final ForgeConfigSpec.IntValue PRICE_SAVIOR_BOOK;
	public static final ForgeConfigSpec.IntValue PRICE_SEER_BOOK;
	
	public static final ForgeConfigSpec.IntValue PRICE_POTION;
	
	public static final ForgeConfigSpec.IntValue PRICE_BANNER_PATTERN;
	
	static {
		BUILDER.push("infiltrators-common-config");
		
		BUILDER.push("time-cycle");
			INFILTRATOR_POSSIBILITY_CAUSE_RAID = BUILDER.comment("The possibility for an infiltrator causes raid each day.")
					.defineInRange("INFILTRATOR_POSSIBILITY_CAUSE_RAID", 10, 0, 100);
			INFILTRATOR_CAUSE_RAID_TICK = BUILDER.comment("The only tick in each day that infiltrators try to cause raid.")
					.defineInRange("INFILTRATOR_CAUSE_RAID_TICK", 14000, 0, 24000);
			
			INFILTRATOR_POSSIBILITY_CHANGE_PROFESSION = BUILDER.comment("The possibility for an infiltrator changes his own profession each day.")
					.defineInRange("INFILTRATOR_POSSIBILITY_CHANGE_PROFESSION", 25, 0, 100);
			INFILTRATOR_CHANGE_PROFESSION_TICK = BUILDER.comment("The only tick in each day that infiltrators try to change his profession.")
					.defineInRange("INFILTRATOR_CHANGE_PROFESSION_TICK", 50, 0, 24000);
		
		BUILDER.pop();
		
		BUILDER.push("raid");
			INFILTRATOR_CONVERT_ILLAGER_AND_JOIN_RAID_POSSIBILITY = BUILDER.comment("The possibility for an infiltrator converts to an illager and joins raid when he is near with another illager.")
					.defineInRange("INFILTRATOR_CONVERT_ILLAGER_AND_JOIN_RAID_POSSIBILITY", 10, 1, 100);
			INFILTRATOR_CONVERT_DELAY = BUILDER.comment("How many tick will an infiltrator try to convert to an illager again after last failure tick.")
					.defineInRange("INFILTRATOR_CONVERT_DELAY", 100, 20, 10000);
		BUILDER.pop();
		
		BUILDER.push("break-works");
			INFILTRATOR_BREAK_OTHERS_WORK = BUILDER.comment("The possibility for villagers been interrupted their works by infiltrators.")
					.defineInRange("INFILTRATOR_BREAK_OTHERS_WORK", 80, 0, 100);
			
			INFILTRATOR_BREAK_WORKSTATION_MIN = BUILDER.comment("The min possibility for an infiltrator breaks a workstation when he's pretending working at it.")
					.defineInRange("INFILTRATOR_BREAK_WORKSTATION_MIN", 5, 0, 100);
			INFILTRATOR_BREAK_WORKSTATION_MAX = BUILDER.comment("The max possibility for an infiltrator breaks a workstation when he's pretending working at it.")
					.defineInRange("INFILTRATOR_BREAK_WORKSTATION_MAX", 10, 0, 100);
			
			INFILTRATOR_SPAWN_POSSIBILITY = BUILDER.comment("The possibility for a villager spawned as an infiltrator.")
					.defineInRange("INFILTRATOR_SPAWN_POSSIBILITY", 5, 0, 100);
		BUILDER.pop();
		
		BUILDER.push("special-books");
			FAKE_SPECIAL_BOOK_RATE = BUILDER.comment("The possibility for a fake special book that can not work correctly.")
					.defineInRange("FAKE_SPECIAL_BOOK_RATE", 20, 0, 100);
		
			PRICE_ALCHEMIST_BOOK = BUILDER.comment("The price of a alchemist book (emeralds).")
					.defineInRange("PRICE_ALCHEMIST_BOOK", 26, 1, 64);
			PRICE_HUNTER_BOOK = BUILDER.comment("The price of a hunter book (emeralds).")
					.defineInRange("PRICE_HUNTER_BOOK", 20, 1, 64);
			PRICE_SAVIOR_BOOK = BUILDER.comment("The price of a savior book (emeralds).")
					.defineInRange("PRICE_SAVIOR_BOOK", 20, 1, 64);
			PRICE_SEER_BOOK = BUILDER.comment("The price of a seer book (emeralds).")
					.defineInRange("PRICE_SEER_BOOK", 24, 1, 64);
			
			PRICE_POTION = BUILDER.comment("The price of potions (emeralds).")
					.defineInRange("PRICE_POTION", 12, 1, 64);
			PRICE_BANNER_PATTERN = BUILDER.comment("The price of banner patterns (emeralds).")
					.defineInRange("PRICE_BANNER_PATTERN", 8, 1, 64);
		BUILDER.pop();
		
		BUILDER.pop();
		
		SPEC = BUILDER.build();
	}
}
