package com.hexagram2021.infiltrators.common.register;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.infiltrators.common.blocks.entities.AnalystTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class InfBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
	
	public static final RegistryObject<BlockEntityType<AnalystTableBlockEntity>> ANALYST_TABLE = REGISTER.register(
		"analyst_table", () -> new BlockEntityType<>(
				AnalystTableBlockEntity::new, ImmutableSet.of(InfBlocks.ANALYST_TABLE.get()), null
			)
	);
	
	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
