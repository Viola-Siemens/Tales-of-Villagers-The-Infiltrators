package com.hexagram2021.infiltrators.common.util.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class VillagerGetKilledTrigger extends SimpleCriterionTrigger<VillagerGetKilledTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(MODID, "villager_get_killed");
	
	@Override @NotNull
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override @NotNull
	public VillagerGetKilledTrigger.TriggerInstance createInstance(@NotNull JsonObject json,
																   @NotNull EntityPredicate.Composite entity,
																   @NotNull DeserializationContext context) {
		EntityPredicate.Composite killer = EntityPredicate.Composite.fromJson(json, "killer", context);
		return new VillagerGetKilledTrigger.TriggerInstance(entity, killer);
	}
	
	public void trigger(ServerPlayer player, LivingEntity killer) {
		LootContext killerContext = EntityPredicate.createContext(player, killer);
		this.trigger(player, instance -> instance.matches(killerContext));
	}
	
	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		private final EntityPredicate.Composite killer;
		
		public TriggerInstance(EntityPredicate.Composite entity, EntityPredicate.Composite killer) {
			super(VillagerGetKilledTrigger.ID, entity);
			this.killer = killer;
		}
		
		public boolean matches(LootContext killerContext) {
			return this.killer.matches(killerContext);
		}
		
		@Override @NotNull
		public JsonObject serializeToJson(@NotNull SerializationContext context) {
			JsonObject jsonobject = super.serializeToJson(context);
			jsonobject.add("killer", this.killer.toJson(context));
			return jsonobject;
		}
	}
}
