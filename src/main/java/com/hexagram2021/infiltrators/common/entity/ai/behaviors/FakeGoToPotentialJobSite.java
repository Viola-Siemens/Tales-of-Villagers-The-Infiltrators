package com.hexagram2021.infiltrators.common.entity.ai.behaviors;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;

public class FakeGoToPotentialJobSite extends Behavior<Villager> {
	private static final int TICKS_UNTIL_TIMEOUT = 1200;
	final float speedModifier;
	
	public FakeGoToPotentialJobSite(float p_23098_) {
		super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT), TICKS_UNTIL_TIMEOUT);
		this.speedModifier = p_23098_;
	}
	
	@Override
	protected boolean checkExtraStartConditions(@NotNull ServerLevel level, Villager villager) {
		return villager.getBrain().getActiveNonCoreActivity().map((p_23115_) ->
				p_23115_ == Activity.IDLE || p_23115_ == Activity.WORK || p_23115_ == Activity.PLAY).orElse(true);
	}
	
	@Override
	protected boolean canStillUse(@NotNull ServerLevel level, Villager villager, long tick) {
		return villager.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
	}
	
	@Override
	protected void tick(@NotNull ServerLevel level, @NotNull Villager villager, long tick) {
		BehaviorUtils.setWalkAndLookTargetMemories(villager, villager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos(), this.speedModifier, 1);
	}
	
	@Override
	protected void stop(@NotNull ServerLevel level, Villager villager, long tick) {
		villager.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
	}
}
