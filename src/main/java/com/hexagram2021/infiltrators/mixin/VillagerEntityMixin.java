package com.hexagram2021.infiltrators.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.hexagram2021.infiltrators.Infiltrators;
import com.hexagram2021.infiltrators.common.entities.InfiltratorDataHolder;
import com.hexagram2021.infiltrators.common.entities.ai.behaviors.FakeAcquirePoi;
import com.hexagram2021.infiltrators.common.entities.ai.behaviors.FakeGoToPotentialJobSite;
import com.hexagram2021.infiltrators.common.entities.ai.behaviors.FakeWorkAtPoi;
import com.hexagram2021.infiltrators.common.register.InfTriggers;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.hexagram2021.infiltrators.common.config.InfCommonConfig.*;
import static net.minecraft.world.entity.EntityEvent.VILLAGER_HAPPY;

@SuppressWarnings("unused")
@Mixin(Villager.class)
public abstract class VillagerEntityMixin implements InfiltratorDataHolder {
	
	boolean isInfiltrator;
	
	int possibilityBreakingWorkstation = INFILTRATOR_BREAK_WORKSTATION_MIN.get();
	
	boolean isImmuneToBadOmen = false;
	
	int nearWithIllagerTickCoolDown = 0;
	
	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/npc/VillagerType;)V", at = @At(value = "TAIL"))
	private void setIsInfiltrator(EntityType<? extends Villager> entityType, Level level, VillagerType villagerType, CallbackInfo ci) {
		this.isInfiltrator = ((Villager)(Object)this).getRandom().nextInt(100) < INFILTRATOR_SPAWN_POSSIBILITY.get();
	}
	
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
	
	@Inject(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putInt(Ljava/lang/String;I)V", ordinal = 1, shift = At.Shift.BEFORE))
	private void addIsInfiltrator(CompoundTag nbt, CallbackInfo ci) {
		nbt.putBoolean("IsInfiltrator", this.isInfiltrator);
		if(this.isInfiltrator) {
			nbt.putInt("PossibilityBreakingWorkstation", this.possibilityBreakingWorkstation);
			nbt.putBoolean("IsImmuneToBadOmen", this.isImmuneToBadOmen);
			nbt.putInt("NearWithIllagerTickCoolDown", this.nearWithIllagerTickCoolDown);
		}
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;setCanPickUpLoot(Z)V", shift = At.Shift.BEFORE))
	private void readIsInfiltrator(CompoundTag nbt, CallbackInfo ci) {
		if (nbt.contains("IsInfiltrator")) {
			this.isInfiltrator = nbt.getBoolean("IsInfiltrator");
			if(this.isInfiltrator) {
				this.possibilityBreakingWorkstation = nbt.getInt("PossibilityBreakingWorkstation");
				this.isImmuneToBadOmen = nbt.getBoolean("IsImmuneToBadOmen");
				this.nearWithIllagerTickCoolDown = nbt.getInt("NearWithIllagerTickCoolDown");
			}
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
		if(!((InfiltratorDataHolder)instance).isInfiltrator()) {
			instance.releasePoi(memoryModuleType);
		}
	}
	@Redirect(method = "releaseAllPois", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;releasePoi(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)V", ordinal = 2))
	public void doNotReleasePotentialJobSiteIfInfiltrator(Villager instance, MemoryModuleType<GlobalPos> memoryModuleType) {
		if(!((InfiltratorDataHolder)instance).isInfiltrator()) {
			instance.releasePoi(memoryModuleType);
		}
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
		AABB aabb = new AABB(current.getX() - 20.0D, current.getY() - 8.0D, current.getZ() - 20.0D, current.getX() + 20.0D, current.getY() + 8.0D, current.getZ() + 20.0D);
		if(!current.level.getEntities(current, aabb, entity -> entity instanceof Villager && ((InfiltratorDataHolder)entity).isInfiltrator()).isEmpty() &&
				current.getRandom().nextInt(100) < INFILTRATOR_BREAK_OTHERS_WORK.get()) {
			ci.cancel();
		}
	}
	
	@Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/AbstractVillager;customServerAiStep()V"))
	protected void tryCauseRaidAndChangeProfession(CallbackInfo ci) {
		Villager current = (Villager)(Object)this;
		
		long timeOfDay = current.level.dayTime() % 24000L;
		
		if(this.isInfiltrator()) {
			//cause raid
			if(timeOfDay == INFILTRATOR_CAUSE_RAID_TICK.get()) {
				if(this.isImmuneToBadOmen) {
					this.isImmuneToBadOmen = false;
				} else if(current.getRandom().nextInt(100) < INFILTRATOR_POSSIBILITY_CAUSE_RAID.get()) {
					Player player = current.level.getNearestPlayer(current, 32.0D);
					if(player != null) {
						player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 20, 5));
					}
				}
			}
			//change profession
			if(timeOfDay == INFILTRATOR_CHANGE_PROFESSION_TICK.get()) {
				if(current.getRandom().nextInt(100) < INFILTRATOR_POSSIBILITY_CHANGE_PROFESSION.get()) {
					AABB aabb = new AABB(current.getX() - 32.0D, current.getY() - 12.0D, current.getZ() - 32.0D, current.getX() + 32.0D, current.getY() + 12.0D, current.getZ() + 32.0D);
					//TODO: choose randomly
					current.level.getEntities(current, aabb, entity -> {
						if(entity instanceof Villager) {
							if(((InfiltratorDataHolder)entity).isInfiltrator()) {
								return current.getRandom().nextBoolean();
							}
							return true;
						}
						return false;
					}).stream().map(entity -> (Villager)entity).findAny().ifPresent(villager -> {
						villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).ifPresentOrElse(
								globalPos -> current.getBrain().setMemory(MemoryModuleType.JOB_SITE, globalPos),
								() -> current.getBrain().eraseMemory(MemoryModuleType.JOB_SITE)
						);
						current.setVillagerData(current.getVillagerData().setProfession(villager.getVillagerData().getProfession()));
						current.refreshBrain((ServerLevel)current.level);
						Infiltrators.LOGGER.debug(current.getDisplayName().getString() + " -> " + villager.getDisplayName().getString());
						this.setFakeMerchantOffers();
					});
				}
			}
			//join raid
			if(this.nearWithIllagerTickCoolDown <= 0) {
				AABB aabb = new AABB(current.getX() - 16.0D, current.getY() - 8.0D, current.getZ() - 16.0D, current.getX() + 16.0D, current.getY() + 8.0D, current.getZ() + 16.0D);
				if(!current.level.getEntities(current, aabb, entity -> entity instanceof AbstractIllager).isEmpty()) {
					this.nearWithIllagerTickCoolDown = INFILTRATOR_CONVERT_DELAY.get();
					if(current.getRandom().nextInt(100) < INFILTRATOR_CONVERT_ILLAGER_AND_JOIN_RAID_POSSIBILITY.get()) {
						current.releaseAllPois();
						AbstractIllager illager = null;
						switch (current.getRandom().nextInt(4)) {
							case 0 -> illager = current.convertTo(EntityType.EVOKER, false);
							case 1 -> {
								illager = current.convertTo(EntityType.ILLUSIONER, false);
								if(illager != null) {
									ItemStack weapon = EnchantmentHelper.enchantItem(illager.getRandom(), new ItemStack(Items.BOW), 10, true);
									if(weapon.getEnchantmentLevel(Enchantments.POWER_ARROWS) == 0) {
										weapon.enchant(Enchantments.POWER_ARROWS, illager.getRandom().nextInt(3) + 3);
									}
									illager.setItemSlot(EquipmentSlot.MAINHAND, weapon);
								}
							}
							case 2 -> {
								illager = current.convertTo(EntityType.VINDICATOR, false);
								if(illager != null) {
									ItemStack weapon = EnchantmentHelper.enchantItem(illager.getRandom(), new ItemStack(Items.IRON_AXE), 10, true);
									if(weapon.getEnchantmentLevel(Enchantments.SHARPNESS) == 0) {
										weapon.enchant(Enchantments.SHARPNESS, illager.getRandom().nextInt(3) + 3);
									}
									illager.setItemSlot(EquipmentSlot.MAINHAND, weapon);
								}
							}
							case 3 -> {
								illager = current.convertTo(EntityType.PILLAGER, false);
								if(illager != null) {
									ItemStack weapon = EnchantmentHelper.enchantItem(illager.getRandom(), new ItemStack(Items.CROSSBOW), 10, true);
									if(weapon.getEnchantmentLevel(Enchantments.QUICK_CHARGE) == 0) {
										weapon.enchant(Enchantments.QUICK_CHARGE, illager.getRandom().nextInt(2) + 2);
									}
									illager.setItemSlot(EquipmentSlot.MAINHAND, weapon);
									
									int count = illager.getRandom().nextInt(8) - 1;
									if(count > 0) {
										ItemStack fireworks = new ItemStack(Items.FIREWORK_ROCKET, count);
										CompoundTag nbt = fireworks.getOrCreateTagElement(FireworkRocketItem.TAG_FIREWORKS);
										nbt.putByte(FireworkRocketItem.TAG_FLIGHT, (byte) 2);
										CompoundTag explosion = new CompoundTag();
										explosion.putByte(FireworkRocketItem.TAG_EXPLOSION_TYPE, (byte) FireworkRocketItem.Shape.LARGE_BALL.getId());
										explosion.putIntArray(FireworkRocketItem.TAG_EXPLOSION_COLORS, Collections.singletonList(DyeColor.GRAY.getFireworkColor()));
										explosion.putBoolean(FireworkRocketItem.TAG_EXPLOSION_TRAIL, illager.getRandom().nextBoolean());
										explosion.putBoolean(FireworkRocketItem.TAG_EXPLOSION_FLICKER, illager.getRandom().nextBoolean());
										ListTag list = new ListTag();
										list.add(explosion);
										nbt.put(FireworkRocketItem.TAG_EXPLOSIONS, list);
										illager.setItemSlot(EquipmentSlot.OFFHAND, fireworks);
									}
								}
							}
						}
						if(illager != null) {
							illager.setCanJoinRaid(true);
							illager.setPersistenceRequired();
						}
					}
				}
			} else {
				this.nearWithIllagerTickCoolDown -= 1;
			}
		}
	}
	
	@Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;releaseAllPois()V", shift = At.Shift.BEFORE))
	public void tryTriggerAdvancement(DamageSource damageSource, CallbackInfo ci) {
		if(this.isInfiltrator) {
			if(damageSource.getEntity() instanceof LivingEntity livingEntity) {
				Villager current = (Villager)(Object)this;
				List<Player> players = current.level.getNearbyPlayers(TargetingConditions.forNonCombat().ignoreLineOfSight().range(64.0D), current, current.getBoundingBox().inflate(48.0D, 32.0D, 48.0D));
				
				for (Player player: players) {
					if(player instanceof ServerPlayer serverPlayer) {
						InfTriggers.VILLAGER_GET_KILLED.trigger(serverPlayer, livingEntity);
					}
				}
			}
		}
	}
	
	@Inject(method = "wantsToSpawnGolem", at = @At(value = "HEAD"), cancellable = true)
	public void dontSpawnGolemIfIsInfiltrator(long time, CallbackInfoReturnable<Boolean> cir) {
		if(this.isInfiltrator) {
			cir.setReturnValue(Boolean.FALSE);
			cir.cancel();
		}
	}
	
	@Override
	public boolean isInfiltrator() {
		return this.isInfiltrator;
	}
	
	@Override
	public void resetPossibilityBreakingWorkstation() {
		this.possibilityBreakingWorkstation = INFILTRATOR_BREAK_WORKSTATION_MIN.get();
	}
	
	@Override
	public int getPossibilityBreakingWorkstation() {
		return this.possibilityBreakingWorkstation;
	}
	
	@Override
	public void increasePossibilityBreakingWorkstation() {
		if(this.possibilityBreakingWorkstation < INFILTRATOR_BREAK_WORKSTATION_MAX.get()) {
			this.possibilityBreakingWorkstation += 1;
		}
	}
	
	@Override
	public void setImmuneToBadOmen() {
		this.isImmuneToBadOmen = true;
	}
	
	private void setFakeMerchantOffers() {
		Villager current = (Villager)(Object)this;
		VillagerProfession profession = current.getVillagerData().getProfession();
		
		int level = current.getVillagerData().getLevel();
		
		current.setOffers(new MerchantOffers());
		if(profession == VillagerProfession.NITWIT || profession == VillagerProfession.NONE) {
			return;
		}
		Int2ObjectMap<VillagerTrades.ItemListing[]> trades = VillagerTrades.TRADES.get(profession);
		for(int i = 1; i <= level; ++i) {
			current.addOffersFromItemListings(current.getOffers(), trades.get(i), 2);
		}
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
						Pair.of(new StrollToPoi(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5)
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
				Pair.of(0, new ValidateNearbyPoi(villagerprofession.acquirableJobSite(), MemoryModuleType.JOB_SITE)),
				Pair.of(0, new ValidateNearbyPoi(villagerprofession.acquirableJobSite(), MemoryModuleType.POTENTIAL_JOB_SITE)),
				Pair.of(1, new MoveToTargetSink()),
				Pair.of(3, new LookAndFollowTradingPlayerSink(speed)),
				Pair.of(5, new GoToWantedItem<>(speed, false, 4)),
				Pair.of(6, new FakeAcquirePoi(villagerprofession.acquirableJobSite(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true)),
				Pair.of(7, new FakeGoToPotentialJobSite(speed)),
				Pair.of(10, new AcquirePoi((poiType) -> poiType.is(PoiTypes.HOME), MemoryModuleType.HOME, false, Optional.of(VILLAGER_HAPPY))),
				Pair.of(10, new AcquirePoi((poiType) -> poiType.is(PoiTypes.MEETING), MemoryModuleType.MEETING_POINT, true, Optional.of(VILLAGER_HAPPY))),
				Pair.of(10, new AssignProfessionFromJobSite()),
				Pair.of(10, new ResetProfession())
		);
	}
}
