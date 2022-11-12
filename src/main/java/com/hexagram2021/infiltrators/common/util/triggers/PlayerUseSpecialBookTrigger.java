package com.hexagram2021.infiltrators.common.util.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;

import static com.hexagram2021.infiltrators.Infiltrators.MODID;

public class PlayerUseSpecialBookTrigger extends SimpleCriterionTrigger<PlayerUseSpecialBookTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(MODID, "player_use_special_book");
	
	@Override @NotNull
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override @NotNull
	public PlayerUseSpecialBookTrigger.TriggerInstance createInstance(@NotNull JsonObject json,
																	  @NotNull EntityPredicate.Composite entity,
																	  @NotNull DeserializationContext context) {
		EntityPredicate.Composite villager = EntityPredicate.Composite.fromJson(json, "villager", context);
		ItemPredicate book = ItemPredicate.fromJson(json.get("book"));
		return new PlayerUseSpecialBookTrigger.TriggerInstance(entity, villager, book);
	}
	
	public void trigger(ServerPlayer player, Villager villager, ItemStack book) {
		LootContext villagerContext = EntityPredicate.createContext(player, villager);
		this.trigger(player, instance -> instance.matches(villagerContext, book));
	}
	
	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		private final EntityPredicate.Composite villager;
		private final ItemPredicate book;
		
		public TriggerInstance(EntityPredicate.Composite entity, EntityPredicate.Composite villager, ItemPredicate book) {
			super(PlayerUseSpecialBookTrigger.ID, entity);
			this.villager = villager;
			this.book = book;
		}
		
		public boolean matches(LootContext villagerContext, ItemStack itemStack) {
			return this.villager.matches(villagerContext) && this.book.matches(itemStack);
		}
		
		@Override @NotNull
		public JsonObject serializeToJson(@NotNull SerializationContext context) {
			JsonObject jsonobject = super.serializeToJson(context);
			jsonobject.add("villager", this.villager.toJson(context));
			jsonobject.add("book", this.book.serializeToJson());
			return jsonobject;
		}
	}
}
