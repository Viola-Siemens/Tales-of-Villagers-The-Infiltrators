package com.hexagram2021.infiltrators.common.entity.ai.behaviors;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.infiltrators.Infiltrators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FakeAcquirePoi extends Behavior<Villager> {
	private static final int BATCH_SIZE = 5;
	private static final int RATE = 20;
	public static final int SCAN_RANGE = 48;
	private final PoiType poiType;
	private final MemoryModuleType<GlobalPos> memoryToAcquire;
	private final boolean onlyIfAdult;
	private long nextScheduledStart;
	private final Long2ObjectMap<FakeAcquirePoi.JitteredLinearRetry> batchCache = new Long2ObjectOpenHashMap<>();
	
	public FakeAcquirePoi(PoiType poiType, MemoryModuleType<GlobalPos> moduleType, MemoryModuleType<GlobalPos> memoryToAcquire, boolean onlyIfAdult) {
		super(constructEntryConditionMap(moduleType, memoryToAcquire));
		this.poiType = poiType;
		this.memoryToAcquire = memoryToAcquire;
		this.onlyIfAdult = onlyIfAdult;
	}
	
	private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> moduleType, MemoryModuleType<GlobalPos> memoryToAcquire) {
		ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();
		builder.put(moduleType, MemoryStatus.VALUE_ABSENT);
		if (memoryToAcquire != moduleType) {
			builder.put(memoryToAcquire, MemoryStatus.VALUE_ABSENT);
		}
		
		return builder.build();
	}
	
	@Override
	protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull Villager villager) {
		if (this.onlyIfAdult && villager.isBaby()) {
			return false;
		}
		if (this.nextScheduledStart == 0L) {
			this.nextScheduledStart = villager.level.getGameTime() + (long)level.random.nextInt(RATE);
			return false;
		}
		return level.getGameTime() >= this.nextScheduledStart;
	}
	
	@Override
	protected void start(ServerLevel level, Villager villager, long tick) {
		this.nextScheduledStart = tick + RATE + (long)level.getRandom().nextInt(RATE);
		PoiManager poimanager = level.getPoiManager();
		this.batchCache.long2ObjectEntrySet().removeIf((entry) -> !entry.getValue().isStillValid(tick));
		Predicate<BlockPos> predicate = (blockPos) -> {
			FakeAcquirePoi.JitteredLinearRetry retry = this.batchCache.get(blockPos.asLong());
			if (retry == null) {
				return true;
			}
			if (!retry.shouldRetry(tick)) {
				return false;
			}
			retry.markAttempt(tick);
			return true;
		};
		Set<BlockPos> set = poimanager
				.findAllClosestFirst(this.poiType.getPredicate(), predicate, villager.blockPosition(), SCAN_RANGE, PoiManager.Occupancy.ANY)
				.limit(BATCH_SIZE).collect(Collectors.toSet());
		Path path = villager.getNavigation().createPath(set, this.poiType.getValidRange());
		if (path != null && path.canReach()) {
			BlockPos blockpos1 = path.getTarget();
			poimanager.getType(blockpos1).ifPresent((poiType) -> {
				takeWithoutAcquireTicket(poimanager, this.poiType.getPredicate(), (blockPos) -> blockPos.equals(blockpos1), blockpos1, 1);
				villager.getBrain().setMemory(this.memoryToAcquire, GlobalPos.of(level.dimension(), blockpos1));
				this.batchCache.clear();
				DebugPackets.sendPoiTicketCountPacket(level, blockpos1);
			});
		} else {
			for(BlockPos blockpos : set) {
				this.batchCache.computeIfAbsent(blockpos.asLong(), (v) -> new FakeAcquirePoi.JitteredLinearRetry(villager.level.random, tick));
			}
		}
	}
	
	@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
	private static Optional<BlockPos> takeWithoutAcquireTicket(PoiManager manager, Predicate<PoiType> checkPoi, Predicate<BlockPos> checkPos, BlockPos blockPos, int range) {
		return manager.getInRange(checkPoi, blockPos, range, PoiManager.Occupancy.ANY)
				.filter((record) -> checkPos.test(record.getPos())).findFirst().map(PoiRecord::getPos);
	}
	
	static class JitteredLinearRetry {
		private static final int MIN_INTERVAL_INCREASE = 40;
		private static final int INTERVAL_INCREASE = 40;
		private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
		private final Random random;
		private long previousAttemptTimestamp;
		private long nextScheduledAttemptTimestamp;
		private int currentDelay;
		
		JitteredLinearRetry(Random random, long attempt) {
			this.random = random;
			this.markAttempt(attempt);
		}
		
		public void markAttempt(long attempt) {
			this.previousAttemptTimestamp = attempt;
			int i = this.currentDelay + this.random.nextInt(INTERVAL_INCREASE) + MIN_INTERVAL_INCREASE;
			this.currentDelay = Math.min(i, MAX_RETRY_PATHFINDING_INTERVAL);
			this.nextScheduledAttemptTimestamp = attempt + (long)this.currentDelay;
		}
		
		public boolean isStillValid(long time) {
			return time - this.previousAttemptTimestamp < MAX_RETRY_PATHFINDING_INTERVAL;
		}
		
		public boolean shouldRetry(long time) {
			return time >= this.nextScheduledAttemptTimestamp;
		}
		
		@Override
		public String toString() {
			return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + "}";
		}
	}
}
