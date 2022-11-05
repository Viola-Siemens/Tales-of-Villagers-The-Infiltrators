package com.hexagram2021.infiltrators.common;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
			
			Component component = new TranslatableComponent(this.doBookSpecialUse(sleepingVillagers.get(0)));
			
			if (!context.getLevel().isClientSide) {
				context.getPlayer().sendMessage(component, Util.NIL_UUID);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	protected abstract String doBookSpecialUse(Villager villager);
}
