package com.hexagram2021.infiltrators.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public interface RegistryHelper {
	static ResourceLocation getRegistryName(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}
	static ResourceLocation getRegistryName(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}
	static ResourceLocation getRegistryName(VillagerProfession profession) {
		return ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession);
	}
	static ResourceLocation getRegistryName(Biome biome) {
		return ForgeRegistries.BIOMES.getKey(biome);
	}
}
