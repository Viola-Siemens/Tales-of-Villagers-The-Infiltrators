package com.hexagram2021.infiltrators.common.blocks.entities;

import com.hexagram2021.infiltrators.common.register.InfBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AnalystTableBlockEntity extends BlockEntity {
	public AnalystTableBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(InfBlockEntities.ANALYST_TABLE.get(), blockPos, blockState);
	}
	
	
}
