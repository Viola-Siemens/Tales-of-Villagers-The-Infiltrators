package com.hexagram2021.infiltrators.common.entity.ai.behaviors;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class FakeWorkAtPoi extends WorkAtPoi {
	@Override
	protected void useWorkstation(@NotNull ServerLevel level, @NotNull Villager villager) {
		InfiltratorDataHolder infiltrator = (InfiltratorDataHolder)villager;
		if(villager.getRandom().nextInt(100) < infiltrator.getPossibilityBreakingWorkstation()) {
			villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).ifPresent(globalPos ->
					level.setBlock(globalPos.pos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
			infiltrator.resetPossibilityBreakingWorkstation();
		} else {
			infiltrator.increasePossibilityBreakingWorkstation();
		}
	}
}
