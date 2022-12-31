package com.hexagram2021.infiltrators.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hexagram2021.infiltrators.common.util.RegistryHelper.getRegistryName;

public abstract class SpecialBookItem extends Item {
	public SpecialBookItem(Properties props) {
		super(props);
	}
	
	@Override @NotNull
	public InteractionResult useOn(UseOnContext context) {
		BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
		if(blockState.getBlock() instanceof BedBlock) {
			List<Villager> sleepingVillagers = context.getLevel().getEntitiesOfClass(Villager.class, new AABB(context.getClickedPos()), LivingEntity::isSleeping);
			if(sleepingVillagers.isEmpty()) {
				return InteractionResult.PASS;
			}
			
			ItemStack itemStack = context.getItemInHand();
			if(context.getPlayer() != null) {
				if (!context.getPlayer().getAbilities().instabuild) {
					context.getPlayer().getCooldowns().addCooldown(this, 16000);
					itemStack.shrink(1);
				}
			} else {
				itemStack.shrink(1);
			}
			
			if (!context.getLevel().isClientSide) {
				boolean fake = false;
				CompoundTag tag = context.getItemInHand().getTag();
				if(tag != null && tag.contains("Fake", Tag.TAG_BYTE)) {
					fake = tag.getBoolean("Fake");
				}
				Component component = Component.translatable(this.doBookSpecialUse((ServerPlayer)context.getPlayer(), sleepingVillagers.get(0), itemStack, fake));
				
				context.getPlayer().sendSystemMessage(component);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	protected abstract String doBookSpecialUse(ServerPlayer player, Villager villager, ItemStack itemStack, boolean fake);
	
	@Override
	public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
		ResourceLocation registryName = getRegistryName(this);
		if(registryName != null) {
			components.add(Component.translatable("desc." + registryName.getNamespace() + "." + registryName.getPath()).withStyle(ChatFormatting.GRAY));
		}
	}
}
