package com.hexagram2021.infiltrators.common.world.village;

import com.hexagram2021.infiltrators.common.entities.InfiltratorDataHolder;
import com.hexagram2021.infiltrators.common.items.SpecialBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class InfTrades {
	public static final int DEFAULT_SUPPLY = 12;
	public static final int COMMON_ITEMS_SUPPLY = 16;
	public static final int UNCOMMON_ITEMS_SUPPLY = 3;
	public static final int ONLY_SUPPLY_ONCE = 1;
	
	public static final int XP_LEVEL_1_SELL = 1;
	public static final int XP_LEVEL_1_BUY = 2;
	public static final int XP_LEVEL_2_SELL = 5;
	public static final int XP_LEVEL_2_BUY = 10;
	public static final int XP_LEVEL_3_SELL = 10;
	public static final int XP_LEVEL_3_BUY = 20;
	public static final int XP_LEVEL_4_SELL = 15;
	public static final int XP_LEVEL_4_BUY = 30;
	public static final int XP_LEVEL_5_TRADE = 30;
	
	static class EmeraldForItems implements VillagerTrades.ItemListing {
		private final Item item;
		private final int cost;
		private final int maxUses;
		private final int villagerXp;
		private final float priceMultiplier;
		
		public EmeraldForItems(ItemLike item, int cost, int maxUses, int xp) {
			this.item = item.asItem();
			this.cost = cost;
			this.maxUses = maxUses;
			this.villagerXp = xp;
			this.priceMultiplier = 0.05F;
		}
		
		@Override
		public MerchantOffer getOffer(@NotNull Entity entity, @NotNull Random random) {
			ItemStack itemstack = new ItemStack(this.item, this.cost);
			return new MerchantOffer(itemstack, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, this.priceMultiplier);
		}
	}
	
	static class ItemsForEmeralds implements VillagerTrades.ItemListing {
		private final Item item;
		private final int emeraldCost;
		private final int numberOfItems;
		private final int maxUses;
		private final int villagerXp;
		private final float priceMultiplier;
		
		public ItemsForEmeralds(ItemLike item, int cost, int itemCount, int maxUses, int xp) {
			this.item = item.asItem();
			this.emeraldCost = cost;
			this.numberOfItems = itemCount;
			this.maxUses = maxUses;
			this.villagerXp = xp;
			this.priceMultiplier = 0.05F;
		}
		
		@Override
		public MerchantOffer getOffer(@NotNull Entity entity, @NotNull Random random) {
			ItemStack itemStack = new ItemStack(this.item, this.numberOfItems);
			if(this.item instanceof SpecialBookItem) {
				CompoundTag compoundtag = new CompoundTag();
				compoundtag.putBoolean("Fake", entity instanceof InfiltratorDataHolder villager && villager.isInfiltrator());
				itemStack.setTag(compoundtag);
			}
			return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), itemStack, this.maxUses, this.villagerXp, this.priceMultiplier);
		}
	}
	
	static class PotionItemsForEmeralds implements VillagerTrades.ItemListing {
		private final Potion effect;
		private final int emeraldCost;
		private final int maxUses;
		private final int villagerXp;
		private final float priceMultiplier;
		
		public PotionItemsForEmeralds(Potion effect, int cost, int maxUses, int xp) {
			this.effect = effect;
			this.emeraldCost = cost;
			this.maxUses = maxUses;
			this.villagerXp = xp;
			this.priceMultiplier = 0.05F;
		}
		
		@Override
		public MerchantOffer getOffer(@NotNull Entity entity, @NotNull Random random) {
			ItemStack itemStack = new ItemStack(Items.POTION);
			return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), PotionUtils.setPotion(itemStack, this.effect), this.maxUses, this.villagerXp, this.priceMultiplier);
		}
	}
}
