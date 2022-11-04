package com.hexagram2021.infiltrators.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.hexagram2021.infiltrators.common.entity.ai.behaviors.*;
import com.hexagram2021.infiltrators.common.entity.InfiltratorDataHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static net.minecraft.world.entity.EntityEvent.VILLAGER_ANGRY;
import static net.minecraft.world.entity.EntityEvent.VILLAGER_HAPPY;

@SuppressWarnings("unused")
@Mixin(Villager.class)
public class VillagerEntityMixin implements InfiltratorDataHolder {
	private static final int INFILTRATOR_POSSIBILITY_CAUSE_RAID = 20;
	private static final int INFILTRATOR_CAUSE_RAID_TICK = 14000;
	
	private static final int INFILTRATOR_BREAK_OTHERS_WORK = 80;
	
	private static final int INFILTRATOR_BREAK_WORKSTATION_MIN = 5;
	private static final int INFILTRATOR_BREAK_WORKSTATION_MAX = 10;
	
	boolean isInfiltrator;
	
	int possibilityBreakingWorkstation = INFILTRATOR_BREAK_WORKSTATION_MIN;
	
	@Inject(method = "registerBrainGoals", at = @At(value = "HEAD"), cancellable = true)
	private void registerInfiltratorBrainGoals(Brain<Villager> brain, CallbackInfo ci) {
		Villager current = (Villager)(Object)this;
		
		if(this.isInfiltrator) {
			VillagerProfession villagerprofession = current.getVillagerData().getProfession();
			if (current.isBaby()) {
				brain.setSchedule(Schedule.VILLAGER_BABY);
				brain.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5F));
			} else {
				brain.setSchedule(Schedule.VILLAGER_DEFAULT);
				brain.addActivityWithConditions(Activity.WORK, getInfiltratorWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
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
			nbt.putInt("PossibilityBreakingWorkstation", this.possibilityBreakingWorkstation);
		}
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	private void readIsInfiltrator(CompoundTag nbt, CallbackInfo ci) {
		if (nbt.contains("IsInfiltrator")) {
			this.isInfiltrator = nbt.getBoolean("IsInfiltrator");
			this.possibilityBreakingWorkstation = nbt.getInt("PossibilityBreakingWorkstation");
		}
	}
	
	@Redirect(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;getPlayerReputation(Lnet/minecraft/world/entity/player/Player;)I"))
	private int getInfiltratorPlayerReputation(Villager instance, Player player) {
		if(((InfiltratorDataHolder)instance).isInfiltrator()) {
			return 0;
		}
		return instance.getPlayerReputation(player);
	}
	
	@Redirect(method = "releaseAllPois", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;releasePoi(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)V", ordinal = 1))
	public void doNotReleaseJobSiteIfInfiltrator(Villager instance, MemoryModuleType<GlobalPos> memoryModuleType) {
		//Do nothing
	}
	@Redirect(method = "releaseAllPois", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;releasePoi(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)V", ordinal = 2))
	public void doNotReleasePotentialJobSiteIfInfiltrator(Villager instance, MemoryModuleType<GlobalPos> memoryModuleType) {
		//Do nothing
	}
	
	@Inject(method = "shouldRestock", at = @At(value = "HEAD"), cancellable = true)
	public void checkIfInfiltrator(CallbackInfoReturnable<Boolean> cir) {
		if(this.isInfiltrator()) {
			cir.setReturnValue(Boolean.FALSE);
			cir.cancel();
		}
	}
	
	@Inject(method = "restock", at = @At(value = "HEAD"), cancellable = true)
	public void restockBroken(CallbackInfo ci) {
		if(this.isInfiltrator()) {
			ci.cancel();
		}
		
		Villager current = (Villager)(Object)this;
		AABB aabb = new AABB(current.getX() - 20.0D, current.getX() + 20.0D, current.getY() - 8.0D, current.getY() + 8.0D, current.getZ() - 20.0D, current.getZ() + 20.0D);
		if(!current.level.getEntities(current, aabb, entity -> entity instanceof Villager && ((InfiltratorDataHolder)entity).isInfiltrator()).isEmpty() &&
				current.getRandom().nextInt(100) < INFILTRATOR_BREAK_OTHERS_WORK) {
			//TODO: delete this in release
			current.level.broadcastEntityEvent(current, VILLAGER_ANGRY);
			ci.cancel();
		}
	}
	
	@Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/AbstractVillager;customServerAiStep()V"))
	protected void tryCauseRaid(CallbackInfo ci) {
		Villager current = (Villager)(Object)this;
		
		if(this.isInfiltrator() && current.level.dayTime() == INFILTRATOR_CAUSE_RAID_TICK && current.getRandom().nextInt(100) < INFILTRATOR_POSSIBILITY_CAUSE_RAID) {
			Player player = current.level.getNearestPlayer(current, 32.0D);
			if(player != null) {
				player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 20, 5));
			}
		}
	}
	
	@Override
	public boolean isInfiltrator() {
		return this.isInfiltrator;
	}
	
	@Override
	public void resetPossibilityBreakingWorkstation() {
		this.possibilityBreakingWorkstation = INFILTRATOR_BREAK_WORKSTATION_MIN;
	}
	
	@Override
	public int getPossibilityBreakingWorkstation() {
		return this.possibilityBreakingWorkstation;
	}
	
	@Override
	public void increasePossibilityBreakingWorkstation() {
		this.possibilityBreakingWorkstation += 1;
	}
	
	//TODO: Infiltrator will NOT give gift to hero.
	private static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getInfiltratorWorkPackage(
			VillagerProfession villagerprofession, @SuppressWarnings("SameParameterValue") float speed
	) {
		return ImmutableList.of(
				getMinimalLookBehavior(),
				Pair.of(5, new RunOne<>(ImmutableList.of(
						Pair.of(new FakeWorkAtPoi(), 6),
						Pair.of(new StrollAroundPoi(MemoryModuleType.JOB_SITE, 0.4F, 4), 3),
						Pair.of(new StrollToPoi(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
						Pair.of(new StrollToPoiList(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5)
				))),
				Pair.of(10, new ShowTradesToPlayer(400, 1600)),
				Pair.of(10, new SetLookAndInteract(EntityType.PLAYER, 4)),
				Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)),
				Pair.of(99, new UpdateActivityFromSchedule())
		);
	}
	
	private static Pair<Integer, Behavior<LivingEntity>> getMinimalLookBehavior() {
		return Pair.of(5, new RunOne<>(ImmutableList.of(
				Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2),
				Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2),
				Pair.of(new DoNothing(30, 60), 8)
		)));
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
				Pair.of(7, new FakeGoToPotentialJobSite(speed)),
				Pair.of(10, new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false, Optional.of(VILLAGER_HAPPY))),
				Pair.of(10, new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of(VILLAGER_HAPPY))),
				Pair.of(10, new AssignProfessionFromJobSite()),
				Pair.of(10, new ResetProfession())
		);
	}
}
