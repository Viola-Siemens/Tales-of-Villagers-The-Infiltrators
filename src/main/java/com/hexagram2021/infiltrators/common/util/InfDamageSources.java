package com.hexagram2021.infiltrators.common.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class InfDamageSources {
	private static final ResourceKey<DamageType> HUNTED = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "hunted"));
	
	public static DamageSource hunted(LivingEntity victim) {
		return victim.damageSources().source(HUNTED);
	}
}
