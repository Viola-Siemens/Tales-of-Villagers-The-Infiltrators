package com.hexagram2021.infiltrators.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.hexagram2021.infiltrators.common.entity.ai.behaviors.FakeAcquirePoi;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static net.minecraft.world.entity.EntityEvent.VILLAGER_HAPPY;

@SuppressWarnings("unused")
@Mixin(Villager.class)
public class VillagerEntityMixin {
	boolean isInfiltrator;
	
	@Inject(method = "registerBrainGoals", at = @At(value = "HEAD"), cancellable = true)
	private void getVillagerPanicPacket(Brain<Villager> brain, CallbackInfo ci) {
		Villager current = (Villager)(Object)this;
		
		if(this.isInfiltrator) {
			VillagerProfession villagerprofession = current.getVillagerData().getProfession();
			if (current.isBaby()) {
				brain.setSchedule(Schedule.VILLAGER_BABY);
				brain.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5F));
			} else {
				brain.setSchedule(Schedule.VILLAGER_DEFAULT);
				brain.addActivityWithConditions(Activity.WORK, VillagerGoalPackages.getWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
			}
			
			brain.addActivity(Activity.CORE, getInfiltratorCorePackage(villagerprofession, 0.5F));
			brain.addActivityWithConditions(Activity.MEET, VillagerGoalPackages.getMeetPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
			brain.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(villagerprofession, 0.5F));
			brain.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(villagerprofession, 0.5F));
			brain.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(villagerprofession, 0.6F));
			brain.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(villagerprofession, 0.5F));
			brain.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(villagerprofession, 0.5F));
			brain.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(villagerprofession, 0.5F));
			brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
			brain.setDefaultActivity(Activity.IDLE);
			brain.setActiveActivityIfPossible(Activity.IDLE);
			brain.updateActivityFromSchedule(current.level.getDayTime(), current.level.getGameTime());
			
			ci.cancel();
		}
	}
	
	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	private void addIsInfiltrator(CompoundTag nbt, CallbackInfo ci) {
		if(this.isInfiltrator) {
			nbt.putBoolean("IsInfiltrator", true);
		}
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	private void readIsInfiltrator(CompoundTag nbt, CallbackInfo ci) {
		if (nbt.contains("IsInfiltrator")) {
			this.isInfiltrator = nbt.getBoolean("IsInfiltrator");
		}
	}
	
	private static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getInfiltratorCorePackage(
			VillagerProfession villagerprofession, @SuppressWarnings("SameParameterValue") float speed
	) {
		return ImmutableList.of(
				Pair.of(0, new Swim(0.8F)),
				Pair.of(0, new InteractWithDoor()),
				Pair.of(0, new LookAtTargetSink(45, 90)),
				Pair.of(0, new VillagerPanicTrigger()),
				Pair.of(0, new WakeUp()),
				Pair.of(0, new ReactToBell()),
				Pair.of(0, new SetRaidStatus()),
				Pair.of(0, new ValidateNearbyPoi(villagerprofession.getJobPoiType(), MemoryModuleType.JOB_SITE)),
				Pair.of(0, new ValidateNearbyPoi(villagerprofession.getJobPoiType(), MemoryModuleType.POTENTIAL_JOB_SITE)),
				Pair.of(1, new MoveToTargetSink()),
				Pair.of(2, new PoiCompetitorScan(villagerprofession)),
				Pair.of(3, new LookAndFollowTradingPlayerSink(speed)),
				Pair.of(5, new GoToWantedItem<>(speed, false, 4)),
				Pair.of(6, new FakeAcquirePoi(villagerprofession.getJobPoiType(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true)),
				Pair.of(7, new GoToPotentialJobSite(speed)),
				Pair.of(8, new YieldJobSite(speed)),
				Pair.of(10, new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false, Optional.of(VILLAGER_HAPPY))),
				Pair.of(10, new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of(VILLAGER_HAPPY))),
				Pair.of(10, new AssignProfessionFromJobSite()),
				Pair.of(10, new ResetProfession())
		);
	}
}
